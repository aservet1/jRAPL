#!/usr/bin/python3
from sys import argv
with open(argv[1]) as f: data = [ line.strip() for line in f.readlines() ]

for line in data:
	line = line.split('|')
	bits = line[1]
	r = line[0].split(',')
	lo = int(r[0])
	hi = int(r[1])
