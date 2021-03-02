
function usage() {
	echo "usage: $1 <benchmark> <iterations> <monitorType>"
	echo "monitorType = java|c-linklist|c-dynamicarray"
	exit 1
}

mycallback=AsyncMonitorCallback
dacapo_jar="dacapo-evaluation-git+309e1fa.jar"
jRAPL_jar="jRAPL-1.0.jar"
classpath="$dacapo_jar:$jRAPL_jar:."

[ $# != 3 ] && usage $0

benchmark=$1
iterations=$2
java_or_c=$3

sudo java -DmonitorType=java -cp $classpath Harness $benchmark -c $mycallback -n $iterations
