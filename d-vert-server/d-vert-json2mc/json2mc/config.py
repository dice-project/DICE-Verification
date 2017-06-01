'''
Created on Jan 19, 2017

@author: francesco marconi
'''
from storm_verification import StormVerificationTask
from spark_verification import SparkVerificationTask
import os

ZOT_CMD = os.environ.get('D-VERT_ZOT_CMD', 'zot')

SUPPORTED_TECHS = {
        "storm": StormVerificationTask,
        "spark": SparkVerificationTask
    }

TECH_KEYS = SUPPORTED_TECHS.keys()

TOLERANCE = 0.04

PLOT_CFG = {
    "x-label":{
        "fontsize": 14,
        "s": "time"
    },
    "y-label": {
        "fontsize": 14,
        "s": "#tuples"
    },
    "plot_titles":{
        "fontsize": 14
    }

}