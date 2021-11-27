#!/usr/bin/env python3

import json
import pandas as pd
from sys import argv
from io import StringIO

def usage_abort():
    print("usage:",argv[0],"<list of json data files> <result_dir>")
    print("  assumed that JSON data files will be named whatever the metric is, and will be well-formed")
    exit(2)

def json_files(filenames):
    return len(filenames) == sum([ int(f.endswith('.json')) for f in filenames ])

def to_csv(item):
    return item['title'] + ',' + ','.join([ trunc_str(item['data'][k]['avg']) + ' +/- ' + trunc_str(item['data'][k]['stdev']) for k in sorted(item['data'].keys())[::-1] ])

def trunc_str(n):
    return "%.3f" % n

try:
    data_files = argv[1:-1]
    result_dir = argv[-1]
except IndexError:
    usage_abort()
if len(data_files) == 0 or not json_files(data_files):
    usage_abort()

# probably use pandas to turn this into LaTeX. maybe we can come up with a pretty image format. look into prettifying pd.DataFrame.to_latex(). maybe they have themes

data = []
for data_file in data_files:
    title = '.'.join(data_file.split('.')[:-1]).split('/')[-1].replace('-',' ').title()
    with open(data_file) as fd:
        jsondata = json.load(fd)
        data.append( {
            "title": title,
            "data": jsondata['overall']
        } )

prettier_monitor_types = {
    'java': 'Java',
    "c-linklist": "C Linked List",
    "c-dynamicarray": "C Dynamic Array"
}

csv = ''
csv += ( ' ,' + ','.join( [ prettier_monitor_types[m] for m in sorted(data[0]['data'].keys())[::-1] ] ) + '\n' )
csv += ( '\n'.join([ to_csv(item) for item in data ]) )

df = pd.read_csv( StringIO(csv) )
print(df.to_latex())