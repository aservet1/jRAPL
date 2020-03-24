import matplotlib.pyplot as plt
from os import listdir

files = listdir()



for file in files:
    parts = file.split('.')
    if(len(parts) == 1):
        continue
    if(parts[1] == 'data'):
        y = []
        fh = open(file, 'r')
        for datapoint in fh:
            y.append(int(datapoint))
        x = [item for item in range(1, len(y)+1)]
        plt.scatter(x, y, marker = ",", s = 1)
        plt.ylabel('Time (μs)')
        plt.title(parts[0])
        plt.xlabel('Trial number')
        plt.savefig(parts[0])
        plt.clf()
