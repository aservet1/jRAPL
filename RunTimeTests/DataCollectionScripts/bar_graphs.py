import matplotlib.pyplot as plt
from os import listdir
from os import chdir
from statistics import mean
from statistics import stdev


folders = listdir()

statdict = {}
for folder in folders:
    parent = folder
    chdir(parent)
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
    print("Too many stats files, I need only 2")
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
plt.show()
plt.savefig(keys[0] + '-' + keys[1] + '-bar_graph')