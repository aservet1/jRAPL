
'''https://www.planttext.com/'''

from glob import glob
from ParseJavaClass import parseJavaClass

'''dot.notation access to dictionary attributes, easier to write access'''
class DotDict(dict):
    __getattr__ = dict.get
    __setattr__ = dict.__setitem__
    __delattr__ = dict.__delitem__


def method_to_string:

def umlFormat(jclsInfo):
	publicMethods = '\n\t+'.join(jclsInfo.pub_methods)
	print(publicMethods)
	#privateMethods = jclsInfo.pri_methods


filenames = glob('../src/*.java')

javaInfos = [ parseJavaClass(filename) for filename in filenames ]

umls = [ umlFormat(jinf) for jinf in javaInfos ]
