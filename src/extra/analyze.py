from sys import argv

if (len(argv)==1): print("enter a file"); exit()

file = open(argv[1])
data = file.read()
file.close()

data = data.split('~\n')

for d in data:
    if ("readings: 0" in d and "1\tdram" in d): print(d,"\n\n\n------------=============+++============-----------\n\n\n")
