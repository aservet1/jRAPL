import re

'''dot.notation access to dictionary attributes, easier to write access'''
class DotDict(dict):
    __getattr__ = dict.get
    __setattr__ = dict.__setitem__
    __delattr__ = dict.__delitem__


def removeComments(datastring):
    datastring = re.sub(re.compile("/\*.*?\*/",re.DOTALL ) , "", datastring)
    datastring = re.sub(re.compile("//.*?\n" ), "\n", datastring)
    return datastring


#strip leading and trailing whitespace of each line, and remove empty lines
def removeWhitespace(datastring):
    splitDelim = '\n'
    _datastring = [ line.strip() for line in datastring.split(splitDelim) ]
    _datastring = filter(lambda line: len(line) > 0, _datastring)
    datastring = splitDelim.join(_datastring)
    return datastring


#assumed that the string is already {}-balanced, it's your problem if you pass in a bad one
def matching_brace_index(string, start):
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

def remove_section(string, lowerbound, upperbound):
        return string[:lowerbound]+string[upperbound:]

def removeMethodBodies(data):
    firstbrace = data.index('{')
    lastbrace = matching_brace_index(data, firstbrace)
    inner = data[firstbrace+1:lastbrace-1]
    i = 0
    while i < len(inner):
        if inner[i]=='{':
            start = i; end = matching_brace_index(inner, i)
            inner = remove_section(inner, start, end)
        i += 1
    
    data = data[:firstbrace] + '{' + inner + '}' + data[lastbrace:]
    
    return data
    

def safePop(items, index): #don't break if you're removing indices that can't be accessed
    try:
        item = items.pop(index)
    except:
        item = None
    return item

def getClass(data):
    cls = DotDict()
    lines = data.split('\n')
    statement = [ l for l in lines if 'class' in l ]
    if len(statement) > 0:
        statement = statement[0]
        cls.name = statement.split('class ')[1].split()[0]
        if 'extends' in statement:
            cls.extends = statement.split(' extends ')[1].split()[0]
        if 'implements' in statement:
            cls.implements = statement.split(' implements ')[1].split()[0].split(',')
    return cls

def getPackage(data):
    lines = data.split('\n')
    return [ line for line in lines if line.startswith('package') ][0].split()[1].replace(';','')

def parseDeclaration(d): # same format for method and variable declarations (splitted by ' ')
    info = DotDict({})
    if d[0] in ['public','private','protected']:
        info.access = safePop(d,0)
    else: info.access = None
    info.name = safePop(d,-1)
    info.return_type = safePop(d,-1)
    info.other_info = ' '.join(d)
    return info

def getVariableList(data):
    firstbrace = data.index('{')
    lastbrace = matching_brace_index(data, firstbrace)
    inside_class = data[firstbrace+1:lastbrace-1].split('\n')
    variables = [ line for line in inside_class if not '(' in line and not ')' in line ]
    variables = filter(lambda x: len(x) > 0, variables)
    variables = [ re.sub('=.*$','',v) for v in variables ]
    variables = [ v.replace(';','').strip() for v in variables ]
    variables = [ v.split() for v in variables ]
    
    parsed_variables = [ parseDeclaration(v) for v in variables if len(v) > 1 ]
    
    return parsed_variables
    
def getMethodList(data):
    firstbrace = data.index('{')
    lastbrace = matching_brace_index(data, firstbrace)
    inside_class = data[firstbrace+1:lastbrace-1].split('\n')
    methods = [line for line in inside_class if '(' in line and ')' in line]
    methods = [re.sub('\(.*\)','()',method) for method in methods] #THIS IS JUST A HACK FOR NOW, DONT ACTUALLY DELETE
    methods = [ method.replace(';','') for method in methods ]
    methods = [ method.split() for method in methods ]
    
    parsed_methods = [ parseDeclaration(m) for m in methods if len(m) > 1 ] 
    
    return parsed_methods

def getPublicVariables(data):
    return [ v for v in getVariableList(data) if v.access == 'public' ]

def getPrivateVariables(data):
    return [ v for v in getVariableList(data) if v.access == 'private' ]

def getPublicMethods(data):
    return [ m for m in getMethodList(data) if m.access == 'public' ]

def getPrivateMethods(data):
    return [ m for m in getMethodList(data) if m.access == 'private' ]



''' This is the only one I'm actually exporting '''
""" Assumes it's one class per file, nothing fancier than that """
def parseJavaClass(filename):
	infile = open(filename,'r')
	data = infile.read()
	infile.close()

	data = removeComments(data)
	data = removeMethodBodies(data)
	data = removeWhitespace(data)

	jclsInfo = DotDict()
	jclsInfo.cls = getClass(data)
	jclsInfo.package = getPackage(data)
	jclsInfo.pub_vars = getPublicVariables(data)
	jclsInfo.pri_vars = getPrivateVariables(data)
	jclsInfo.pub_methods = getPublicMethods(data)
	jclsInfo.pri_methods = getPrivateMethods(data)

	return jclsInfo

'''Also exporting this one :)'''
def simpleCleanup(filename):
	infile = open(filename,'r')
	data = infile.read()
	infile.close()

	data = removeComments(data)
	data = removeMethodBodies(data)
	data = removeWhitespace(data)

	return data
