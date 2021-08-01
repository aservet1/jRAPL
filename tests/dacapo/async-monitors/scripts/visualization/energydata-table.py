#!/usr/bin/env python3

import json
import pandas as pd
from sys import argv
from io import StringIO

def usage_abort():
    print("usage:",argv[0],"<json energy data file> <result_dir>")
    print("  assumed that JSON data file will be named whatever the metric is, and will be well-formed")
    exit(2)

def to_csv(data):
    prettier_monitor_types = {
        'java': 'Java',
        "c-linklist": "C Linked List",
        "c-dynamicarray": "C Dynamic Array"
    }
    prettier_power_domain = lambda pd: pd.split('_')[0].upper() + " " + '_'.join(pd.split('_')[1:])

    headerlist = sorted (
        data [
            list(data.keys())[0]
        ] .keys()
    ) [::-1]

    body = (
        '\n'.join([ 
            prettier_power_domain(power_domain) + ',' +
                ','.join([
                    trunc_str(data[power_domain][m]['avg']) + " +/- " + trunc_str(data[power_domain][m]['stdev'])
                    for m in headerlist
                ])
            for power_domain in sorted(data.keys())
        ])
    )

    head = ','.join([ prettier_monitor_types[h] for h in headerlist])

    return '\n'.join((head,body))

def to_LaTeX(data):
    prettier_monitor_types = {
        'java': 'Java',
        "c-linklist": "C Linked List",
        "c-dynamicarray": "C Dynamic Array"
    }
    prettier_power_domain = lambda pd: pd.split('_')[0].upper() + " " + '_'.join(pd.split('_')[1:])

    headerlist = sorted (
        data [
            list(data.keys())[0]
        ] .keys()
    ) [::-1]

    body = (
        '\t' + '\n\t'.join([ 
            prettier_power_domain(power_domain) + ' & ' +
                ' & '.join([
                    trunc_str(data[power_domain][m]['avg']) + " +/- " + trunc_str(data[power_domain][m]['stdev'])
                    for m in headerlist
                ]) + r"\\ \hline"
            for power_domain in sorted(data.keys())
        ]) + r' \hline'
    )

    head = (
        r'\hline ' + '& ' +
            ' & '.join([ prettier_monitor_types[h] for h in headerlist]) + r'\\'
        r'\hline\hline'
    )

    return (
        r'\begin{tabular} {|' + ''.join(['l|' for _ in range(len(headerlist)+1)]) + '}\n\t' +
            '\n'.join((head,body)) + '\n'
        r'\end{tabular}'
    )

def trunc_str(n):
    return "%.6f" % n

try:
    data_file  = argv[1]
    result_dir = argv[2]
except IndexError:
    usage_abort()


with open(data_file) as fd:
    data = json.load(fd)['overall']

# csv = to_csv(data)
# df = pd.read_csv( StringIO(csv) )
# print(df.to_latex())

print(to_LaTeX(data))