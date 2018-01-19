'''
Created on Jan 19, 2017

@author: francesco marconi
'''
import os

ZOT_CMD = os.environ.get('D_VERT_ZOT_CMD', 'zot')
UPPAAL_CMD = os.environ.get('D_VERT_UPPAAL_CMD', 'verifyta')

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
