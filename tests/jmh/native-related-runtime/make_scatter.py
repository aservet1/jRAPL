import matplotlib.pyplot as plt
from matplotlib.patches import Rectangle
import sys

for i in range(1, len(sys.argv)):
    fname = sys.argv[i]
    data = {}
    with open(fname) as fh:
        n = 0
        sum_ = 0
        for line in fh:
            k, v = [int(i) for i in line.split()]
            data[k] = v
            n += v
            sum_ += k*v
        mean = sum_/n
        sq_sum = 0
        for time in data:
            sq_sum += ((time - mean)**2)*data[time]
        sd = (sq_sum / n)**0.5
        filtered = {}
        outliers = {}
        for time in data:
            if time <= 3*sd:
                filtered[time] = data[time]
            else:
                outliers[time] = data[time]
        plt.bar(list(filtered.keys()), filtered.values())
        extra1 = Rectangle((0, 0), 1, 1, fc="w", fill=False, edgecolor='none', linewidth=0)
        extra2 = Rectangle((0, 0), 1, 1, fc="w", fill=False, edgecolor='none', linewidth=0)
        plt.legend()
        title = fname.split('.')[0]
        plt.title(title)
        plt.legend([extra1, extra2], (f"σ: {sd}µ", f"x̄: {mean}µ"))
        plt.xlabel("ms")
        plt.ylabel("num calls with time")
        plt.savefig(title)