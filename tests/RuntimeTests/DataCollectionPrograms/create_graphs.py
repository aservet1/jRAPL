import matplotlib.pyplot as plt
from os import listdir
from statistics import mean
from statistics import stdev

files = listdir()

stats = False

if "stats" in files:
    stats = True

files = [f for f in files if len(f.split('.')) == 2 and f.split('.')[1] == 'data']

if len(files) % 2 != 0:
    rows = len(files)
    columns = 1
    specs = {'hspace':.25}
    figsiz = (6,4*len(files))
else: # most likely 2x2
    rows = len(files) // 2
    columns = 2 
    specs = {'hspace':.6,'wspace':.5}
    figsiz = (6,4)

print(files)

fig, axs = plt.subplots(rows, columns, gridspec_kw= specs, figsize=figsiz )

#print(files)

i = 0
for ax in axs.flat:
    if (i >= len(files)): continue

    stats_fh = open('stats', 'a')    
    y = []
    fh = open(files[i], 'r')
    for datapoint in fh:
        y.append(int(datapoint))
    x = range(1, len(y)+1)
    ax.scatter(x, y, marker = ",", s = 1)
    ax.set_ylabel('Time (μs)')
    name = files[i].split('.')[0]
    ax.set_title(name)
    ax.set_xlabel('Trial number')
    #axs.flat[i].savefig(parts[0]+'_scatter')
    #axs.flat[i].clf()
    if(len(y) == 0):
        y.append(0)
        y.append(0)
    if not stats:
        stats_fh.write("stats for {}:\nmean: {}\nstddev: {}\n--------------------------\n".format(name, mean(y), stdev(y)))

    i += 1

#plt.figure(figsize=(6,4))
#plt.show()
plt.savefig('scatters')