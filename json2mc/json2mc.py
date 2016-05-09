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

from multiprocessing import Process, Queue
from Queue import Empty
from subprocess import Popen, PIPE

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
    plt.savefig(plot_saving_dir+'imgs/'+file_name+'_'+timestamp+'.png',
                facecolor='w', edgecolor='k')
    plt.savefig(plot_saving_dir+'imgs/'+file_name+'_'+timestamp+'.eps',
                format='eps')
    if display:
        plt.show()


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
                    context_path, solver, solvers_queue):
            app_dir = app_dir+'/'+solver
            prefix = "[" + solver + "] "
            print prefix, 'Creating ', app_dir, 'directory'
            make_sure_path_exists(app_dir+'/conf')
            # make_sure_path_exists(app_dir)
            context["verification_params"]["plugin"] = solver
            with open(app_dir+"/zot_in.lisp", "w+") as out_file:
                out_file.write(template.render(context))
            template_filename = template_path.split('/')[-1]
            context_filename = context_path.split('/')[-1]
            print prefix, "Copying ", template_path, ', ', context_path, \
                'to ', app_dir
            shutil.copy(template_path, app_dir+'/' +
                        'conf/copy_of_' + template_filename)
            shutil.copy(context_path, app_dir+'/' +
                        'conf/copy_of_' + context_filename)
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
            print prefix, "putting solver", solver, "on queue"
            solvers_queue.put(solver)
#            print "DONE putting!", solver


def main(argv):  # noqa
    # if the FORMAL_DICE env variable is not set,
    # we assume to be in the same directory as json2mc.py
    signal.signal(signal.SIGINT, signal_handler)
    base_dir = os.environ.get('FORMAL_DICE', os.getcwd()+'/')
    template_path = base_dir + \
        "templating/templates/storm_topology_template.lisp"
    context_path = base_dir + \
        "templating/contexts/zot_storm_context.json"
    output_dir = base_dir + "output-dir"
    plot_conf_path = base_dir + "visual/plot_conf_simple.json"
    plot_only = False
    app_name = None
    display = False
    try:
        opts, args = getopt.getopt(argv, "ht:c:m:o:v:n:d",
                                   ["template=", "context=", "model",
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
            plot_conf_path = os.getcwd()+'/visual/conf_' + arg + ".json"
        elif opt in ("-o", "--output"):
            output_dir = arg.rstrip('/')
        elif opt in ("-d", "--display"):
            display = True
        elif opt in ("-v", "--plot-conf"):
            plot_conf_path = os.getcwd()+'/'+arg
        elif opt in ("-n", "--plotonly"):
            print 'PLOT-ONLY mode from folder: ', arg
            plot_only = True
            app_dir = arg.rstrip('/')
            app_name = app_dir.split('/')[-1]
        # print 'Template file is "', template_path
        # print 'Context file is "', context_path
        # print 'Output directory is', output_dir
    print 'Plot configuration file is', plot_conf_path
    if not plot_only:
        # with open("templates/zot_template.lisp", "r") as tmp:
        with open(template_path, "r") as tmp:
            template = Template(tmp.read())

        # with open("contexts/zot_context.json") as param_file:
        with open(context_path, "r") as param_file:
            context = json.load(param_file)

        # print template.render(context)
        make_sure_path_exists(output_dir)
        tmp = ''
        for b in context["topology"]["bolts"]:
            tmp += '_'+b["id"] + '_p_' + str(b["parallelism"]) + \
                   '_a_' + str(b["alpha"]) + '_s_' + str(b["sigma"])
        # SIMPLE_TOP_B2_p_15_a_5_b_0.5_B1_5_B3_3_THR_20
        app_name = context["app_name"]
    #    app_name = context["app_name"] + tmp + \
    #        '_THR_' + str(context["topology"]["queue_threshold"]) + \
    #        '_STEPS_' + str(context["verification_params"]["num_steps"])
        app_dir = output_dir+'/'+app_name
        processes = []
        solvers_queue = Queue()
        for solver in context["verification_params"]["plugin"]:
            print 'launching process for', solver
            p = Process(target=parallel_launch, args=(app_dir, template,
                        context, template_path, context_path, solver,
                        solvers_queue))
            p.start()
            processes.append(p)
        print "waiting for first process to terminate"
        first_to_terminate = solvers_queue.get()
        print "Getting results from: ", first_to_terminate
#        time.sleep(10)
        for p in processes:
            if p.is_alive():
                print "terminating children of ", p.pid
                kill_child_processes(p.pid)
                print "terminating ", p.pid
                p.terminate()
#        pids = queue_get_all(pids_queue)
#        print "pids: ", pids
#        for pid in pids:
#            if psutil.pid_exists(pid):
#                print "killing", pid, "..."
#                os.kill(pid, signal.SIGTERM)
        app_dir = app_dir + '/' + first_to_terminate
        os.chdir(app_dir)
    else:
        os.chdir(app_dir)
        print 'moving to: ' + os.getcwd()
    res_f = open(result_file)
    lines = res_f.readlines()
    if lines:
        outcome = lines[0].strip()
    #    verification_time = " ".join(lines[-1].strip().strip(':').strip(')').split()).split(" ")
        verification_time = float(lines[-1].strip().strip(':').strip(')').split(" ")[-1])
    #    outcome = res_f.readline().strip()
        if(outcome == 'sat'):
            my_file_path = hist_file  # app_dir+'/'+'output.hist.txt'
            format = "%Y-%m-%d__%H-%M-%S"
            # GETTING LAST MODIFIED DATE FROM THE FILE
            moddate_seconds = os.stat(my_file_path)[8]  # there are 10 attributes
            # this call returns and you want the next to last
            timestamp = dt.fromtimestamp(moddate_seconds)
            timestamp_str = timestamp.strftime(format)
        #       print timestamp_str
            my_step, my_records, my_bool_set = parse_hist(my_file_path)
            plot_hist(app_name, my_step, my_records,
                      my_bool_set, timestamp_str, plot_conf_path, './',
                      verification_time, display)
        elif(outcome == 'unsat'):
            print 'UNSAT!!!'
        else:
            print "Outcome:", outcome, " -> there may be a problem. See output.1.txt"
        print "Verification time: ", str(verification_time)
        print 'Result saved to ', app_dir, ' directory.'
    else:
        print "Empty output.1.txt file!"


if __name__ == "__main__":
    main(sys.argv[1:])
