# To add a new cell, type '# %%'
# To add a new markdown cell, type '# %% [markdown]'
# %%
import json
import pandas as pd
import matplotlib.pyplot as plt
import os


# %%
def MB(bytes):
    return bytes / 1024 / 1024


# %%
path = "/home/alejandro/jRAPL/tests/dacapo/async-monitors/tmp/throwawayfiles"#results/jolteon-results"
files = []
os.chdir(path)
for file in os.listdir():
    if file.endswith(".memory.json"):
        files.append(file)


# %%
iterations = {}
benches = set()
dses = set()
for file in files:
    parts = file.split('_')
    benches.add(bench := parts[0])
    iteration = parts[1]
    dses.add(ds := parts[2])
    if iteration not in iterations:
        iterations[iteration] = {}
    iteration = iterations[iteration]
    if bench not in iteration:
        iteration[bench] = {}
    bench = iteration[bench]
    if ds not in bench:
        bench[ds] = {}
    ds = bench[ds]
    with open(file) as fh:
        data = json.load(fh)
        for key in data.keys():
            ds[key] = data[key]


# %%
for benchmark in benches:
    os.mkdir(benchmark)


# %%
for iteration in iterations:
    for bench in benches:
        data = iterations[iteration][bench]
        xs = []
        dses_ = []
        ys = []
        fname = f"{bench}/{iteration}_Mem_Comparison"
        print(fname)
        for ds in data:
            data_ = data[ds]
            dses_.append(ds)
            ys.append([MB(sam) for sam in data_["samples"]]) # megabytes
            num_sam = data_["num_samples"]
            frequency = data_["sampling_rate"]
            xs.append(stamps := range(0, num_sam))
            # xs.append(data_["timestamps"])
        for i in range(len(xs)):
            x = xs[i]
            y = ys[i]
            ds = dses_[i]
            plt.plot(x, y, label=ds)
        plt.legend()
        plt.title(fname)
        plt.xlabel("timestamps (ms)")
        plt.ylabel("memory consumed")
        plt.savefig(fname, dpi=400)
        plt.cla()
        plt.clf()

            
        


