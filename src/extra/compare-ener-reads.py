from sys import stdin
import statistics
'''--------------------------------------'''

data = stdin.read() #assumed incoming format of [cdata@@@@@jdata]

#print(data)
data = data.split("@@@@@")
#print(data)
data = [d.split(' ') for d in data]
data = [list(filter(lambda x : x != '',d)) for d in data]
#print(data)

# remove "zero" readings?	
cdata = list(filter(lambda x : x != 0,[float(d) for d in data[0]]))
jdata = list(filter(lambda x : x != 0,[float(d) for d in data[1]]))

print("Stats: C // Java")
print("  mean: \t"+str(statistics.mean(cdata))+" // "+str(statistics.mean(jdata)))
print("  stdev:\t"+str(statistics.stdev(cdata))+" // "+str(statistics.stdev(jdata)))
