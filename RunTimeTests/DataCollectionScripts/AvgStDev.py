from statistics import stdev
from statistics import mean
from sys import argv
from time import sleep
from random import randint
'''------------------------------------'''

def remove_non_numeric_characters(s):
	int_string = ''
	for c in s:
		if c.isnumeric():
			int_string += c
	return int_string


'''------------------------------------'''

if (len(argv) < 2):
	print("usage: python3 " + argv[0] + " inputFile")
	exit(1)

fileName = argv[1]

dataFile = open(fileName,'r')
stringData = dataFile.readlines()
dataFile.close()

intData = []
for s in stringData:
	s = s.strip()
	ints_from_s = remove_non_numeric_characters(s)
	if (ints_from_s == ''): continue
	intData.append(int(ints_from_s))

average = mean(intData)
standard_deviation = stdev(intData)

print('Results from file: \''+fileName+"\'")
print("Avg:\t",average)
print("StDev:\t",standard_deviation)
print('------------------------------')