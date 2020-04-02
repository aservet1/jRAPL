from statistics import mean
from statistics import stdev
from os import listdir

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
                energies.append(float(data[0]))
            except ValueError:
                print(data)
                continue
            try:
                times.append(int(data[1]))
            except IndexError:
                print(data)
                continue
            nonZeros.append(int(data[2]))
        datadict[file.split('.')[0]] = (energies, times, nonZeros)

print(sum(datadict['DRAM'][1]))
for item in datadict.keys():
    fname = item + ".stats"
    fh = open(fname, 'w')
    meanTime = mean(datadict[item][1])
    stdevTime = stdev(datadict[item][1])
    meanEnergy = mean(datadict[item][0])
    stdevEnergy = stdev(datadict[item][0])
    meanNonZeros = mean(datadict[item][2])
    stdevNonZeros = stdev(datadict[item][2])
    fh.write("Average energy change per non-zero reading: {}\nStdev of non-zero readings: {}\nAverage num readings between non-zero readings: {}\nStdev of num of readings between non-zero readings: {}\nAverage time between non-zero readings: {}μ\nStdev of time between non-zero readings: {}μ".format(meanEnergy, stdevEnergy, meanNonZeros, stdevNonZeros, meanTime, stdevTime))
    