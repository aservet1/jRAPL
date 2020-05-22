from sys import argv
from random import randint
from random import random as randfloat
'''--------------------------------------'''
def alphabet_string():
    s = str()
    for i in range(ord('a'),ord('z')+1):
        s += chr(i)
    return s
'''--------------------------------------'''

'''--------------------------------------'''
def remove_at(i, string):
    return string[:i] + string [i+1:]

#cut out random letters
def remove_random_characters(string, percent_length_to_use):
    assert(percent_length_to_use < 1 and percent_length_to_use >= 0)
    max_num_to_remove = int(len(string) * percent_length_to_use)

    for x in range(randint(0,max_num_to_remove)):
        i = randint(0,len(string))
        string = remove_at(i, string)
    
    return string

'''--------------------------------------'''
def remove_block_by_index(s, head, tail): # guarantee circular wraparound of index removal
    if (head == tail): return s
    if (head < tail):  return s[0:head] + s[tail+1:len(s)]
    if (head > tail):  return s[tail+1:head]

#cut out random blocks (random start and end, max limit on upper bound of len(input) * percentOfLengthToUse. keep at 0.25 when you call this for now
def remove_single_random_block(string, percent_length_to_use):
    assert(percent_length_to_use < 1 and percent_length_to_use >= 0)
    max_block_length = int(len(string) * percent_length_to_use)
    
    head = randint(0,len(string)-1)
    tail = ( head + randint(0,max_block_length) ) % len(string)

    return remove_block_by_index(string,head,tail)

'''--------------------------------------'''

if (len(argv[1]) <= 1): print("usage: "+argv[0]+"filename-to-garble"); exit(1)

filename = argv[1]
filehandle = open(filename,"r")
data_lines = filehandle.readlines()
filehandle.close()

probability_a = 0.45            ;       probability_b = 0.45
benchmark_a = probability_a     ;       benchmark_b = benchmark_a + probability_b

for line in data_lines:
    
    choice = randfloat()
    
    if choice < benchmark_a:
        print(remove_random_characters(line, .49),end='') 
    
    if choice < benchmark_b:
        print(remove_single_random_block(line, 0.30),end='')
    
    else:
        print(line)



filehandle.close()
