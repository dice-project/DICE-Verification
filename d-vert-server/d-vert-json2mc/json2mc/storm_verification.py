'''
Created on Jan 6, 2017

@author: francesco
'''

from dia_verification import VerificationTask
from dia_verification import VerificationResult
from dia_verification import VerificationTrace
from dia_verification_zot import ZotResult
from dia_verification_zot import ZotTrace

import config as cfg

import utils as utils

import jinja2
import json
import os
import itertools
import string

import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from matplotlib.font_manager import FontProperties
from __builtin__ import staticmethod


def get_y_max(cur_y_max, var_list, component_id, records):
    y_max = cur_y_max
    for var in var_list:
        var_id = string.upper(var["var_prefix"])+component_id
        y_max = max([int(r) for r in records[var_id]] + [y_max])
    return y_max


def plot_vars_from_list(steps, var_list, component_id, records,
                        bool_set, var_styles_dict={}):
    step_list = range(steps + 1)
    for var in var_list:
        var_id = string.upper(var["var_prefix"])+component_id
        var_style = var_styles_dict.get(var_id, var["linestyle"])
        var_lw = var["linewidth"]
        var_msize = var["markersize"]
        # print var_id, '\t\t', records[var_id]
        if var_id not in bool_set:
            # print 'Plotting: ',var_id, "style:", var_style
            plt.plot(step_list, records[var_id], var_style, label=var_id,
                     linewidth=var_lw, markersize=var_msize)
        else:  # works only for TAKE_BX
            bool_series = \
                [records['R_PROCESS_' + var_id.split('_')[-1]][j] if j
                    in records[var_id]else 0 for j in range(steps + 1)]
            plt.plot(step_list, bool_series, var_style, label=var_id,
                     linewidth=var_lw, markersize=var_msize)


def uppercase_ids(context):
    for s in context["topology"]["spouts"]:
        s["id"] = s["id"].upper()
    for b in context["topology"]["bolts"]:
        b["id"] = b["id"].upper()
        b["subs"] = [c.upper() for c in b["subs"]]
    context["verification_params"]["periodic_queues"] =\
        [q.upper() for q in
         context["verification_params"]["periodic_queues"]]
    context["verification_params"]["strictly_monotonic_queues"] =\
        [q.upper() for q in
         context["verification_params"]["strictly_monotonic_queues"]]
    return context


class StormVerificationTask(VerificationTask):
    '''
    Class containing all the specific code needed to run verification
    on Storm topologies with D-VerT
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
        if template_path is None:
            template_path = os.path.join(VerificationTask.base_dir,
                                         "templates",
                                         "storm_tl_template.lisp")
        if graphical_conf_path is None:
            graphical_conf_path = os.path.join(VerificationTask.base_dir,
                                               "visual",
                                               "storm_plot_settings.json")
        self.verification_result = VerificationResult()
        self.output_trace = VerificationTrace()
        self.display = display
        self.graphical_conf_path = os.path.abspath(graphical_conf_path)
        self.figure_path = None
        if not plotonly_folder:
            self.result_dir = None  # to be assigned in launch_verification
            self.output_dir = os.path.abspath(output_dir)
            self.app_name = context["app_name"]
            self.app_dir = os.path.join(self.output_dir, self.app_name)
            tmp_plugin = context["verification_params"]["plugin"]
            # temporary assignment to cope with plugins list defined in eclipse client
            self.plugin = tmp_plugin[0] if isinstance(tmp_plugin, list) else tmp_plugin
            context["verification_params"]["plugin"] = self.plugin
            self.template_path = os.path.abspath(template_path)
            print "opening", self.template_path
            with open(self.template_path, "r") as tmp:
                self.template = jinja2.Template(tmp.read())
#            self.dag = StormDAG(context)
#            self.dag.label_graph()
#            print self.dag.g.nodes(data=True)
#            gr = DAGRenderer(self.dag.g)
#            gr.render(os.path.join(self.app_dir, self.app_name + ".gv"), True)
#            self.context = self.dag.json_context
            self.context = uppercase_ids(context)
        else:
            self.result_dir = self.app_dir = os.path.abspath(plotonly_folder)
            self.app_name = plotonly_folder.split(os.path.sep)[-3]
            print "opening {}".format(os.path.join(self.result_dir, 'conf',
                                      'copy_of_'+self.app_name+'.json'))
            with open(os.path.join(self.result_dir, 'conf',
                                   'copy_of_'+self.app_name+'.json'),
                      "r") as c_file:
                self.context = json.load(c_file)
            self.process_zot_results(self.result_dir,
                                     os.path.join(self.result_dir,
                                                  self.hist_file))

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

    def plot_trace(self):
        """Plot the trace provided in the records dictionary."""
        print "Opening {}".format(self.graphical_conf_path)
        with open(self.graphical_conf_path) as settings_file:
            settings = json.load(settings_file)
        file_name = self.app_name if self.app_name is not None else "plot"
        topology = self.context["topology"]
        strictly_monotonic_ids = self.context["verification_params"]["strictly_monotonic_queues"]
        bolts_to_plot = topology["bolts"] if len(strictly_monotonic_ids) == 0 \
            else [b for b in topology["bolts"] if b["id"] in strictly_monotonic_ids]
        steplist = range(self.output_trace.time_bound + 1)
        rows, columns = utils.get_grid_dimensions(len(bolts_to_plot))
        #  first round to get the maximum y value
        my_dpi = 96
        plt.figure(figsize=(1460/my_dpi, 900/my_dpi), dpi=my_dpi)
        styles_list = self.get_plot_styles_list(settings)
        timestamp_indexes = range(len(bolts_to_plot) + 1 - columns,
                                  len(bolts_to_plot) +1)
        print "timestamp_indexes: {}".format(timestamp_indexes)
        i = 1
        y_max = 1
        # get the maximum "Y" value to be displayed across all plots
        for b in bolts_to_plot:
            y_max = get_y_max(y_max, settings["bolt_vars"], b["id"],
                              self.output_trace.records)
            for s in b["subs"]:
                y_max = get_y_max(y_max, settings["subs_vars"], s,
                                  self.output_trace.records)
        # plot variables' values
        for b in bolts_to_plot:  # TODO checki if complete
            plt.subplot(rows, columns, i)
            plot_vars_from_list(self.output_trace.time_bound,
                                settings["bolt_vars"],
                                b["id"],
                                self.output_trace.records,
                                self.output_trace.bool_set)
            # get list of all varables to plot to couple them with style
            subs_vars_list = [x[0]["var_prefix"]+x[1] for x in
                              itertools.product(settings["subs_vars"],
                                                b["subs"])]
            vars_styles_dict = {}
            if (styles_list is not None and
                    len(subs_vars_list) <= len(styles_list)):
                vars_styles_dict = dict(zip(subs_vars_list, styles_list))
            for s in b["subs"]:
                plot_vars_from_list(self.output_trace.time_bound,
                                    settings["subs_vars"],
                                    s,
                                    self.output_trace.records,
                                    self.output_trace.bool_set,
                                    vars_styles_dict)
            if i == 1:
                plt.title("verification time: {}\n\n{}"
                          .format(str(self.verification_result
                                      .verification_time),
                                  b["id"]),
                          fontsize=cfg.PLOT_CFG["plot_titles"]["fontsize"])
            else:
                plt.title(b["id"], fontsize=cfg.PLOT_CFG["plot_titles"]["fontsize"])
    #        plt.suptitle(file_name)
            # limit the y axis to the maximum value present across the plots
            plt.ylim([0, y_max + 1])
            # print records['TOTALTIME']
            rounded_totaltime = \
                map(lambda t: round(float(t.strip('?')), 2),
                    self.output_trace.records['TOTALTIME'])
            plt.ylabel('#tuples')
            if i in timestamp_indexes:
                plt.xticks(steplist, rounded_totaltime, rotation=45)
                plt.xlabel('TOTALTIME', fontsize=22)
            else:
                plt.xticks(steplist, ['' for s in steplist])
            fontP = FontProperties()
            fontP.set_size('x-small')
            plt.legend(prop=fontP, loc='upper left')
    #        plt.legend(loc='upper left')
            plt.grid()
            # highlight in red the looping suffix of the output trace
            plt.axvspan(self.output_trace.records['LOOP'],
                        self.output_trace.time_bound,
                        color='red',
                        alpha=0.3)
            i += 1  # end for
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
