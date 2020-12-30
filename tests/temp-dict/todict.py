import re
from sys import argv

def toDict(data):
	lines = data.split('\n')
	samplingRate = lines[0]
	header = [ re.sub('\(.*\)','',h) for h in lines[1].split(',') ]
	d = { h:[] for h in header }

	for i in range(2,len(lines)):
		line = lines[i].split(',')
		for j in range(len(header)):
			try:
				d[header[j]].append(float(line[j]))
			except:
				pass	

	print(d)






with open(argv[1]) as file:
	data = file.read()

toDict(data)
