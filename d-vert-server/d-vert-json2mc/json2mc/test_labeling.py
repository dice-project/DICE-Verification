from spark_verification import SparkDAG
from dagrenderer import DAGRenderer
import os
import argparse
import sys
import json


DEFAULT_OUT_DIR = './labeling_test'
DISPLAY = True

def label_and_render(args):
    context_file_path = args.context_file_path
    with open(context_file_path) as cf:
        stages = json.load(cf)
        not_skipped_stages = {k : v for k, v in stages.items() if not v['skipped']}
        context = {'app_name': os.path.basename(context_file_path).rstrip('.json'),
                   'stages': not_skipped_stages}
    dag = SparkDAG(context)
    dag.label_graph()
    print dag.g.nodes(data=True)
    gr = DAGRenderer(dag.g)
    gr.render(os.path.join(DEFAULT_OUT_DIR, context['app_name'] + ".gv"), DISPLAY)



def main():
    parser = argparse.ArgumentParser(
        description=
            """
            Test Labeling
            """
    )

    parser.add_argument('-c', '--context', dest='context_file_path',
                        help='Path to the JSON context file')

    parser.set_defaults(func=label_and_render)
    args = parser.parse_args()

    try:
        getattr(args, "func")
    except AttributeError:
        parser.print_help()
        sys.exit(0)

    args.func(args)


if __name__ == '__main__':
    main()