#!/usr/bin/env python
"""json2mc module."""
from jinja2 import Template
from datetime import datetime as dt
import json
import os
import sys
import getopt
import errno
import shutil

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
              timestamp, conf_path, plot_saving_dir, display):
    """Plot the trace provided in the records dictionary."""
    with open(conf_path) as param_file:
        conf = json.load(param_file)
    file_name = app_name if app_name is not None else conf["app_name"]
    steplist = np.arange(step+1)
    num_rows = 3
    i = 1
    y_max = 1
    if len(conf["plots"]) % 3:
        columns = len(conf["plots"])/3 + 1
    else:
        columns = len(conf["plots"])/3
    #  first round to get the maximum y value
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
        plt.title(p["title"])
#        plt.suptitle(file_name)
        # limit the y axis to the maximum value present among the plots
        plt.ylim([0, y_max + 1])
        # print records['TOTALTIME']
        rounded_totaltime = \
            map(lambda t: round(float(t.strip('?')), 2), records['TOTALTIME'])
        plt.ylabel('#tuples')
        if i == num_rows:
            plt.xticks(steplist, rounded_totaltime, rotation=45)
            plt.xlabel('TOTALTIME')
        else:
            plt.xticks(steplist, ['' for s in steplist])
        fontP = FontProperties()
        fontP.set_size('x-small')
        plt.legend(prop=fontP, loc='upper left')
#        plt.legend(loc='upper left')
        plt.grid()
        plt.axvspan(records['LOOP'], step, color='red', alpha=0.5)
        i += 1  # end for
#    plt.tight_layout()
    make_sure_path_exists(plot_saving_dir + 'imgs')
    plt.savefig(plot_saving_dir+'imgs/'+file_name+'_'+timestamp+'.png',
                figsize=(20, 16), dpi=300, facecolor='w', edgecolor='k')
    plt.savefig(plot_saving_dir+'imgs/'+file_name+'_'+timestamp+'.eps',
                format='eps', dpi=1000)
    if display:
        plt.show()


def main(argv):  # noqa
    # if the FORMAL_DICE env variable is not set,
    # we assume to be in the same directory as json2mc.py
    base_dir = os.environ.get('FORMAL_DICE', os.getcwd()+'/')
    template_path = base_dir + \
        "templating/templates/storm_topology_template.lisp"
    context_path = base_dir + \
        "templating/contexts/zot_storm_context.json"
    output_dir = base_dir + "templating/generated_files"
    plot_conf_path = base_dir + "visual/plot_conf_simple.json"
    plot_only = False
    app_name = None
    display = False
    try:
        opts, args = getopt.getopt(argv, "ht:c:o:v:n:d",
                                   ["template=", "context=", "output=",
                                    "plot-conf=", "plotonly=", "display"])
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
        elif opt in ("-o", "--output"):
            output_dir = arg
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
        print 'Creating ', app_dir, 'directory'
        make_sure_path_exists(app_dir+'/'+'conf')
        # make_sure_path_exists(app_dir)
        with open(app_dir+"/zot_in.lisp", "w+") as out_file:
            out_file.write(template.render(context))

        template_filename = template_path.split('/')[-1]
        context_filename = context_path.split('/')[-1]
        print "Copying ", template_path, 'and ', context_path, 'to ', app_dir
        shutil.copy(template_path, app_dir+'/' +
                    'conf/copy_of_' + template_filename)
        shutil.copy(context_path, app_dir+'/' +
                    'conf/copy_of_' + context_filename)
# print os.getcwd()
    os.chdir(app_dir)
    print 'moving to: ' + os.getcwd()
    if not plot_only:
        bashCommand = "zot zot_in.lisp"
        os.system(bashCommand)

    res_f = open(result_file)
    outcome = res_f.readline().strip()
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
                  my_bool_set, timestamp_str, plot_conf_path, './', display)
    elif(outcome == 'unsat'):
        print 'UNSAT!!!'
    print 'Result saved to ', app_dir, ' directory.'

if __name__ == "__main__":
    main(sys.argv[1:])
