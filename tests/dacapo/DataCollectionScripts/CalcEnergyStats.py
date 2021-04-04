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

#text stats
for item in datadict.keys():   #dram, core, pkg, gpu
	energies = datadict[item][1]
	times = datadict[item][0]
	nonZeros = datadict[item][2]

	if len(energies): meanEnergy = mean(energies)
	else: meanTime = 0
	if len(energies): totalEnergy = sum(energies)
	else: totalTime = 0
	if len(energies) > 1: stdevEnergy = stdev(energies)
	else: stdTime = 0

	if len(times): meanTime = mean(times)
	else: meanTime = 0
	if len(times): totalTime = sum(times)
	else: totalTime = 0
	if len(times) > 1: stdevTime = stdev(times) 
	else: stdevTime = 0

	if len(nonZeros) > 1:
		meanNonZeros = mean(nonZeros)
		stdevNonZeros = stdev(nonZeros)
	else: 
		meanNonZeros = 0
		stdevNonZeros = 0

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

 
#plot stats 
measurings = [('Time_Between_Nonzero_Readings', 'μs'), ('Energy', 'J'), ('Reads_Btwn_Consecutive_NonZero_Readings','')]
titles_idx = 0

powerdomains = list(datadict.keys())

for pd in powerdomains:
	fig, axs = plt.subplots(3,1,gridspec_kw={'hspace':.25},figsize=(6,12))
	fig.suptitle(pd)

	energies = datadict[pd][1]
	times = datadict[pd][0]
	nonZeros = datadict[pd][2]

	arrays = [energies, times, nonZeros]

	i = 0
	for ax in axs.flat:
		measuring = measurings[i]
		ax.set_title(measuring[0])
		ax.set_ylabel = measuring[0].split('_')[0]
		y = arrays[i]
		x = range(1,len(y)+1)
		ax.scatter(x,y,marker=',',s=1)
		i += 1

	plt.savefig(pd+"-scatter")

	plt.clf()




'''
titles = [('Time_Between_Nonzero_Readings', 'μs'), ('Energy', 'J'), ('Reads_Btwn_Consecutive_NonZero_Readings','')]
titles_idx = 0
for y in (energies, times, nonZeros):

	fig, axs = plt.subplots(2,2,gridspec_kw={'hspace':0.5,'wspace':0.5})
	fig.suptitle(titles[titles_idx][0])

	power_domains = list(datadict.keys())
	pd_idx = 0
	for ax in axs.flat:
		power_domain = power_domains[pd_idx]; pd_idx += 1
		x = range(1, len(y) + 1)
		ax.scatter(x, y, marker = ",", s = 1)
		ax.set_ylabel(titles[titles_idx][0].split('_')[0] + ' (' + titles[titles_idx][1] + ')')
		ax.set_title(power_domain)
		ax.set_xlabel('Trial number')
		#ax.savefig(plotname +'-scatter')
		#plt.clf()
	
	plt.savefig(titles[titles_idx][0])
	plt.clf
	titles_idx += 1
'''





'''
for item in datadict.keys():   #dram, core, pkg, gpu
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
'''


