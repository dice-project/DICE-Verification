import re

from setuptools import setup

version = re.search(
    '^__version__\s*=\s*"(.*)"',
    open('json2mc/json2mc.py').read(),
    re.M
    ).group(1)

setup(name='D-VerT-json2mc',
      version=version,
      description='JSON2MC module for D-VerT Server',
      url='https://github.com/dice-project/DICE-Verification',
      author='Francesco Marconi',
      author_email='francesco.marconi@polimi.it',
      license='Apache License 2.0',
      packages=['json2mc'],
      entry_points={
        "console_scripts": ['json2mc = json2mc.json2mc:main']
        },
      install_requires=['graphviz>=0.5.2',
                        'Jinja2>=2.8',
                        'matplotlib>=1.5.3',
                        'networkx==1.11',
                        'tinydb==3.7.0'],
      zip_safe=False,
      include_package_data=True)
