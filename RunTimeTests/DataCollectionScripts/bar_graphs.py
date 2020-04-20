import matplotlib.pyplot as plt
from os import listdir
from statistics import mean
from statistics import stdev


files = listdir()

statdict = {}
for file in files:
    if(file.endswith('stats')):
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
        statdict[file.split('_')[0]] = (names, means, stddevs)

fig, ax = plt.subplots()
rutvik_ind = [item for item in range(4)]
alejandro_ind = [(item + 0.35) for item in range(4)]
xticks = [(item + 0.35/2) for item in range(4)]
p1 = ax.bar(rutvik_ind, statdict['Rutvik'][1], width = 0.35, yerr = statdict['Rutvik'][2])
p2 = ax.bar(alejandro_ind, statdict['Alejandro'][1], width = 0.35,  yerr = statdict['Alejandro'][2])
ax.set_title('Timings of MSR read on Rutvik\'s and Alejandro\'s computers')
ax.set_xticks(xticks)
ax.set_xticklabels(statdict['Rutvik'][0])
ax.legend((p1[0], p2[0]), ('Rutvik', 'Alejandro'))
ax.autoscale_view()

plt.show()