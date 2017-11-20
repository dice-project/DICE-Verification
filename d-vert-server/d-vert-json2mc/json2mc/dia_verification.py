'''
Created on Jan 6, 2017

@author: francesco
'''

import os
import shutil
import json
import subprocess as sp

import utils
import pkg_resources

import config as cfg


class VerificationTask(object):
    '''
    classdocs
    '''
    resource_package = __name__  # Could be any module/package name
    file_dir = pkg_resources.resource_filename(resource_package, "")

    # base directory
#    file_dir = os.path.dirname(os.path.realpath(__file__))
    base_dir = os.environ.get('FORMAL_DICE',
                              file_dir)
    # standard result file for zot (containing verification outcome)
    result_file = 'output.1.txt'
    # standard history file for zot (containing output trace)
    hist_file = 'output.hist.txt'

    def launch_verification(self):
        self.result_dir = os.path.join(self.app_dir, self.plugin)
        prefix = "[{}]".format(self.plugin)
        print "{} Creating {} directory".format(prefix, self.result_dir)
        utils.make_sure_path_exists(os.path.join(self.result_dir, 'conf'))
        # make_sure_path_exists(app_dir)
        # self.context["verification_params"]["plugin"] = plugin
        with open(os.path.join(self.result_dir, "zot_in.lisp"), "w+") as out_f:
            out_f.write(self.template.render(self.context))
        template_filename = self.template_path.split(os.path.sep)[-1]
        context_filename = self.context['app_name'] + '.json'
        print "{}Copying {} to {}".format(prefix,
                                          self.template_path,
                                          self.result_dir)
        shutil.copy(self.template_path,
                    os.path.join(self.result_dir, 'conf',
                                 'copy_of_' + template_filename))
        self.json_context_path = os.path.join(self.result_dir, 'conf',
                                            'copy_of_' + context_filename)
        self.json_starting_context_path = os.path.join(self.result_dir, 'conf',
                                              'copy_of_start_' + context_filename)
        print '{}Dumping JSON context to: {}'.format(prefix,
                                                     self.json_context_path)
        with open(self.json_context_path, 'w+') as outfile:
            json.dump(self.context, outfile, indent=4)
        starting_context = getattr(self, 'starting_context', None)
        if starting_context:
            print '{}Dumping JSON Starting context to: {}'.format(prefix,
                                                                  self.json_starting_context_path)
            with open(self.json_starting_context_path, 'w+') as outfile:
                json.dump(starting_context, outfile, indent=4)
        command_list = [cfg.ZOT_CMD, "zot_in.lisp"]

        try:
            print ("{}Launching command {} on dir. {} "
                   "with plugin {}").format(prefix,
                                            " ".join(command_list),
                                            self.result_dir,
                                            self.plugin)
            proc_out = sp.check_output(command_list, stderr=sp.STDOUT, cwd=self.result_dir)
            print "{}Terminated -> output:\n{}".format(prefix, proc_out)
            print "{}Verification complete with plugin: {}".format(prefix,
                                                                   self.plugin)
            # do something with output
        except sp.CalledProcessError as exc:
            print("Status : FAIL", exc.returncode, exc.output)

        '''
        proc = sp.Popen(command_list,
                        stdout=sp.PIPE,
                        stderr=sp.PIPE,
                        cwd=self.result_dir)
        '''


        '''
        if error:
            print "{}Error!".format(prefix)
            raise VerificationException(error)
        print "{}{}Terminated -> output:\n{}".format(prefix,
                                                     str(child_pid),
                                                     output)
        print "{}Verification complete with plugin: {}".format(prefix,
                                                               self.plugin)
        '''

    def parse_zot_trace(self, file_path=None):
        raise NotImplementedError()

    def process_zot_results(self, result_dir=None, trace_file=None):
        raise NotImplementedError()

    def plot_trace(self):
        raise NotImplementedError()

    def __str__(self):
        return '''Verification Task:
                template_path: {} \n
                template: {} \n
                context: {} \n
                output_dir: {} \n
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
                           self.app_name,
                           self.app_dir,
                           self.plugins,
                           self.verification_result,
                           self.output_trace,
                           self.display)


class VerificationResult(object):
    '''
    classdocs
    '''

    def __init__(self,
                 result_file_path=None,
                 outcome=None,
                 verification_time=None,
                 timestamp=None,
                 timestamp_str=None):
        '''
        Constructor
        '''
        self.result_file_path = result_file_path
        self.outcome = outcome
        self.verification_time = verification_time
        self.timestamp = timestamp
        self.timestamp_str = timestamp_str


class VerificationTrace(object):
    '''
    classdocs
    '''

    def __init__(self,
                 output_trace_file_path=None,
                 time_bound=None, records=None,
                 bool_set=None):
        '''
        Constructor
        '''
        self.time_bound = time_bound
        self.records = records
        self.bool_set = bool_set
