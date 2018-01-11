from jinja2 import Template
import json
with open('uppaal_spark_template.xml') as t_file:
    template = Template(t_file.read())

with open('context.json') as c_file:
    c = json.load(c_file)
    with open('output.xml', 'w+') as outfile:
        outfile.write(template.render(c))

