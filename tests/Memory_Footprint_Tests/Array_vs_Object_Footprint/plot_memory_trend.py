from sys import argv
import statistics
import matplotlib.pyplot as plt
'''------------------------------------------------------------------'''
def unbiased(data):
	unbiased = list()
	floor = data[0]
	unbiased.append(floor-data[0])
	for i in range(1,len(data)):
		unbiased.append(floor-data[i])
		if (data[i-1]+floor != data[i]):
			floor += data[i]
	return unbiased

def parse_memsamples(data):
	newdata = [d for d in data if not d[0].isalpha()]
	newdata = [d.strip() for d in newdata]
	newdata = ''.join(newdata)
	newdata = newdata.split(',')
	newdata = [int(d) for d in newdata]
	return newdata

def parse_avg(data):
	for i in range(len(data)-1,-1,-1):
		if 'avg' in data[i]: return data[i].strip()
	return None

def parse_stdev(data):
	for i in range(len(data)-1,-1,-1):
		if 'stdev' in data[i]: return data[i].strip()
	return None

def parse_lifetime(data):
	for d in data[len(data)-1:0:-1]:
		if 'lifetime' in d: return d.strip()

def average_list(all_file_names, category):
	filehandles 	= [ open(name) for name in all_file_names if category in name ]
	alldata 	= [ filehandle.readlines() for filehandle in filehandles ]
	for f in filehandles: f.close()	

	alldata 	= [ parse_memsamples(data) for data in alldata ]
	truncate	= min([len(data) for data in alldata])
	alldata		= [ data[:truncate] for data in alldata ]

	average = list()
	for i in range(len(alldata[0])):
		average.append( statistics.mean([ data[i] for data in alldata ]) )
	
	return average
	

'''------------------------------------------------------------------'''

def plot_it(data,name):
	y = data
	x = range(1,len(y)+1)
	plt.plot(x,y,label=name)
	plt.legend(loc='upper left')


'''------------------------------------------------------------------'''

arr_averageList = average_list(argv[1:],'Array')
obj_averageList = average_list(argv[1:],'Object')

plot_it(arr_averageList,'Array')
plot_it(obj_averageList,'Object')

plt.xlabel('step')
plt.ylabel('memory (bytes)')
plt.title('memory use trend comparison')
plt.show()
