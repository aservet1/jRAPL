import os

def parse_cmdline_args(argv):
    try:
        data_file = argv[1]
        result_dir = argv[2]
    except:
        print("usage:",argv[0],"<json data file>","<directory to output the plots>")
        exit(2)
    if not (result_dir.startswith("/") or result_dir.startswith("~")):
        result_dir = os.path.join(os.getcwd(),result_dir)
    if not os.path.isdir(result_dir):
        print("directory",result_dir,"does not exist")
        exit(2)

    return data_file, result_dir

