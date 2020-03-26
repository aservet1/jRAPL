#Script to remove the name of the function from each line in the C-output files

from os import listdir

files = listdir()
print(files)

for file in files:
    if(file.split('.')[1] != 'data'):
        continue
    newfile = file.split('.')[0] + "_cleaned" + ".data"
    newfh = open(newfile, 'w')
    fh = open(file, 'r')
    for line in fh:
        data = int(line.split()[1])
        newfh.write('{}\n'.format(data))    