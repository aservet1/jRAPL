#!/usr/bin/env python3

import pandas as pd
from sys import argv, stdin

# try:
# 	csvfile = argv[1]
# except IndexError:
# 	print("usage",argv[0],"filename.csv")
# 	exit(2)

csv_source = stdin if len(argv) == 1 else argv[1]

df = pd.read_csv(csv_source)

print(df.to_string(index=False))
