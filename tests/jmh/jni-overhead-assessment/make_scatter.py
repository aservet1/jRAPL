import matplotlib.pyplot as plt
import sys

for i in range(1, len(sys.argv)):
    fname = sys.argv[i]
    data = {}
    with open(fname) as fh:
        for line in fh:
            k, v = [int(i) for i in line.split()]
            data[k] = v;
        plt.bar(list(data.keys()), data.values())
        plt.title(fname)
        plt.xlabel("ms")
        plt.ylabel("num calls with time")
        plt.show()