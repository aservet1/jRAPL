import matplotlib.pyplot as plt
from os import listdir
from os import chdir
from statistics import mean
from statistics import stdev
import sys

#thanks, stackoverflow :)
def add_value_labels(ax, spacing=5):
    """Add labels to the end of each bar in a bar chart.

    Arguments:
        ax (matplotlib.axes.Axes): The matplotlib object containing the axes
            of the plot to annotate.
        spacing (int): The distance between the labels and the bars.
    """

    # For each bar: Place a label
    for rect in ax.patches:
        # Get X and Y placement of label from rect.
        y_value = rect.get_height()
        x_value = rect.get_x() + rect.get_width() / 2

        # Number of points between bar and label. Change to your liking.
        space = spacing
        # Vertical alignment for positive values
        va = 'bottom'

        # If value of bar is negative: Place label below bar
        if y_value < 0:
            # Invert space to place label below
            space *= -1
            # Vertically align label at top
            va = 'top'

        # Use Y value as label and format number with one decimal place
        label = "{:.1f}".format(y_value)

        # Create annotation
        ax.annotate(
            label,                      # Use `label` as label
            (x_value, y_value),         # Place label at end of the bar
            xytext=(0, space),          # Vertically shift label by `space`
            textcoords="offset points", # Interpret `xytext` as offset in points
            ha='center',                # Horizontally center label
            va=va)                      # Vertically align label differently for
                                        # positive and negative values

'''-----------------------------------------------------------------------------------'''

folders = sys.argv

if(len(folders) != 3):
    print("USAGE: python3 bar_graphs.py folder1 folder2")
    exit(1)

del folders[0]

statdict = {}
for folder in folders:
    parent = folder
    try:
        chdir(parent)
    except FileNotFoundError as e:
        print("\x1B[31m{} is not a folder\x1B[0m\n\n\x1B[32mUSAGE: python3 bar_graphs.py folder1 folder2\x1B[0m".format(folder))
        exit(0)

    files = listdir()
    for file in files:
        if(file == 'stats'):
            fh = open(file, 'r')
            means = []
            stddevs = []
            names = []
            for line in fh:
                words = line.split()
                if(line.startswith('-')):
                    continue
                elif(line.startswith('stats')):
                    names.append(words[-1][0:-1])
                elif(line.startswith('mean')):
                    means.append(float(words[-1]))
                elif(line.startswith('stddev')):
                    stddevs.append(float(words[-1]))
            statdict[parent] = (names, means, stddevs)
    chdir("..")


fig, ax = plt.subplots()
keys = list(statdict.keys())
if(len(keys) != 2):
    print("Too few stats files, I need 2")
    exit(1)
key1_ind = [item for item in range(3)]
key2_ind = [(item + 0.35) for item in range(3)]
xticks = [(item + 0.35/2) for item in range(3)]
p1 = ax.bar(key1_ind, statdict[keys[0]][1], width = 0.35, yerr = statdict[keys[0]][2])
p2 = ax.bar(key2_ind, statdict[keys[1]][1], width = 0.35,  yerr = statdict[keys[1]][2])
ax.set_title('Timing comparison for ' + keys[0] + " and " + keys[1])
ax.set_xticks(xticks)
ax.set_xticklabels(statdict[keys[0]][0])
ax.legend((p1[0], p2[0]), (keys[0], keys[1]))
ax.autoscale_view()
add_value_labels(ax)
plt.savefig(keys[0] + '-' + keys[1] + '-bar_graph')
