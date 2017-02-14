'''
Created on Jan 19, 2017

@author: francesco marconi
'''
from storm_verification import StormVerificationTask
from spark_verification import SparkVerificationTask


SUPPORTED_TECHS = {
        "storm": StormVerificationTask,
        "spark": SparkVerificationTask
    }

TECH_KEYS = SUPPORTED_TECHS.keys()