'''
Created on Jan 24, 2017

@author: francesco
'''
from dia_verification import VerificationResult
from dia_verification import VerificationTrace

import os
from datetime import datetime as dt


class ZotResult(VerificationResult):
    '''
    classdocs
    '''
# standard result file for zot (containing verification outcome)
    result_file = 'output.1.txt'
    # standard history file for zot (containing output trace)
    hist_file = 'output.hist.txt'
    timestamp_format = "%Y-%m-%d__%H-%M-%S"

    def __init__(self, result_dir):
        '''
        Constructor
        '''
        self.result_dir = result_dir.rstrip('/')+'/'
        self.result_file_path = self.result_dir+self.result_file
        res_f = open(self.result_file_path)
        lines = res_f.readlines()
        self.outcome = None
        self.verification_time = None
        self.timestamp = None
        self.timestamp_str = None
        if lines:
            self.outcome = lines[0].strip()
            self.verification_time = (float(lines[-1]
                                            .strip()
                                            .strip(':')
                                            .strip(')')
                                            .split(" ")[-1]))
            if(self.outcome == 'sat'):
                print 'Outcome is {} '.format(self.outcome.upper())
                # app_dir+'/'+'output.hist.txt'
                my_file_path = self.result_dir+self.hist_file
                print 'I AM in {}'.format(os.getcwd())
                # GETTING LAST MODIFIED DATE FROM THE FILE
                moddate_seconds = os.stat(my_file_path)[8]  # 10 attributes
                self.timestamp = dt.fromtimestamp(moddate_seconds)
                self.timestamp_str = (self.timestamp
                                      .strftime(self.timestamp_format))
            elif(self.outcome == 'unsat'):
                print 'Outcome is {} '.format(self.outcome.upper())
            else:
                print ("Outcome: {} -> there may be a problem. See {}"
                       .format(self.outcome, self.result_file))
            print "Verification time: {}".format(self.verification_time)
#            print 'Result saved to ', app_dir, ' directory.'


class ZotTrace(VerificationTrace):
    '''
    classdocs
    '''

    def __init__(self, output_trace_file_path):
        '''
        Constructor
        Parse history trace and save variable values in a dictionary.
        '''
        step = -1
        records = {}
        bool_set = set()
        f = open(output_trace_file_path)
        for line in f:
            line = line.strip()   # strip out carriage return
            if line.startswith("------ time"):
                step += 1
    #            print("------- STEP {0} -------".format(step))
            elif line.startswith("------ end"):
                pass
            elif line.strip() == "**LOOP**":
                records["LOOP"] = step
            else:
                key_value = line.split(" = ")  # split line into key and value
                key = key_value[0]   # key is first item in list
                if len(key_value) > 1:  # counter variable
                    value = key_value[1]   # value is 2nd item
                else:  # boolean variable (only key in trace)
                    value = step
                    bool_set.add(key)
                # print key
                if key in records:
                    records[key].append(value)
                else:
                    records[key] = [value]
        self.bool_set = bool_set
        self.records = records
        self.time_bound = step
