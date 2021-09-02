import os
import json
import matplotlib.pyplot as plt
from matplotlib.ticker import FormatStrFormatter


def get_data_files(metric):
    # the subplot grid will be laid out visually like how this list is laid out
    return [
        'results/SystemA/samplingrate_1/'+metric+'.json', 'results/SystemB/samplingrate_1/'+metric+'.json',
        'results/SystemA/samplingrate_2/'+metric+'.json', 'results/SystemB/samplingrate_2/'+metric+'.json',
        'results/SystemA/samplingrate_4/'+metric+'.json', 'results/SystemB/samplingrate_4/'+metric+'.json'
    ]

output_dir = 'results/overall-plots'

def validate_output_dir(output_dir):
    if not (output_dir.startswith("/") or output_dir.startswith("~")):
        output_dir = os.path.join(os.getcwd(),output_dir)
    if not os.path.isdir(output_dir):
        print("directory",output_dir,"does not exist")
        exit(2)

def plt_set_axis_limits(xrange, yrange, xaxis_precision, yaxis_precision):
    none = (None,None)
    if ((xrange)!=(none)):
        plt.gca().set(xlim = xrange)
        if xaxis_precision != 0:
            plt.gca().xaxis.set_major_formatter(FormatStrFormatter('%.'+str(xaxis_precision)+'f'))
    if ((yrange)!=(none)):
        plt.gca().set(ylim = yrange)
        if yaxis_precision != 0:
            plt.gca().yaxis.set_major_formatter(FormatStrFormatter('%.'+str(yaxis_precision)+'f'))

'''
data is a tree (well, a dictionary).
return the subtree rooted at the end of the keypath
'''
def dictionary_subtree(data, keypath):
    for key in keypath:
        data = data[key]
    return data

def megaplot(data_files, keypath=[], color='blue', edgecolor='black', alpha=1):
    fig, axs = plt.subplots (
        nrows=3,
        ncols=2,
        sharex=True,
        sharey=True,
        figsize=(5,5),
        constrained_layout=True
    )
    put_bar_on_an_axis(data_files[0], axs[0][0], keypath=keypath, ylabel='Sampling Rate 1', title='System A', color=color, edgecolor=edgecolor, alpha=alpha )
    put_bar_on_an_axis(data_files[1], axs[0][1], keypath=keypath, ylabel=      None,        title='System B', color=color, edgecolor=edgecolor, alpha=alpha )
    put_bar_on_an_axis(data_files[2], axs[1][0], keypath=keypath, ylabel='Sampling Rate 2', title=   None   , color=color, edgecolor=edgecolor, alpha=alpha )
    put_bar_on_an_axis(data_files[3], axs[1][1], keypath=keypath, ylabel=      None,        title=   None   , color=color, edgecolor=edgecolor, alpha=alpha )
    put_bar_on_an_axis(data_files[4], axs[2][0], keypath=keypath, ylabel='Sampling Rate 4', title=   None   , color=color, edgecolor=edgecolor, alpha=alpha )
    put_bar_on_an_axis(data_files[5], axs[2][1], keypath=keypath, ylabel=      None,        title=   None   , color=color, edgecolor=edgecolor, alpha=alpha )
    return fig

def put_bar_on_an_axis(data_file, ax, keypath=[], ylabel=None, title=None, color='blue', edgecolor='black', alpha = 1):
    
    with open(data_file) as fp:
        data = json.load(fp)

    data = dictionary_subtree(data, keypath)

    java_avg = data['java']['avg'] 
    java_std = data['java']['stdev']

    c_ll_avg = data['c-linklist']['avg'] 
    c_ll_std = data['c-linklist']['stdev']

    c_da_avg = data['c-dynamicarray']['avg'] 
    c_da_std = data['c-dynamicarray']['stdev']

    ax.bar (
        x           =  [0       , 1       ,        2],
        height      =  [java_avg, c_ll_avg, c_da_avg],
        yerr        =  [java_std, c_ll_std, c_da_std],
        tick_label  =  ['J'     , 'CL'    , 'CD'    ],
        color = color,
        edgecolor = edgecolor,
        alpha = alpha
    )

    if ylabel: ax.set_ylabel(ylabel)
    if title: ax.set_title(title)

