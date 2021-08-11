import os
import json
import matplotlib.pyplot as plt
from matplotlib.ticker import FormatStrFormatter

def parse_cmdline_args(argv):
    try:
        result_dir = argv[1]
        data_files = argv[2:]
    except:
        print (
            "usage:",
            argv[0],
            "<directory to output the plots>",
            "<json data file for System A>",
            "<json data file for System B>" # todo: add System C arg and integrate that into your scripts
        )
        exit(2)
    if not (result_dir.startswith("/") or result_dir.startswith("~")):
        result_dir = os.path.join(os.getcwd(),result_dir)
    if not os.path.isdir(result_dir):
        print("directory",result_dir,"does not exist")
        exit(2)

    return result_dir, data_files

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

def put_bar_on_an_axis(data_file, ax, title, color='blue', edgecolor='black', alpha = 1):
    
    with open(data_file) as fd:
        data = json.load(fd)['overall']
        #plotinfo = json.load(fd)['plotinfo']['overall']

    java_avg = data['java']['avg'] 
    java_std = data['java']['stdev']

    c_ll_avg = data['c-linklist']['avg'] 
    c_ll_std = data['c-linklist']['stdev']

    c_da_avg = data['c-dynamicarray']['avg'] 
    c_da_std = data['c-dynamicarray']['stdev']

    labels = ['java','c-linklist','c-dynamicarray']
    ax.bar (
        x           =  [0       , 1       ,        2],
        height      =  [java_avg, c_ll_avg, c_da_avg],
        yerr        =  [java_std, c_ll_std, c_da_std],
        tick_label  =  labels,
        #capsize     =  .5,
        color = color,
        edgecolor = edgecolor,
        alpha = alpha
    )

    ax.set_title(title)

