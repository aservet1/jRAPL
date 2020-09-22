
from sys import argv,stdin
from re import sub as regexsub

if len(argv) > 1:	infile = open(argv[1],'r')
else:			infile = stdin
data = infile.read()
infile.close()

data = [ d.strip() for d in data.split('\n')]

data = [ d.replace('native ','native_') for d in data ]
data = [ d.replace('static ', 'static_') for d in data ]
data = [ d.replace('final ', 'final_') for d in data ]
data = [ d.replace('abstract ', 'abstract_') for d in data]
data = [ regexsub('[;\{\}]','',d) for d in data ]

publics = list(filter(lambda l: l.startswith('public'), data))
publics = [ regexsub('\(.*\)','()',p) for p in publics ]
publics = [ p.split()[1:] for p in publics ]

package = [ d for d in data if d.startswith('package')][0].replace(';','')

print(f"{{ {package} }}")
for pub in publics:
	if len(pub) == 1: #most likely a constructor
		print(f"+{pub[0]}")
	elif 'class' in pub[0] or 'interface' in pub[0]:
		print(f"<<{' '.join(pub)}>>")
	else:
		print(f"+{pub[1]}: {pub[0]}")
