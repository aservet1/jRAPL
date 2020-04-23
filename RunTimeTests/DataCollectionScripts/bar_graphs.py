import matplotlib.pyplot as plt
from os import listdir
from os import chdir
from statistics import mean
from statistics import stdev
import sys




folders = sys.argv

if(len(folders) != 3):
    print("USAGE: python3 bar_graphs.py folder1 folder2")
    exit(1)

del folders[0]

statdict = {}
for folder in folders:
    parent = folder
    try:
        chdir(parent)
    except FileNotFoundError as e:
        print("\x1B[31m{} is not a folder\x1B[0m\n\n\x1B[32mUSAGE: python3 bar_graphs.py folder1 folder2\x1B[0m".format(folder))
        exit(0)

    files = listdir()
    for file in files:
        if(file == 'stats'):
            fh = open(file, 'r')
            means = []
            stddevs = []
            names = []
            for line in fh:
                words = line.split()
                if(line.startswith('-')):
                    continue
                elif(line.startswith('stats')):
                    names.append(words[-1][0:-1])
                elif(line.startswith('mean')):
                    means.append(float(words[-1]))
                elif(line.startswith('stddev')):
                    stddevs.append(float(words[-1]))
            statdict[parent] = (names, means, stddevs)
    chdir("..")


fig, ax = plt.subplots()
keys = list(statdict.keys())
if(len(keys) != 2):
    print("Too few stats files, I need 2")
    exit(1)
key1_ind = [item for item in range(4)]
key2_ind = [(item + 0.35) for item in range(4)]
xticks = [(item + 0.35/2) for item in range(4)]
p1 = ax.bar(key1_ind, statdict[keys[0]][1], width = 0.35, yerr = statdict[keys[0]][2])
p2 = ax.bar(key2_ind, statdict[keys[1]][1], width = 0.35,  yerr = statdict[keys[1]][2])
ax.set_title('Timing comparison for ' + keys[0] + " and " + keys[1])
ax.set_xticks(xticks)
ax.set_xticklabels(statdict[keys[0]][0])
ax.legend((p1[0], p2[0]), (keys[0], keys[1]))
ax.autoscale_view()
plt.savefig(keys[0] + '-' + keys[1] + '-bar_graph')