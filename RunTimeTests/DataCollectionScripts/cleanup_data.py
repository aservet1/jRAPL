#Script to remove the name of the function from each line in the C-output files

import os

files = os.listdir()


for file in files:
    parts = file.split('.')
    if(len(parts) != 2 or file.split('.')[1] != 'data' or parts[0].endswith("_cleaned")):
        continue
    newfile = parts[0] + "_cleaned" + ".data"
    newfh = open(newfile, 'w')
    fh = open(file, 'r')
    for line in fh:
        data = line.split()[1]
        if data.isdigit():
            newfh.write('{}\n'.format(data))
    os.remove(file)
    os.rename(newfile, file)