from math import sqrt
from sys import argv
from time import sleep
from random import randint
'''------------------------------------'''
def avg(l):
	return sum(l)/len(l)

def deviations(l):
	av = avg(l)
	devs = []
	for x in l:
		devs.append(x-av)
	return devs

def stdev(l):
	devs = deviations(l)
	S = 0
	for d in devs:
		S += d**2
	S /= (len(l)-1)
	S = sqrt(S)
	return S

'''------------------------------------'''

if (len(argv) < 2):
	print("need to pass file of ints to read as arg")
	exit(1)

fileName = argv[1]

dataFile = open(fileName,'r')
stringData = dataFile.readlines()
dataFile.close()

intData = []
for s in stringData:
	s = s.strip()
	i = int(s)
	intData.append(i)

average = avg(intData)
standard_deviation = stdev(intData)

print('Results from file: \''+fileName+"\'")
print("Avg:\t",average)
print("StDev:\t",standard_deviation)
