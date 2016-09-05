#!/usr/bin/env python
"""json2mc module."""
from jinja2 import Template
from datetime import datetime as dt
import json
import os
import signal
import sys
import getopt
import errno
import shutil
import psutil
import itertools

from Queue import Empty
from subprocess import Popen, PIPE

import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import numpy as np
import string
from matplotlib.font_manager import FontProperties

result_file = 'output.1.txt'
hist_file = 'output.hist.txt'


def usage():
    """Print out the usage instructions."""
    print sys.argv[0] + \
        ' -t <template_file_path> -c <context_file_path> -o <output_dir> \
        -v <plot_conf_file_path> --plotonly <zot_history_dir> -d'


def signal_handler(signal, frame):
        print('['+str(os.getpid())+'] Received SIGTERM')
        sys.exit(0)


def make_sure_path_exists(path):
    """"Check if the provided path exists. If it does not exist, create it."""
    try:
        os.makedirs(path)
    except OSError as exception:
        if exception.errno != errno.EEXIST:
            raise

def get_absolute_path(path):
    if path.startswith('/'):
        return path
    else:
        return os.getcwd()+'/'+path

def parse_hist(file_path):
    """Parse history trace and save variable values in a dictionary."""
    step = -1
    records = {}
    bool_set = set()
    f = open(file_path)
    for line in f:
        line = line.strip()   # strip out carriage return
        if line.startswith("------ time"):
            step += 1
#            print("------- STEP {0} -------".format(step))
        elif line.startswith("------ end"):
            pass
        elif line.strip() == "**LOOP**":
            records["LOOP"] = step
        else:
            key_value = line.split(" = ")   # split line, into key and value
            key = key_value[0]   # key is first item in list
            if len(key_value) > 1:
                value = key_value[1]   # value is 2nd item
            else:
                value = step
                bool_set.add(key)
            # print key
            if key in records:
                records[key].append(value)
            else:
                records[key] = [value]
#    print bool_set
#    print step
#    print records
    return step, records, bool_set


def plot_hist(app_name, step, records, bool_set,
              timestamp, conf_path, plot_saving_dir, ver_time, display):
    """Plot the trace provided in the records dictionary."""
    with open(conf_path) as param_file:
        conf = json.load(param_file)
    file_name = app_name if app_name is not None else conf["app_name"]
    steplist = np.arange(step+1)
    num_rows = min(3, len(conf["plots"]))
    i = 1
    y_max = 1
    if len(conf["plots"]) % 3:
        columns = len(conf["plots"])/3 + 1
    else:
        columns = len(conf["plots"])/3
    #  first round to get the maximum y value
    my_dpi = 96
    plt.figure(figsize=(1460/my_dpi, 900/my_dpi), dpi=my_dpi)
    for p in conf["plots"]:
        for var in p["varlist"]:
            var_id = string.upper(var["id"])
            y_max = max([int(r) for r in records[var_id]] + [y_max])
    for p in conf["plots"]:
        plt.subplot(num_rows, columns, i)
        for var in p["varlist"]:
            var_id = string.upper(var["id"])
            var_style = var["style"]
            var_lw = var["linewidth"]
            var_msize = var["markersize"]
            # print var_id, '\t\t', records[var_id]
            if var_id not in bool_set:
                plt.plot(steplist, records[var_id], var_style, label=var_id,
                         linewidth=var_lw, markersize=var_msize)
            else:  # works only for TAKE_BX
                bool_series = \
                    [records['R_PROCESS_' + var_id.split('_')[-1]][j] if j
                        in records[var_id]else 0 for j in range(step + 1)]
                plt.plot(steplist, bool_series, var_style, label=var_id,
                         linewidth=var_lw, markersize=var_msize)
        if i == 1:
            plt.title("verification time: " + str(ver_time) + "\n\n" +
                      p["title"], fontsize=20)
        else:
            plt.title(p["title"], fontsize=20)
#        plt.suptitle(file_name)
        # limit the y axis to the maximum value present among the plots
        plt.ylim([0, y_max + 1])
        # print records['TOTALTIME']
        rounded_totaltime = \
            map(lambda t: round(float(t.strip('?')), 2), records['TOTALTIME'])
        plt.ylabel('#tuples')
        if i == num_rows:
            plt.xticks(steplist, rounded_totaltime, rotation=45)
            plt.xlabel('TOTALTIME', fontsize=22)
        else:
            plt.xticks(steplist, ['' for s in steplist])
        fontP = FontProperties()
        fontP.set_size('x-small')
        plt.legend(prop=fontP, loc='upper left')
#        plt.legend(loc='upper left')
        plt.grid()
        plt.axvspan(records['LOOP'], step, color='red', alpha=0.3)
        i += 1  # end for
#    plt.tight_layout()
    make_sure_path_exists(plot_saving_dir + 'imgs')
    figure_path = plot_saving_dir+'imgs/'+file_name+'_'+timestamp+'.png'
    plt.savefig(figure_path,
                facecolor='w', edgecolor='k')
    if display:
        plt.show()
    return os.path.abspath(figure_path)

def get_plot_styles_list(settings):
    # ONELINE  return [''.join(t) for t in list(itertools.product(settings["markers"], setting["line_styles"],setting["colors"]))]
    markers = settings["markers"]
    line_styles = settings["line_styles"]
    colors = settings["colors"]
    tuples_list = list(itertools.product(markers, line_styles, colors))
    strings_list = [''.join(t) for t in tuples_list]
    # print strings_list
    return strings_list

def get_y_max(cur_y_max, var_list, component_id, records):
    y_max = cur_y_max
    for var in var_list:
        var_id = string.upper(var["var_prefix"])+component_id
        y_max = max([int(r) for r in records[var_id]] + [y_max])
    return y_max

def plot_vars_from_list(step_list, var_list, component_id, records, bool_set, var_styles_dict={}):
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
                    in records[var_id]else 0 for j in range(step + 1)]
            plt.plot(step_list, bool_series, var_style, label=var_id,
                        linewidth=var_lw, markersize=var_msize)



def plot_hist2(app_name, step, records, bool_set,
              timestamp, topology, settings_path, plot_saving_dir, ver_time, display):
    """Plot the trace provided in the records dictionary."""
    with open(settings_path) as settings_file:
        settings = json.load(settings_file)
    file_name = app_name if app_name is not None else topology["app_name"]
    steplist = np.arange(step+1)
    num_rows = min(3, len(topology["bolts"]))
    if len(topology["bolts"]) % 3:
        columns = len(topology["bolts"])/3 + 1
    else:
        columns = len(topology["bolts"])/3
    #  first round to get the maximum y value
    my_dpi = 96
    plt.figure(figsize=(1460/my_dpi, 900/my_dpi), dpi=my_dpi)
    styles_list = get_plot_styles_list(settings)
    i = 1
    y_max = 1
    # get the maximum "Y" value to be displayed across all plots
    for b in topology["bolts"]:
        y_max = get_y_max(y_max, settings["bolt_vars"], b["id"], records)
        for s in b["subs"]:
            y_max = get_y_max(y_max, settings["subs_vars"], s, records)
    #plot variables' values
    for b in topology["bolts"]:# TODO COMPLETARE
        plt.subplot(num_rows, columns, i)
        plot_vars_from_list(steplist, settings["bolt_vars"], b["id"], records, bool_set)
        # get list of all varables to plot to couple them with style
        subs_vars_list = [x[0]["var_prefix"]+x[1] for x in itertools.product(settings["subs_vars"], b["subs"])]
        vars_styles_dict = {}
        if styles_list is not None and len(subs_vars_list)<=len(styles_list):
            vars_styles_dict = dict(zip(subs_vars_list, styles_list))
        for s in b["subs"]:
            plot_vars_from_list(steplist, settings["subs_vars"], s, records, bool_set, vars_styles_dict)
        if i == 1:
            plt.title("verification time: " + str(ver_time) + "\n\n" +
                      b["id"] + ' profile', fontsize=18)
        else:
            plt.title(b["id"] + ' profile', fontsize=18)
#        plt.suptitle(file_name)
        # limit the y axis to the maximum value present across the plots
        plt.ylim([0, y_max + 1])
        # print records['TOTALTIME']
        rounded_totaltime = \
            map(lambda t: round(float(t.strip('?')), 2), records['TOTALTIME'])
        plt.ylabel('#tuples')
        if i == num_rows:
            plt.xticks(steplist, rounded_totaltime, rotation=45)
            plt.xlabel('TOTALTIME', fontsize=22)
        else:
            plt.xticks(steplist, ['' for s in steplist])
        fontP = FontProperties()
        fontP.set_size('x-small')
        plt.legend(prop=fontP, loc='upper left')
#        plt.legend(loc='upper left')
        plt.grid()
        plt.axvspan(records['LOOP'], step, color='red', alpha=0.3)
        i += 1  # end for
#    plt.tight_layout()
    make_sure_path_exists(plot_saving_dir + 'imgs')
    figure_path = plot_saving_dir+'imgs/'+file_name+'_'+timestamp+'.png'
    plt.savefig(figure_path,
                facecolor='w', edgecolor='k')
    if display:
        plt.show()
    return os.path.abspath(figure_path)



def queue_get_all(q):
    items = []
    while 1:
        try:
            items.append(q.get_nowait())
        except Empty:
            break
    return items


def kill_child_processes(parent_pid, sig=signal.SIGTERM):
    try:
        parent = psutil.Process(parent_pid)
    except psutil.NoSuchProcess:
        return
    children = parent.children(recursive=True)
    for process in children:
        process.send_signal(sig)


def parallel_launch(app_dir, template, context, template_path,
                    context_path, solver):
            app_dir = app_dir+'/'+solver
            prefix = "[" + solver + "] "
            print prefix, 'Creating ', app_dir, 'directory'
            make_sure_path_exists(app_dir+'/conf')
            # make_sure_path_exists(app_dir)
            context["verification_params"]["plugin"] = solver
            with open(app_dir+"/zot_in.lisp", "w+") as out_file:
                out_file.write(template.render(context))
            template_filename = template_path.split('/')[-1]
            if context_path == None:
                context_filename = context['app_name'] + '.json'
            else:
                context_filename = context_path.split('/')[-1]
                
            print prefix, "Copying ", template_path, 'to ', app_dir
            shutil.copy(template_path, app_dir+'/' +
                        'conf/copy_of_' + template_filename)
            json_model_saving_path = app_dir+'/' + \
                        'conf/copy_of_' + context_filename
            if context_path == None:
                print 'Dumping JSON context to: ' + json_model_saving_path
                with open(json_model_saving_path, 'w+') as outfile:
                    json.dump(context, outfile, indent=4)
            else:
                print prefix, "Copying ", context_path, 'to ', app_dir
                shutil.copy(context_path, json_model_saving_path)
        # print os.getcwd()
            os.chdir(app_dir)
            command_list = ["zot", "zot_in.lisp"]
            print prefix, 'moving to: ' + os.getcwd()
            proc = Popen(command_list, stdout=PIPE, stderr=PIPE)
            child_pid = proc.pid
            print prefix, "Launched command " + " ".join(command_list) +\
                " with solver " + solver + " (" + str(child_pid) + ")"
            # wait for the child to complete
            (output, error) = proc.communicate()
            if error:
                print prefix, "error:", error
            print prefix, str(child_pid) + "Terminated -> output:", output
    #        bashCommand = "zot zot_in.lisp"
    #        os.system(bashCommand)
            print "Verication complete with solver:", solver
            return json_model_saving_path

def uppercase_ids(context):
    for s in context["topology"]["spouts"]:
        s["id"] = s["id"].upper()
    for b in context["topology"]["bolts"]:
        b["id"] = b["id"].upper()
        b["subs"] = [c.upper() for c in b["subs"]]
    context["verification_params"]["periodic_queues"] = [q.upper() for q in context["verification_params"]["periodic_queues"]] 
    context["verification_params"]["strictly_monotonic_queues"] = [q.upper() for q in context["verification_params"]["strictly_monotonic_queues"]]
    return context

def main(argv):  # noqa
    # if the FORMAL_DICE env variable is not set,
    # we assume to be in the same directory as json2mc.py
    signal.signal(signal.SIGINT, signal_handler)
    base_dir = os.environ.get('FORMAL_DICE', os.getcwd()+'/')
    os.chdir(base_dir)
    print 'BASE_DIRECTORY set to {}'.format(base_dir)
    template_path = base_dir + \
        "templating/templates/storm_topology_template.lisp"
    context_path = base_dir + \
        "templating/contexts/zot_storm_context.json"
    output_dir = base_dir + "output-dir"
    plot_conf_path = base_dir + "visual/plot_settings.json" # "visual/plot_conf_simple.json"
    plot_only = False
    app_name = None
    display = False
    json_conf = None
    try:
        opts, args = getopt.getopt(argv, "ht:c:m:j:o:v:n:d",
                                   ["template=", "context=", "model", "json",
                                    "output=", "plot-conf=", "plotonly=",
                                    "display"])
    except getopt.GetoptError:
        usage()
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            usage()
            sys.exit()
        elif opt in ("-t", "--template"):
            template_path = arg
        elif opt in ("-c", "--context"):
            context_path = arg
        elif opt in ("-m", "--model"):
            context_path = base_dir + \
                            "templating/contexts/" + arg + "_context.json"
            plot_conf_path = base_dir + 'visual/conf_' + arg + ".json"
        elif opt in ("-j", "--json"):
            json_conf = arg
        elif opt in ("-o", "--output"):
            output_dir = os.path.abspath(arg.rstrip('/'))
        elif opt in ("-d", "--display"):
            display = True
        elif opt in ("-v", "--plot-conf"):
            plot_conf_path = base_dir+arg
        elif opt in ("-n", "--plotonly"):
            print 'PLOT-ONLY mode from folder: ', arg
            plot_only = True
            app_dir = arg.rstrip('/')
            app_name = app_dir.split('/')[-1]
        # print 'Template file is "', template_path
        # print 'Context file is "', context_path
        # print 'Output directory is', output_dir
    print 'Plot configuration file is'.format(plot_conf_path)
    print 'BASE_DIRECTORY set to {}'.format(base_dir)
    print 'JSON context: ', json_conf
    json_model_saving_path = None
    if not plot_only:
        # with open("templates/zot_template.lisp", "r") as tmp:
        with open(template_path, "r") as tmp:
            template = Template(tmp.read())

        # with open("contexts/zot_context.json") as param_file:
        if json_conf is None:
            with open(context_path, "r") as param_file:
                context = json.load(param_file)
        else:
            context_path = None
            context = json_conf
        # print template.render(context)
        # turn all the ids into uppercase to match with the ids generated by zot
        context = uppercase_ids(context);
        make_sure_path_exists(output_dir)
        tmp = ''
        # SIMPLE_TOP_B2_p_15_a_5_b_0.5_B1_5_B3_3_THR_20
        app_name = context["app_name"]
        print "setting appname:  "+app_name 
        app_dir = os.path.abspath(output_dir)+'/'+app_name
        print "setting appdir:  "+app_dir
        solver = context["verification_params"]["plugin"][0]
        print 'launching process for', solver
        json_model_saving_path = parallel_launch(app_dir, template,
                                                context, template_path, 
                                                context_path, solver)
        app_dir = app_dir + '/' + solver
        print "setting app_dir: app_dir = app_dir + '/' + solver --> "+app_dir
        print "parallel_launch terminated, moving to: " + app_dir
        os.chdir(app_dir)
    else:
        os.chdir(app_dir)
    res_f = open(result_file)
    lines = res_f.readlines()
    outcome = 'Undefined'
    figure_path = None
    verification_time = 'N/A'
    hist_file_abs_path = ''
    if lines:
        outcome = lines[0].strip()
    #    verification_time = " ".join(lines[-1].strip().strip(':').strip(')').split()).split(" ")
        verification_time = (float(lines[-1].strip().strip(':').strip(')')
                                            .split(" ")[-1]))
    #    outcome = res_f.readline().strip()
        if(outcome == 'sat'):
            print "outcome is: " + outcome
            my_file_path = hist_file  # app_dir+'/'+'output.hist.txt'
            format = "%Y-%m-%d__%H-%M-%S"
            # GETTING LAST MODIFIED DATE FROM THE FILE
            moddate_seconds = os.stat(my_file_path)[8]  # there are10attributes
            # this call returns and you want the next to last
            timestamp = dt.fromtimestamp(moddate_seconds)
            timestamp_str = timestamp.strftime(format)
        #       print timestamp_str
            my_step, my_records, my_bool_set = parse_hist(my_file_path)
        #    figure_path = plot_hist(app_name, my_step, my_records,
        #              my_bool_set, timestamp_str, plot_conf_path, './',
        #              verification_time, display)
            figure_path = plot_hist2(app_name, my_step, my_records,
                        my_bool_set, timestamp_str, context['topology'], 
                        plot_conf_path, './', verification_time, display)
            hist_file_abs_path = app_dir+'/'+hist_file
        elif(outcome == 'unsat'):
            print 'UNSAT!!!'
        else:
            print "Outcome:", outcome, " -> there may be a problem. See output.1.txt"
        print "Verification time: ", str(verification_time)
        print 'Result saved to ', app_dir, ' directory.'
    else:
        print "Empty output.1.txt file!"
    generated_lisp_model_path = app_dir+"/zot_in.lisp"
    result_file_abs_path = app_dir+'/'+result_file
    # return all the info needed for display
    if not plot_only:
        return outcome, verification_time, hist_file_abs_path, figure_path, \
            json_model_saving_path, generated_lisp_model_path, result_file_abs_path


if __name__ == "__main__":
    main(sys.argv[1:])
