from dia_verification import VerificationEngine, VerificationResult
from jinja2 import Template
import pkg_resources
import os
from utils import make_sure_path_exists
from colors import bold, fail, okblue, okgreen, warning, underline, header
import config as cfg
import subprocess as sp
from v_exceptions import VerificationException
import re
from datetime import datetime as dt

class UppaalEngine(VerificationEngine):

    resource_package = __name__  # Could be any module/package name

    file_dir = pkg_resources.resource_filename(resource_package, "")
    # base directory
    base_dir = os.environ.get('FORMAL_DICE',
                              file_dir)
    model_filename = 'model.xml'
    property_filename = 'property.q'

    def __init__(self, v_task):
        labeling = v_task.context["labeling"]
        model_template_filename = "spark_ta_model_template_{}labeling.xml".format('' if labeling else 'NO_')
        property_template_filename = "spark_ta_property_template_{}labeling.xml".format('' if labeling else 'NO_')
        self.model_template_path = os.path.join(UppaalEngine.base_dir,
                                                "templates",
                                                model_template_filename)
        self.property_template_path = os.path.join(UppaalEngine.base_dir,
                                                   "templates",
                                                   property_template_filename)
        self.app_dir = os.path.join(v_task.app_dir, 'uppaal')
        v_task.result_dir = self.app_dir
        print("opening {}".format(self.model_template_path))
        with open(self.model_template_path) as t_file:
            self.model_template = Template(t_file.read())
        print("opening {}".format(self.property_template_path))
        with open(self.property_template_path) as t_property_file:
            self.property_template = Template(t_property_file.read())

        stages = v_task.context["stages"]

        if labeling:
            # LABELING
            print {s['id']: s['label'] for s in stages.values()}
            stages_int = {int(s): v for s, v in stages.items()}
            labels = {l: {s["id"]: idx for idx, s in
                          enumerate([x for x in stages_int.values() if x['label'] == l])}
                      for l in v_task.context["labels"]}
            v_task.context.update({"indexes": labels})
            print warning("Label -> (associated_node -> index)\n{}".format(labels))
            final_stages = {k: v["label"] for k, v in stages.items() if
                            not any([int(k) in s["parentsIds"] for s in stages.values()])}
        else:
            # NO LABELING
            final_stages = [k for k in stages.keys() if not any([k in v["parentsIds"] for v in stages.values()])]

        v_task.context.update({"finalstages": final_stages})
        print okblue("Final stage -> label\n{}".format(final_stages))

        make_sure_path_exists(self.app_dir)
        # RENDER MODEL AND PROPERTY
        print "rendering model with template: {}".format(self.model_template_path)
        with open(os.path.join(self.app_dir, self.model_filename), 'w+') as outfile:
            outfile.write(self.model_template.render(v_task.context))
        print "rendering property with template: {} \n to {}".format(self.property_template_path, v_task.app_dir)
        with open(os.path.join(self.app_dir, self.property_filename), 'w+') as outfile:
            outfile.write(self.property_template.render(v_task.context))
        print "end"


    def launch_verification(self, v_task):
        prefix = '[uppaal]'
        command_list = [cfg.UPPAAL_CMD, '-u', self.model_filename, self.property_filename]
        try:
            print("{}Launching command {} on dir. {} ").format(prefix,
                                                               " ".join(command_list),
                                                               self.app_dir)
            proc_out = sp.check_output(['ls', '-l'], stderr=sp.STDOUT, cwd=self.app_dir)
            print "{}Terminated -> output:\n{}".format(prefix, proc_out)
            with open(os.path.join(self.app_dir, 'output.txt'), "w+") as of:
                of.write(proc_out)
            print "{}Verification complete.".format(prefix)
            # do something with output
        except sp.CalledProcessError as exc:
            print "Error (return code: {})".format(exc.returncode)
            with open(os.path.join(self.app_dir, 'output.err'), "w+") as of:
                of.write(exc.output)
            raise VerificationException(exc.output)

    def process_result(self, v_task):
        v_task.verification_result = UppaalResult(self.app_dir)


class UppaalResult(VerificationResult):
    """
    classdocs
    """
    # standard result file for zot (containing verification outcome)
    result_file = 'output.txt'
    # standard history file for zot (containing output trace)
    timestamp_format = "%Y-%m-%d__%H-%M-%S"

    def __init__(self, result_dir):
        '''
        Constructor
        '''
        self.result_dir = os.path.abspath(result_dir)
        self.result_file_path = os.path.join(self.result_dir, self.result_file)
        with open(self.result_file_path) as res_f:
            lines = res_f.readlines()
        self.outcome = None
        self.all_stats = None
        self.verification_time = None
        self.max_memory = None
        self.memory = None
        self.timestamp = None
        self.timestamp_str = None
        if lines:
            self.all_stats = {re.sub(' +', '_', y[0].strip().lower()): y[1].strip() for x in lines if '--' in x for
                              y in
                              [re.sub(' -- ', '', x).strip().split(':') if len(x.split(' -- ')) <= 2
                               else ['outcome', x.split(' -- ')[-1].strip()]]}
            self.verification_time = self.all_stats['cpu_user_time_used']
            self.max_memory = self.all_stats['virtual_memory_used']
            self.memory = self.all_stats['resident_memory_used']
            self.outcome = 'unsat' if 'NOT' in self.all_stats['outcome'] else 'sat'
            if self.outcome == 'sat':
                print 'Outcome is {} '.format(self.outcome.upper())
                # app_dir+'/'+'output.hist.txt'
                my_file_path = os.path.join(self.result_dir, self.result_file)
                print 'I AM in {}'.format(os.getcwd())
                # GETTING LAST MODIFIED DATE FROM THE FILE
                moddate_seconds = os.stat(my_file_path)[8]  # 10 attributes
                self.timestamp = dt.fromtimestamp(moddate_seconds)
                self.timestamp_str = (self.timestamp
                    .strftime(self.timestamp_format))
            elif self.outcome == 'unsat':
                print 'Outcome is {} '.format(self.outcome.upper())

            print "Verification time: {}".format(self.verification_time)
            print "VirtualMemory: {} - ResidentMemory: {}".format(self.max_memory, self.memory)
            print "Result saved to {} directory".format(self.result_dir)
        else:
            print ("Outcome: {} -> \n\n THERE MIGHT BE A PROBLEM."
                   "check {} for further info.".format(self.outcome, self.result_file))