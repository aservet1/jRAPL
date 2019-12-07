'''-----------------------------------------------------------------------'''
def is_valid_code_string(code_string):
	'''Makes sure that there's equal numbers of { and }'''
	openbrace_count = 0
	closebrace_count = 0
	for character in code_string:
		if (character == '{'):
			openbrace_count += 1
		if (character == '}'):
			closebrace_count += 1
	return (openbrace_count == closebrace_count)
'''-----------------------------------------------------------------------'''
def cut_out_section(string, start, end):
	upperbound = end+1
	lowerbound = start
	return string[0:lowerbound]+string[upperbound:len(string)]

'''------------------------------------------------------------------------'''
def find_complementary_brace_index(start, string):
	i = start
	pseudostack = []
	found_complement = False
	while (not found_complement):
		if (string[i] == '{'):
			pseudostack.append(string[i])
		if (string[i] == '}'):
			pseudostack.pop()
			if(len(pseudostack) == 0):
				found_complement = True
		i += 1
	return i
'''------------------------------------------------------------------------'''
def generate_dest_filename(originalfile): #assuming originalfile has a .fileextension
	#@TODO -- write the body
	return None
'''------------------------------------------------------------------------'''

from sys import argv

src = open(argv[1],'r')
code_string = src.read()
code_string_length = len(code_string)
if (is_valid_code_string(code_string)):
	i=0
	while(i<len(code_string)):
		if (code_string[i]=='{'):
			start = i
			end = find_complementary_brace_index(start, code_string)
			code_string=cut_out_section(code_string, start, end)
		i+=1

	dest=open(argv[1]+".no-func-bod",'w')
	dest.write(code_string)
	dest.close()

src.close()
