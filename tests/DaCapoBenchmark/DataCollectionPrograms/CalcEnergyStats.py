from os import listdir
from statistics import mean
from statistics import stdev
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

	meanTime = mean(energies) if len(energies) else 0
	totalTime = sum(energies) if len(energies) else 0
	stdevTime = stdev(energies) if len(energies) else 0

	meanEnergy = mean(times) if len(times) else 0
	totalEnergy = sum(times) if len(times) else 0
	stdevEnergy = stdev(times) if len(times) else 0

	meanNonZeros = mean(nonZeros) if len(nonZeros) else 0
	stdevNonZeros = stdev(nonZeros) if len(nonZeros) else 0

	names = [('Time', 'μs'), ('Energy', 'J'), ('Reads_Btwn_Consecutive_NonZero_Readings','')]
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

	fh.write("""Stats for {}:
		\n--------------------------\n
		Average energy change per non-zero reading: {}\n
		Stdev of non-zero readings: {}\n
		Average num readings between non-zero readings: {}\n
		Stdev of num of readings between non-zero readings: {}\n
		Average time between non-zero readings: {}μ\n
		Stdev of time between non-zero readings: {}μ\n
		\n
		Total energy consumed: {}\n
		Total time taken: {}\n--------------------------\n\n"""
			.format(item, meanEnergy, stdevEnergy, meanNonZeros, stdevNonZeros,
					meanTime, stdevTime, totalEnergy, totalTime))



