from dia_verification import VerificationEngine
from jinja2 import Template
import pkg_resources
import os
from utils import order_int

class UppaalEngine(VerificationEngine):

    resource_package = __name__  # Could be any module/package name

    file_dir = pkg_resources.resource_filename(resource_package, "")
    # base directory
    #    file_dir = os.path.dirname(os.path.realpath(__file__))
    base_dir = os.environ.get('FORMAL_DICE',
                              file_dir)

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
            print '\033[93m' + '\033[1m' + "\nLabel -> [associated nodes]" + '\033[0m'
            print labels
            final_stages = {k: v["label"] for k, v in stages.items() if
                            not any([int(k) in s["parentsIds"] for s in stages.values()])}


        else:
            # NO LABELING
            final_stages = [k for k in stages.keys() if not any([k in v["parentsIds"] for v in stages.values()])]

        v_task.context.update({"finalstages": final_stages})
        print "\033[93m \033[1m \nFinal stages: {} \033[0m".format(final_stages)

        # RENDER MODEL AND PROPERTY
        print "rendering model with template: {}".format(self.model_template_path)
        with open('output.xml', 'w+') as outfile:
            outfile.write(self.model_template.render(v_task.context))
        print "rendering property with template: {}".format(self.property_template_path)
        with open('output.q', 'w+') as outfile:
            outfile.write(self.property_template.render(v_task.context))
        print "end"


    def launch_verification(self, v_task):
        pass

    def process_result(self):
        pass
