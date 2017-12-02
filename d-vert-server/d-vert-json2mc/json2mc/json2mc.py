#!../venv/bin/python
# encoding: utf-8
'''
json2mc --  from json to formal verification of Storm topologies / Spark DAGs

json2mc is the CLI of d-VerT server
It allows the user to invoke the formal verification
of storm topologies/Spark DAGs by specifying via
command line several parameters like:
the technology to be considered,
the file containing the json description of the application,
the output folder where to save results,
the file containing the graphical configuration, etc.

@author:     Francesco Marconi

@copyright:  2015-2017 Politecnico di Milano. All rights reserved.

@license:    Apache License 2.0

@contact:    francesco.marconi@polimi.it

@deffield    updated: Updated
'''

import sys
import os
import json

from collections import OrderedDict

from argparse import ArgumentParser
from argparse import RawDescriptionHelpFormatter
import config as cfg
from factory_methods import DiaVerificationFactory

from dia_verification import VerificationException

from tinydb import TinyDB, Query
from datetime import datetime as dt

__all__ = []
__version__ = "0.3.2"
__date__ = '2015-11-10'
__updated__ = '2017-12-01'

DEBUG = 0
TESTRUN = 0
PROFILE = 0

STARTED = 'start'
COMPLETED = 'complete'
ERROR = 'error'

def get_current_datetime_string():
    """
    :return: current time a string with format %Y-%m-%d__%H_%M_%S
    """
    return dt.now().strftime('%Y-%m-%d__%H_%M_%S')


def persist_results_on_db(v_task, db_location='./', status=COMPLETED):
    db = TinyDB(os.path.join(os.path.abspath(db_location), 'd_vert_db.json'))
    '''
    cores = None
    tasks = None
    deadline = None
    input_records = None
    if isinstance(v_task, DiaVerificationFactory.__supported_techs.get('spark', None)):
    '''
    cores = v_task.context['tot_cores']
    tasks = v_task.context['stages']['0']['numtask']
    deadline = v_task.context['deadline']
    input_records = v_task.context['stages']['0']['records_read']
    time_bound = v_task.context['verification_params']['time_bound']
    result_dir = v_task.result_dir
    id = v_task.app_name
    v_time = None
    timestamp_label = None
    if status == COMPLETED:
        timestamp_label = 'end_timestamp'
        outcome = v_task.verification_result.outcome
        v_time = v_task.verification_result.verification_time
    elif status == STARTED:
        timestamp_label = 'start_timestamp'
        outcome = 'running'
    elif status == ERROR:
        timestamp_label = 'interruption_timestamp'
        outcome = 'ERROR/TIMEOUT'
    app_type = v_task.context['app_type'] if 'app_type' in v_task.context else None
    table = db.table('{}_C{}_T{}_rec{}'.format(app_type, cores, tasks, input_records))
    entry = Query()
    table.upsert({'id': id,
                  'cores': cores,
                  'tasks': tasks,
                  'input_records': input_records,
                  'time_bound': time_bound,
                  'deadline': deadline,
                  'outcome': outcome,
                  'result_dir': result_dir,
                  'v_time': v_time,
                  'app_type': app_type,
                  timestamp_label: get_current_datetime_string()
                  },
                 entry.id == id
                 )


def create_lisp_list(l):
    res = "'( "
    if len(l) == 0:
        res += "SS "
    else:
        for e in set(l):
            res += "S" + str(e) + " "
    res += ")"
    return res


class CLIError(Exception):
    '''Generic exception to raise and log different fatal errors.'''
    def __init__(self, msg):
        super(CLIError).__init__(type(self))
        self.msg = "E: %s" % msg

    def __str__(self):
        return self.msg

    def __unicode__(self):
        return self.msg


def main(argv=None):  # IGNORE:C0111
    '''Command line options.'''

    if argv is None:
        argv = sys.argv
    else:
        sys.argv.extend(argv)

    program_name = os.path.basename(sys.argv[0])
    program_version = "v%s" % __version__
    program_build_date = str(__updated__)
    program_version_message = '%%(prog)s %s (%s)' % (program_version,
                                                     program_build_date)
    program_shortdesc = __doc__.split("\n")[1]
    program_license = '''%s

  Created by Francesco Marconi on %s.
  Copyright 2015-2017 Politecnico di Milano. All rights reserved.

  Licensed under the Apache License 2.0
  http://www.apache.org/licenses/LICENSE-2.0

  Distributed on an "AS IS" basis without warranties
  or conditions of any kind, either express or implied.

USAGE
''' % (program_shortdesc, str(__date__))

    file_dir = os.path.dirname(os.path.realpath(__file__))
    base_dir = os.environ.get('FORMAL_DICE',
                              file_dir)

    try:
        # Setup argument parser
        parser = ArgumentParser(description=program_license,
                                formatter_class=RawDescriptionHelpFormatter)
        group1 = parser.add_mutually_exclusive_group()
        parser.add_argument("-T", "--technology", dest="technology",
                            default=cfg.TECH_KEYS[0],
                            help="reference technology (supported: {})"
                            .format(", ".join(cfg.TECH_KEYS)))
        parser.add_argument("-t", "--template", dest="template_path",
                            help="path to template file")
        group1.add_argument("-c", "--context", dest="context_path",
                            help="path to JSON context file"
                            " [default: {}s]"
                            .format(os.path.join(base_dir,
                                                 "context_examples",
                                                 "[{}]".format(", ".join(cfg.TECH_KEYS)),
                                                 "[{}]_example.json"
                                                 .format(", ".join(cfg.TECH_KEYS)))))
        parser.add_argument("-l", "--label", dest="label",
                            action="store_true",
                            help="apply graph labeling to reduce the size of "
                            "the formal model created [default: %(default)s]")
        parser.add_argument("-d", "--display", dest="display",
                            action="store_true",
                            help="plots the outcome at the end of verification"
                            " [default: %(default)s]")
        parser.add_argument("-v", "--verbose", dest="verbose",
                            action="count",
                            help="set verbosity level [default: %(default)s]")
        parser.add_argument("-o", "--output", dest="output_dir",
                            default="out",
                            help="output directory where to store results "
                            "[default: %(default)s]")
        group1.add_argument("-p", "--plotonly", dest="results_dir",
                            help="directory of an already performed "
                            "verification task. the program will only plot "
                            "the results. [default: %(default)s]")
        parser.add_argument('-V', '--version', action='version',
                            version=program_version_message)
        parser.add_argument("-g", "--graphical_conf",
                            dest="graphical_conf_path",
                            help="path to JSON file containing the graphical "
                            "configuration [default: %(default)s]")
        parser.add_argument("--db", dest="db", action="store_true")

        # Process arguments
        args = parser.parse_args()
        technology = args.technology
        print args.context_path
        context_path = args.context_path if args.context_path else os.path.join(base_dir,
                                                                                "context_examples",
                                                                                technology,
                                                                                technology+"_example.json")
        verbose = args.verbose
        display = args.display
        results_dir = args.results_dir
        output_dir = args.output_dir
        template_path = args.template_path
        graphical_conf_path = args.graphical_conf_path
        label = args.label
        db = args.db
        

        print("Context_path: {}".format(context_path))

        if verbose > 0:
            print("Verbose mode on")
            if display:
                print("Display mode on")
            else:
                print("Display mode off")

        if not results_dir:  # is not plotonly
            try:
                with open(context_path, "r") as param_file:
                    context = json.load(param_file,
                                        object_pairs_hook=OrderedDict)

                    v_task = DiaVerificationFactory.get_verif_task(tech=technology,
                                                                   template_path=template_path,
                                                                   context=context,
                                                                   output_dir=output_dir,
                                                                   display=display)
                    print v_task
                    try:
                        if db:
                            persist_results_on_db(v_task, output_dir, STARTED)
                        v_task.launch_verification()
                        if v_task.result_dir:
                            v_task.process_zot_results()
                            if v_task.verification_result.outcome == 'sat':
                                v_task.plot_trace()
                            if db:
                                persist_results_on_db(v_task, output_dir, COMPLETED)
                            print 'DONE'
                        else:
                            print 'FINISHED WITH ERRORS'
                    except VerificationException as e:
                        print "Errors while performing the verification task!\n{}\nAborting ...".format(e)
                        persist_results_on_db(v_task, output_dir, ERROR)

            except IOError as e:
                # Does not exist OR no read permissions
                print "Unable to open file"
        else:
            v_task = DiaVerificationFactory.get_verif_task(tech=technology,
                                                           plotonly_folder=results_dir,
                                                           display=display,
                                                           graphical_conf_path=graphical_conf_path)
            v_task.plot_trace()
        return 0
    except KeyboardInterrupt:
        ### handle keyboard interrupt ###
        return 0
    except Exception, e:
        if DEBUG or TESTRUN:
            raise(e)
        indent = len(program_name) * " "
        sys.stderr.write(program_name + ": " + repr(e) + "\n")
        sys.stderr.write(indent + "  for help use --help")
        return 2


if __name__ == "__main__":
    if DEBUG:
        sys.argv.append("-h")
        sys.argv.append("-v")
        sys.argv.append("-r")
    if TESTRUN:
        import doctest
        doctest.testmod()
    if PROFILE:
        import cProfile
        import pstats
        profile_filename = 'json2mc.parse_dag_json_profile.txt'
        cProfile.run('main()', profile_filename)
        statsfile = open("profile_stats.txt", "wb")
        p = pstats.Stats(profile_filename, stream=statsfile)
        stats = p.strip_dirs().sort_stats('cumulative')
        stats.print_stats()
        statsfile.close()
        sys.exit(0)
    sys.exit(main())

