'''
Created on Jan 19, 2017

@author: francesco marconi
'''
from storm_verification import StormVerificationTask


SUPPORTED_TECHS = {
        "storm": StormVerificationTask
    }

TECH_KEYS = SUPPORTED_TECHS.keys()

TOLERANCE = 0.04

PLOT_CFG = {
    "x-label":{
        "fontsize": 22,
        "text": "TOTALTIME"
    },
    "y-label": {
        "fontsize": 22,
        "text": "#tuples"
    },
    "plot-titles":{
        "fontsize": 30
    },
    "highlighted-area": {
        "color" : "red",
        "alpha" : 0.3
    },
    "greyscale" : True,
    "legend" : {
        "size" : "large"
    }


}
