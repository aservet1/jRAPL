from time import time

def energycheck_time():
    s = time()
    with open('/sys/class/powercap/intel-rapl:0/energy_uj') as fp: j = float(fp.read()) / 1000000
    e = time()
    # print('this energy get took '+str((e - s)*1000000)+' microseconds')
    return (e - s) * 1000000
	
for x in range(2500):
	print(int(energycheck_time()), end=' ')
print()
