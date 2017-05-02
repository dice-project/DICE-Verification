'''
Created on Jan 7, 2017

@author: francesco
'''
import os
import errno


def make_sure_path_exists(path):
    """"Check if the provided path exists. If it does not exist, create it."""
    try:
        os.makedirs(path)
    except OSError as exception:
        if exception.errno != errno.EEXIST:
            raise


def get_grid_dimensions(num_elems):
    """ Given the number of elements to display,
        determines the number of  rows and columns"""
    rows = cols = 1
    while rows * cols < num_elems:
        if rows == cols:
            rows += 1
        else:
            cols += 1
    return rows, cols

