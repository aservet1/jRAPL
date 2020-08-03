from sys import stdin
import statistics
''' ------------------------------------------------------------------ '''
'''   assumed stdin format string "cdata@@@@@jdata" and each data is   '''
'''   [dram/gpu#####core#####pkg#####] and each reading field is a     '''
'''   space separated list of float strings                            '''
''' ------------------------------------------------------------------ '''
data = stdin.read()

data = data.split("@@@@@")
data = [d.split('#####') for d in data]

cdata = data[0]; jdata = data[1]
cdata = [d.split() for d in cdata]; jdata = [d.split() for d in jdata]


cdata = list(filter(lambda x : x != 0,[[float(d) for d in data] for data in cdata]))
jdata = list(filter(lambda x : x != 0,[[float(d) for d in data] for data in jdata]))

for i in range(3):
    cmean = statistics.mean(cdata[i]); jmean = statistics.mean(jdata[i])
    cstdev = statistics.stdev(cdata[i]); jstdev = statistics.stdev(jdata[i])
    print("Nonzero energy sample picked up by thread (joules): C // Java")
    print(f"  mean: \t{cmean} // {jmean}")
    print(f"  stdev:\t{cstdev} // {jstdev}")
    print(f"cdata[{i}] sample size: "+str(len(cdata[i])))
    print(f"jdata[{i}] sample size: "+str(len(jdata[i])))
    print("-------------------------------")
