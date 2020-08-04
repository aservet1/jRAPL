from sys import argv
import statistics
'''-----------------------------------------------'''
def read_file_to_string(filename):
    fh = open(filename)
    data = fh.read()
    fh.close()
    return data

def make_numeric_array(data):
    data = data.split("\n")
    data = data[2:len(data)]
    data = list(filter(lambda x : x != '', data))
    data = [line.split("\t") for line in data]
    data = [[float(item) for item in line] for line in data]
    return data

def group_by_column(data):
    columns = list()
    for i in range(len(data[0])):
        columns.append([line[i] for line in data])
    return columns

# zero readings just mean the next reading hasn't been updated in
# the register so they are irrelevant to comparing the actual energy
# level reported assumed already grouped by column
def remove_zeroes(data):
    return list(filter(lambda x: x != 0,data))
'''-----------------------------------------------'''
if len(argv) != 3:
    print(f"usage: python3 {argv[0]} c_data_file java_data_file")
    exit(1)

cdata = read_file_to_string(argv[1])
jdata = read_file_to_string(argv[2])

cdata = remove_zeroes(group_by_column(make_numeric_array(cdata)))
jdata = remove_zeroes(group_by_column(make_numeric_array(jdata)))

#assume cdata and jdata have 3 columns dram, core, pkg
for i in range(3):
    cmean = statistics.mean(cdata[i]); jmean = statistics.mean(jdata[i])
    cstdev = statistics.stdev(cdata[i]); jstdev = statistics.stdev(jdata[i])
    print("Nonzero energy sample picked up by thread (joules): C // Java")
    print(f"  mean: \t{cmean} // {jmean}")
    print(f"  stdev:\t{cstdev} // {jstdev}")
    print(f"cdata[{i}] sample size: "+str(len(cdata[i])))
    print(f"jdata[{i}] sample size: "+str(len(jdata[i])))
    print("-------------------------------")
    
'''
print(cdata)
print('#########')
print(jdata)
'''

''' ------------------------------------------------------------------ '''
'''   assumed stdin format string "cdata@@@@@jdata" and each data is   '''
'''   [dram/gpu#####core#####pkg#####] and each reading field is a     '''
'''   space separated list of float strings                            '''
''' ------------------------------------------------------------------ '''

'''
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
'''
