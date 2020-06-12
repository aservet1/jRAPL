from sys import argv
import matplotlib.pyplot as plt

def make_scatter(readings,name):
	y = readings
	x = range(1,len(y)+1)
	plt.scatter(x,y, marker=',', s = 1)
	plt.xlabel("the progression of time")
	plt.ylabel("consumption over delay (joules)")
	plt.savefig(name+"_scatter")
	plt.clf()


def make_lineplot(readings,name):
	y = readings
	x = range(1,len(y)+1)
	plt.plot(x,y)
	plt.xlabel("the progression of time")
	plt.ylabel("consumption over delay (joules)")
	plt.savefig(name+"_lineplot")
	plt.clf()

filename = argv[1]

fh = open(filename, 'r')
lines = fh.readlines()
fh.close()

delay = int(lines[0])

dram = list(); core = list(); pkg = list()
for i in range(1,len(lines)): #validate format?
	line = lines[i]
	if line == '\n': continue
	parts = line.split('\t')
	dram.append(float(parts[0]))
	core.append(float(parts[1]))
	pkg.append(float(parts[2]))

make_scatter(dram,'dram'); make_lineplot(dram,'dram')
make_scatter(core,'core'); make_lineplot(core,'core')
make_scatter(pkg,'pkg'); make_lineplot(pkg,'pkg')
