from dia_verification import VerificationEngine
from jinja2 import Template
import pkg_resources
import os
from utils import make_sure_path_exists
from colors import bold, fail, okblue, okgreen, warning, underline, header
import config as cfg
import subprocess as sp
from v_exceptions import VerificationException

class UppaalEngine(VerificationEngine):

    resource_package = __name__  # Could be any module/package name

    file_dir = pkg_resources.resource_filename(resource_package, "")
    # base directory
    #    file_dir = os.path.dirname(os.path.realpath(__file__))
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
            labels = {l: {s["id"]: idx for idx, s in
                          enumerate([x for x in stages.values() if x['label'] == l])}
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


    def launch_verification(self):
        prefix = 'uppaal'
        command_list = [cfg.UPPAAL_CMD, self.model_filename, '-u', self.model_filename, self.property_filename]
        try:
            print("{}Launching command {} on dir. {} ").format(prefix,
                                                               " ".join(command_list),
                                                               self.app_dir)
            proc_out = sp.check_output(command_list, stderr=sp.STDOUT, cwd=self.app_dir)
            print
            "{}Terminated -> output:\n{}".format(prefix, proc_out)
            print
            "{}Verification complete.".format(prefix)
            # do something with output
        except sp.CalledProcessError as exc:
            print
            "Error (return code: {})".format(exc.returncode)
            raise VerificationException(exc.output)

    def process_result(self):
        pass
