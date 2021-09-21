import pandas as pd
import os
import matplotlib.pyplot as plt

def unbias(l):
    return [ l[i] - l[0] for i in range(len(l)) ]
def diff_list(l):
    return [l[i] - l[i-1] for i in range(1,len(l))]

def recursive_listdir(path='.'):
    returndir = os.getcwd()
    os.chdir(path)
    parent = os.getcwd()
    files = []
    for f in sorted(os.listdir()):
        if os.path.isdir(f):
            files.extend(recursive_listdir(f))
        else:
            if parent: files.append(os.path.join(parent,f))
            else: files.append(f)
    os.chdir(returndir)
    return files

source_root = 'pulled-files'
dest_root = 'plots'
for filename in recursive_listdir('pulled-files'):
    if not filename.endswith('.csv'): continue

    df = pd.read_csv(filename)

    x_axis = unbias(df['timestamp'].to_list())
    del df['timestamp']
    plt.clf()
    for power_domain in list(df.columns.values):
        y_axis = unbias(df[power_domain].to_list())
        plt.plot(x_axis, y_axis, label=power_domain)
    plt.legend()
    plotfilename = filename.replace(os.getcwd(),'.').replace(source_root,dest_root).replace('.csv','')
    plt.title(plotfilename)
    plt.savefig(plotfilename)
    print(' (-) done with',plotfilename)
