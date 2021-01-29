import json
from sys import argv

with open(argv[1]) as f: data = json.loads(f.read())

#for d in data:
#	print(d['primaryMetric'])

result = d[0]

