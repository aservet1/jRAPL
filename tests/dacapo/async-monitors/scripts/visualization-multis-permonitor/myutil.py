import os
import json
import matplotlib.pyplot as plt
from matplotlib.ticker import FormatStrFormatter

def parse_cmdline_args(argv):
    try:
        result_dir = argv[1]
        data_files = argv[2:8]
        keypath = argv[8:]
    except:
        print (
            "usage:",
            argv[0],
            "output-dir",
            "data files: sysAsr1, sysBsr1, sysAsr2, sysBsr2, sysAsr4, sysBsr4",
            "keypath..."
        )
        print("\nHow each file will translate into each subplot:\n\
   SystemA SystemB   \n\
   ________________ \n\
1 |argv[2] | argv[3]|\n\
2 |argv[4] | argv[5]|\n\
4 |argv[6] | argv[7]|\n\
   ________________ \
        ")
        exit(2)
    if not (result_dir.startswith("/") or result_dir.startswith("~")):
        result_dir = os.path.join(os.getcwd(),result_dir)
    if not os.path.isdir(result_dir):
        print("directory",result_dir,"does not exist")
        exit(2)

    return result_dir, data_files, keypath

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

"""
example:

{
    "normalized": {
        "foo": {
            "baz":  { "avg": 100, "stdev": 150 },
            "bonk": { "avg": 100, "stdev": 150 }
        },
        "bar": {
            "baz": { "avg": 10, "stdev": 10 },
            "bonk": {"avg": 1,  "stdev": 0  }
        }
    },
    "raw": {
        "foo": {
            "baz": { "avg": 100, "stdev": 150 },
            "bonk": {"avg": 0, "stdev": 50 }
        },
        "bar": {
            "baz": { "avg": -10,  "stdev": 101  },
            "bonk": {"avg": 1,   "stdev": 0   }
        }
    }
}
with a keypath of ['raw','foo'], data would be reduced to
{
    "baz":  {"avg": 100, "stdev": 150 },
    "bonk": {"avg": 0, "stdev": 50 }
}

ie you go down the key path and give the data tree rooted at there
"""
def adjust_with_keypath(data, keypath):
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
    put_bar_on_an_axis(data_files[0], axs[0][0], keypath=keypath, title='System A', color=color, edgecolor=edgecolor, alpha=alpha )
    put_bar_on_an_axis(data_files[1], axs[0][1], keypath=keypath, title='System B', color=color, edgecolor=edgecolor, alpha=alpha )
    put_bar_on_an_axis(data_files[2], axs[1][0], keypath=keypath, title=   None   , color=color, edgecolor=edgecolor, alpha=alpha )
    put_bar_on_an_axis(data_files[3], axs[1][1], keypath=keypath, title=   None   , color=color, edgecolor=edgecolor, alpha=alpha )
    put_bar_on_an_axis(data_files[4], axs[2][0], keypath=keypath, title=   None   , color=color, edgecolor=edgecolor, alpha=alpha )
    put_bar_on_an_axis(data_files[5], axs[2][1], keypath=keypath, title=   None   , color=color, edgecolor=edgecolor, alpha=alpha )
    return fig

def put_bar_on_an_axis(data_file, ax, keypath=[], title=None, color='blue', edgecolor='black', alpha = 1):
    
    with open(data_file) as fd:
        data = json.load(fd)
        #plotinfo = json.load(fd)['plotinfo']['overall']

    data = adjust_with_keypath(data, keypath)

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

    if title: ax.set_title(title)

