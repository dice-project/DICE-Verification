'''
Created on Jan 6, 2017

@author: francesco
'''

from dia_verification import VerificationTask
from dia_verification import VerificationTrace
from dia_verification import VerificationResult
from dagrenderer import DAGRenderer
from dia_verification_zot import ZotResult
from dia_verification_zot import ZotTrace
import utils as utils
import networkx as nx

# from subprocess import Popen, PIPE
import jinja2
# import shutil
import json
import os
import itertools
import string

import math
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from matplotlib.font_manager import FontProperties


class SparkVerificationTask(VerificationTask):
    '''
    classdocs
    '''
    def __init__(self,
                 template_path=None,
                 context=None,
                 output_dir=os.path.join(VerificationTask.base_dir,
                                         "output_dir"),
                 plotonly_folder=None,
                 display=False,
                 graphical_conf_path=None):
        '''
        Constructor
        '''
        print "#### name ###", __name__

        if template_path is None:
            template_path = os.path.join(VerificationTask.base_dir,
                                         "templates",
                                         "spark_tl_template.lisp")
        if graphical_conf_path is None:
            graphical_conf_path = os.path.join(VerificationTask.base_dir,
                                               "visual",
                                               "spark_plot_settings.json")
        self.verification_result = VerificationResult()
        self.output_trace = VerificationTrace()
        self.display = display
        self.graphical_conf_path = os.path.abspath(graphical_conf_path)
        if not plotonly_folder:
            self.result_dir = None  # to be assigned in launch_verification
            self.output_dir = os.path.abspath(output_dir)
            self.app_name = context["app_name"]
            self.app_dir = os.path.join(self.output_dir, self.app_name)

            # temporary assignment to cope with plugins list defined in eclipse client
            tmp_plugin = context["verification_params"]["plugin"]
            self.plugin = tmp_plugin[0] if isinstance(tmp_plugin, list) else tmp_plugin
            context["verification_params"]["plugin"] = self.plugin

            self.template_path = os.path.abspath(template_path)
            print "opening {}".format(self.template_path)
            with open(self.template_path, "r") as tmp:
                self.template = jinja2.Template(tmp.read())
            self.dag = SparkDAG(context)
            self.dag.label_graph()
            print self.dag.g.nodes(data=True)
            gr = DAGRenderer(self.dag.g)
            gr.render(os.path.join(self.app_dir, self.app_name + ".gv"), self.display)
            self.context = self.dag.json_context
        else:
            self.result_dir = self.app_dir = os.path.abspath(plotonly_folder)
            self.app_name = plotonly_folder.split(os.path.sep)[-3]
            print "opening {}".format(os.path.join(self.result_dir, "conf",
                                      'copy_of_'+self.app_name+'.json'))
            with open(os.path.join(self.result_dir, "conf",
                                   'copy_of_'+self.app_name+'.json'),
                      "r") as c_file:
                self.context = json.load(c_file)
            self.process_zot_results(self.result_dir,
                                     os.path.join(self.result_dir,
                                                  self.hist_file))
        self.starting_context = self.context

    def parse_zot_trace(self, file_path=None):
        """Create new ZotTrace object after parsing history file."""
        print 'parse_zot_trace({})'.format(file_path)
        if not file_path:
            file_path = os.path.join(self.result_dir, self.hist_file)
        self.output_trace = ZotTrace(file_path)

    def process_zot_results(self, result_dir=None, trace_file=None):
        '''
        get results from file and, if SAT, parse and plot the trace
        '''
        if not result_dir:
            result_dir = self.result_dir
        if not trace_file:
            trace_file = self.hist_file
        print 'process_zot_result({},{})'.format(result_dir, trace_file)
        self.verification_result = ZotResult(result_dir)
        if self.verification_result.outcome == 'sat':
            self.parse_zot_trace(os.path.join(result_dir, trace_file))

    @staticmethod
    def get_plot_styles_list(settings):
        # ONELINE  return [''.join(t) for t in list(itertools.product(
        # settings["markers"], setting["line_styles"],setting["colors"]))]
        markers = settings["markers"]
        line_styles = settings["line_styles"]
        colors = settings["colors"]
        tuples_list = list(itertools.product(markers, line_styles, colors))
        strings_list = [''.join(t) for t in tuples_list]
        # print strings_list
        return strings_list

    @staticmethod  # SPARK-SPECIFIC
    def get_y_max(cur_y_max, var_list, records):
        y_max = cur_y_max
        for var_id in var_list:
            y_max = max([int(r) for r in records[var_id]] + [y_max])
        return y_max

    @staticmethod
    def plot_vars_from_list(num_steps, var_list, component_id, records,
                            bool_set, var_styles_dict={}):
        step_list = range(num_steps + 1)
        for var in var_list:
            var_id = string.upper(var["var_prefix"])+component_id
            var_style = var_styles_dict.get(var_id, var["linestyle"])
            var_lw = var["linewidth"]
            var_msize = var["markersize"]
            # print var_id, '\t\t', records[var_id]
            if var_id not in bool_set:
                # print 'Plotting: {}: {}'.format(var_id, records[var_id])
                plt.plot(step_list, [int(x) for x in records[var_id]], var_style, label=var_id,
                         linewidth=var_lw, markersize=var_msize)
            else:  # works only for START_T_X
                bool_series = \
                    [records['RUN_TC_' + var_id.split('_')[-1]][j] if j
                        in records[var_id]else 0 for j in step_list]
                plt.plot(step_list, bool_series, var_style, label=var_id,
                         linewidth=var_lw, markersize=var_msize)

    def plot_trace(self):
        """Plot the trace provided in the records dictionary."""
        print "Opening {}".format(self.graphical_conf_path)
        with open(self.graphical_conf_path) as settings_file:
            settings = json.load(settings_file)
        file_name = self.app_name if self.app_name is not None else "plot"
        steplist = range(self.output_trace.time_bound + 1)
        num_rows = 1
        columns = 1   # first round to get the maximum y value
        my_dpi = 96
        plt.figure(figsize=(1460/my_dpi, 900/my_dpi), dpi=my_dpi)
        styles_list = self.get_plot_styles_list(settings)
#        timestamp_indexes = range((num_rows-1)*columns,
#                                    num_rows*columns)+[len(topology["bolts"])]
        i = 1
        y_max = 1
        # plot variables' values
        stages = ["S"+str(s) for s in self.context["stages"]]
        stage_vars_list = [x[0]["var_prefix"]+x[1]
                           for x in itertools.product(settings["stage_vars"],
                                                      stages)]
        global_vars_list = [x["var_prefix"]
                            for x in settings["global_vars"]]
#        for j in self.context["jobs"]:
        plt.subplot(num_rows, columns, i)
        self.plot_vars_from_list(self.output_trace.time_bound,
                                 settings["global_vars"],
                                 '',
                                 self.output_trace.records,
                                 self.output_trace.bool_set)
        y_max = self.get_y_max(y_max, stage_vars_list + global_vars_list,
                               self.output_trace.records)
        vars_styles_dict = {}
        if styles_list is not None\
                and len(stage_vars_list) <= len(styles_list):
            vars_styles_dict = dict(zip(stage_vars_list, styles_list))
        for s in stages:
            self.plot_vars_from_list(self.output_trace.time_bound,
                                     settings["stage_vars"],
                                     s,
                                     self.output_trace.records,
                                     self.output_trace.bool_set,
                                     vars_styles_dict)
        if i == 1:
            plt.title("{}\n(verification time: {})"
                      .format(self.app_name,
                              str(self.verification_result.verification_time)),
                      fontsize=18)
        else:
            plt.title(s["id"] + ' profile', fontsize=18)
#        plt.suptitle(file_name)
        # limit the y axis to the maximum value present across the plots
        plt.ylim([0, math.ceil(y_max * 1.1)])
        # print records['TOTALTIME']
        rounded_totaltime = \
            map(lambda t: round(float(t.strip('?')), 3),
                self.output_trace.records['TOTALTIME'])
        plt.ylabel('#cores')
        plt.xticks(steplist, rounded_totaltime, rotation=45)
        plt.xlabel('TOTALTIME', fontsize=18)
        fontP = FontProperties()
        fontP.set_size('small')
        plt.legend(prop=fontP, loc='upper right')
        plt.grid()
        # gets the index of the first timestamp >= deadline
        deadline_timestamp = next(iter([t for t in zip(range(self.output_trace.time_bound),
                                                       self.output_trace.records['TOTALTIME'])
                                        if float(t[1].strip('?')) >= self.context["deadline"]]), None)
        if deadline_timestamp: # it could be that all of the
            plt.axvspan(deadline_timestamp[0],
                        self.output_trace.time_bound,
                        color='red', alpha=0.3)
        # i += 1 end for
    #    plt.tight_layout()
        time_str = self.verification_result.timestamp_str
        utils.make_sure_path_exists(os.path.join(self.result_dir, "imgs"))
        self.figure_path = os.path.join(self.result_dir, "imgs",
                                       "{}_{}.png".format(file_name,
                                                          time_str))
        print "saving figure in {}".format(self.figure_path)
        plt.savefig(self.figure_path,
                    facecolor='w', edgecolor='k')
        if self.display:
            plt.show()
        return os.path.abspath(self.figure_path)

    def __str__(self):
        return '''Spark Verification Task:
                template_path: {} \n
                template: {} \n
                context: {} \n
                output_dir: {} \n
                result_dir: {} \n
                app_name: {} \n
                app_dir: {} \n
                plugins: {} \n
                verification_result {} \n
                output_trace: {} \n
                display: {}
                '''.format(self.template_path,
                           self.template,
                           self.context,
                           self.output_dir,
                           self.result_dir,
                           self.app_name,
                           self.app_dir,
                           self.plugin,
                           self.verification_result,
                           self.output_trace,
                           self.display)


class SparkDAG(object):
    '''
    Direct Acyclic Graph representing the execution plan of a Spark Job

    Attributes:
    g: graph object consisting in a networkx DiGraph instance
    json_context: original JSON data structure from which the dag is built
    labels: set of the labels which are sufficient to represent the DAG
    carry_on_labels: set of labels that each node in the graph can use to label
    its own successors

    '''

    def generateNewLabel(self):
        if self.labels:
            new_label = max(self.labels) + 1
        else:
            new_label = 0
        self.labels.add(new_label)
        print "New Label", new_label
        return new_label

    def __init__(self, json_context):
        '''
        Constructor
        '''
        self.json_context = json_context
        self.g = nx.DiGraph()
        self.labels = set()
        self.carry_on_labels = {}
        self.is_labeled = False
        # add nodes to graph
        for k, v in self.json_context["stages"].iteritems():
            self.g.add_node(k)
            if "skipped" in v:
                if v["skipped"]:
                    print "skipped"
                    self.g.node[k]["skipped"] = True
                    self.g.node[k]['fillcolor'] = 'red'
                    self.json_context["stages"][k]["duration"] = 10.0
                    self.json_context["stages"][k]["numtask"] = 1
                else:
                    self.g.node[k]["skipped"] = False
                    self.g.node[k]['fillcolor'] = 'white'
            else:
                self.g.node[k]["skipped"] = False
                self.g.node[k]['fillcolor'] = 'white'
            # remove possible duplicates in parentIds
            self.json_context["stages"][k]["parentsIds"] =\
                list(set(v["parentsIds"]))
            # add predecessors
            for p in self.json_context["stages"][k]["parentsIds"]:
                self.g.add_edge(str(p), str(k))
            # initialize carry_on_labels to empty set
            self.carry_on_labels[k] = set()
        # identify set of starting nodes (those without predecessors)
        self.starting_nodes = [x for x in self.g.nodes()
                               if not self.g.predecessors(x)]
        for n in self.starting_nodes:
            label = self.generateNewLabel()
            self.g.node[n]["label"] = label
            self.carry_on_labels[n].add(label)

    def label_graph(self):
        '''
        visits the DAG following the precedences among stages
        and labels each node in such a way that labels can be re-used
        for stages whose execution is mutually exclusive
        '''
        visited, queue = set(), self.starting_nodes
        while queue:
            vertex = queue.pop(0)
            if vertex not in visited:
                print "Visiting {}".format(vertex)
                visited.add(vertex)
                succ = self.g.successors(vertex)
                finished_labels = False
                while succ:
                    s = succ.pop()
                    predec_s = set(self.g.predecessors(s))
                    # if all the predecessors have already been visited
                    if not (predec_s - visited):
                        queue.append(s)
                        print "labeling {}".format(s)
                        if self.carry_on_labels[vertex]:
                            l = self.carry_on_labels[vertex].pop()
                        else:
                            labelling_nodes = [x for x in predec_s
                                               if self.carry_on_labels[x]]
                            if labelling_nodes:
                                l = (self.carry_on_labels[labelling_nodes[0]]
                                     .pop())
                            else:
                                l = self.generateNewLabel()
                                finished_labels = True
                        # if s still has to be labeled, then label it
                        if "label" not in self.g.node[s]:
                            print "({}) -> {}".format(s, l)
                            self.g.node[s]["label"] = l
                            self.carry_on_labels[s].add(l)
                        if not finished_labels:
                            self.carry_on_labels[s].add(l)
                            # if there are still labels to be assigned,re-apply
                            # the procedure on all successors of vertex
                            if not succ:
                                succ = self.g.successors(vertex)
#                queue.extend(set(self.g[vertex]) - visited)
#                for n in self.g.successors(vertex):
#                    # if all the predecessors have been already visited
#                    if not (set(self.g.predecessors(n)) - visited):
#                        queue.append(n)
        # copy labels into the JSON context (should be improved)
        self.json_context["labels"] = list(self.labels)
        for i in self.json_context["stages"]:
            self.json_context["stages"][i]["label"] = self.g.node[i]["label"]
        self.is_labeled = True
        return visited
