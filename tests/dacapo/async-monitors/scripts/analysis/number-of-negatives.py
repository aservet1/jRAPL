#!/usr/bin/env python3

import os
import json
from sys import argv

data_dir = argv[1]
os.chdir(data_dir)

count = {
	'negs': 0,
	'nonnegs': 0
}

for filename in sorted([
	filename for filename in os.listdir()
	if filename.endswith('.memory.json')
]):
	print('>>','processing',filename,'...')
	with open(filename) as fd:
		data = json.load(fd)
		count['negs'] += len(data['negatives_caught']['samples'])
		count['nonnegs'] += len(data['samples'])

print (
	count
);
print (
	count['negs'] / (count['nonnegs'] + count['negs']) * 100, '%'
);

