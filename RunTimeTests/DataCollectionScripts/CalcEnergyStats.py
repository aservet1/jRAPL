from statistics import mean
from statistics import stdev
from os import listdir
import matplotlib.pyplot as plt

files = listdir()
datadict = {}

for file in files:
    if(file.endswith(".data")):
        energies = []
        nonZeros = []
        times = []
        fh = open(file, 'r')
        for line in fh:
            data = line.split()
            try:
                energies.append(float(data[1]))
            except ValueError:
                print(data)
                continue
            try:
                times.append(int(data[2]))
            except IndexError:
                print(data)
                continue
            nonZeros.append(int(data[3]))
        datadict[file.split('.')[0]] = (energies, times, nonZeros)

fh = open("stats", 'w')
fh.write("--------------------------\n\n")
for item in datadict.keys():
    energies = datadict[item][1]
    times = datadict[item][0]
    nonZeros = datadict[item][2]
    meanTime = mean(energies)
    totalTime = sum(energies)
    stdevTime = stdev(energies)
    meanEnergy = mean(times)
    totalEnergy = sum(times)
    stdevEnergy = stdev(times)
    meanNonZeros = mean(nonZeros)
    stdevNonZeros = stdev(nonZeros)
    names = [('Time', 'μs'), ('Energy', 'J'), ('Reads_BW_Consecutive_NonZero_Readings','')]
    i = 0
    for y in (energies, times, nonZeros):
        x = range(1, len(y) + 1)
        plt.scatter(x, y, marker = ",", s = 1)
        plt.ylabel(names[i][0] + ' (' + names[i][1] + ')')
        plotname = item +'-'+ names[i][0]
        plt.title(plotname)
        plt.xlabel('Trial number')
        plt.savefig(plotname +'-scatter')
        plt.clf()
        i += 1

    fh.write("Stats for {}:\n--------------------------\nAverage energy change per non-zero reading: {}\nStdev of non-zero readings: {}\nAverage num readings between non-zero readings: {}\nStdev of num of readings between non-zero readings: {}\nAverage time between non-zero readings: {}μ\nStdev of time between non-zero readings: {}μ\n\nTotal energy consumed: {}\nTotal time taken: {}\n--------------------------\n\n".format(item,meanEnergy, stdevEnergy, meanNonZeros, stdevNonZeros, meanTime, stdevTime, totalEnergy, totalTime))
