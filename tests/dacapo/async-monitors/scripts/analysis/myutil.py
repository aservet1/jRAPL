import os
import json

def parse_cmdline_args(argv):
    try:
        data_dir = argv[1]
        result_dir = argv[2]
    except:
        print("usage:",argv[0],"<directory with all the .aggregate{perbench,permonitor}-stats.json files>","<directory to output the result>")
        exit(2)
    if not (result_dir.startswith("/") or result_dir.startswith("~")):
        result_dir = os.path.join(os.getcwd(),result_dir) # make an absolute path
    if not os.path.isdir(result_dir):
        print("directory",result_dir,"does not exist")
        exit(2)
    return data_dir, result_dir


def load_data_by_file_extension(ext, benchmark_or_monitorType):
    files = sorted([ f for f in os.listdir() if f.endswith(ext) ])
    if not len(files):
        print(" .)-) no files found with ext", ext)
        exit(2)
    data = []
    for fname in files:
        with open(fname) as f:
            data.append(json.loads(f.read()))
    tmp = {}
    for d in data:
        bench = d['metadata'][benchmark_or_monitorType]
        if not bench in tmp.keys(): tmp[bench] = [d]
        else: tmp[bench].append(d)
    data = tmp
    return data
