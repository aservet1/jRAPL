sudo modprobe msr

if [ "$#" -lt 1 ] || [ "$#" -gt 4 ]; then
	echo "Usage: $0 name [mem | cpu | both | dacapo_benchmark_name [number of iterations]]" >&2
	exit 1
fi

dirName=""

if [ "$2" = "mem" ]; then
	dirName=$1"EnergyData_stressed_"$2
	stress-ng --vm 2 --vm-bytes 50% --all 0 --timeout 60s >/dev/null 1>&2 &
fi

if [ "$2" = "cpu" ]; then
	dirName=$1"EnergyData_stressed_"$2
	stress-ng --cpu 2 --all	0 --timeout 60s >/dev/null 1>&2 &
fi

if [ "$2" = "both" ]; then
	dirName=$1"EnergyData_stressed_"$2
	stress-ng --vm 2 --cpu 2 --vm-bytes 50% --all 0 --timeout 180s >/dev/null 1>&2 &
fi

if [ "$2" = "dacapo" ]; then
	dirName=$1"EnergyData_DaCapo_"$3
	jrapl_dir=$PWD
	if [ ! -d $HOME"/dacapo/" ]; then
		echo "Directory ~/dacapo/ DOES NOT exist." && exit 1
	fi
	cd $HOME/dacapo/
	java Harness $3 -n $4 >/dev/null 1>&2 &
	sleep 40
	cd $jrapl_dir
fi

if [ "$dirName" = "" ]; then
	dirName=$1"EnergyData_idleCPU"
fi

mkdir -p  RuntimeResults/$dirName
echo $dirName

cd ../src/

sudo java jrapl.EnergyCheckUtils >../RunTimeTests/$dirName/Output

cd ../TimingFunctionCalls/$dirName/

readingAreas="DRAM CORE PACKAGE"

for f in $readingAreas
do
	echo $f.data
	file=$f.data
	grep $f Output > $f.data
done



python3

python3 ../CalcEnergyStats.py

#rm -f ../CORE.data ../PACKAGE.data ../DRAM.data



