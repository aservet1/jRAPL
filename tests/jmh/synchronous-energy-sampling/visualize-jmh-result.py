#!/usr/bin/env python3

import json
from sys import argv
import matplotlib.pyplot as plt

if len(argv) != 2:
    print('usage:',argv[0],'jmh-result-file-to-parse.json')
    exit(2)

with open(argv[1]) as f:
    _data = json.loads(f.read())

data = {}
for d in _data:
    name = d['benchmark'].split('.')[-1]
    data[name] = d

labels = []
means = []
errors = [] # not standard deviation. not sure what the error is
unit = []

for name in data:
    if name.startswith('time'):
        labels.append(name.replace('time','',1))
    else:
        labels.append(name)
    means.append( data[name]['primaryMetric']['score'] )
    errors.append( data[name]['primaryMetric']['scoreError'] )
    unit.append( data[name]['primaryMetric']['scoreUnit'] )

assert(sorted(unit) == sorted(unit)[-1::-1]) # idiom for 'all items equal'
unit = unit[0]

print(labels)
print(means)
print(errors)
print('units:',unit)

plt.bar(range(len(labels)), means, yerr=errors, tick_label=labels)
plt.ylabel('average runtime: '+str(unit))
plt.xlabel('sampling version')
plt.title('average runtime for synchronous energy sampling')
plt.savefig('sync-samples-runtime')
