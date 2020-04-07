import matplotlib.pyplot as plt
from os import listdir
from statistics import mean
from statistics import stdev

files = listdir()



for file in files:
    parts = file.split('.')
    if(len(parts) == 1):
        continue
    stats_fh = open('stats', 'a')
    if(parts[1] == 'data'):
        y = []
        fh = open(file, 'r')
        for datapoint in fh:
            y.append(int(datapoint))
        x = range(1, len(y)+1)
        plt.scatter(x, y, marker = ",", s = 1)
        plt.ylabel('Time (μs)')
        plt.title(parts[0])
        plt.xlabel('Trial number')
        plt.savefig(parts[0]+'_scatter')
        plt.clf()
        if(len(y) == 0):
            y.append(0)
            y.append(0)
        stats_fh.write("stats for {}:\nmean: {}\nstddev: {}\n--------------------------\n".format(parts[0], mean(y), stdev(y)))


