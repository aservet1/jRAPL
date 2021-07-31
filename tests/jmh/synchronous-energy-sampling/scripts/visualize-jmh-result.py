#!/usr/bin/env python3

import json
from sys import argv

import math

import matplotlib.pyplot as plt
from matplotlib.ticker import FormatStrFormatter

def is_float(n):
    return n % 1 != 0
def is_float_list(l):
    counted_floats_in_list = sum([int(is_float(n)) for n in l])
    return counted_floats_in_list != 0

def plt_set_ax_limits(xmin, xmax, ymin, ymax):
    none = (None,None)
    if ((xmin,xmax)!=(none)):
        plt.xlim([xmin,xmax])
    if ((ymin,ymax)!=(none)):
        plt.ylim([ymin,ymax])

    precision = 1
    format_string = f'%.{precision}f'
    plt.gca().yaxis.set_major_formatter(FormatStrFormatter(format_string))

if len(argv) != 2:
    print('usage:',argv[0],'jmh-result-file-to-parse.json')
    exit(2)

with open(argv[1]) as f:
    _data = json.loads(f.read())

data = {}
for d in _data:
    name = d['benchmark'].split('.')[-1]
    data[name] = d

unit    =  [];
means   =  [];
labels  =  [];
errors  =  []; # not standard deviation. not sure what the error is

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

xmin, xmax = (None, None)
ymin, ymax = (32.5555, 35.5555)
plt_set_ax_limits(xmin, xmax, ymin, ymax)
# plt.yticks(range(ymin, ymax+1, (1 if (ymax % 1 == 0) else ymax % 1)))

# # def set_appropriate_yticks(): ----------------------------------- #--#
# ystep = 1 if (ymax % 1 == 0) else ymax % 1                          #--#
# ytick_range = range ( ymin, ymax + 1, ystep )                       #--#
# if (is_float_list(ytick_range)):                                    #--#
#     plt.gca().yaxis.set_major_formatter(FormatStrFormatter('%.2f')) #--#

# ----------------------------------------------------------------- #--#

plt.bar(
    range(len(labels)),
    means,
    yerr = errors,
    tick_label = labels,
    color = 'cadetblue',
    edgecolor = 'black',
    alpha = 1
);

plt.ylabel('Average Runtime ({})'.format(unit))
plt.xlabel('Sampling Version')
# plt.title('average sample runtime'.title())
plt.savefig('sync-samples-runtime')
