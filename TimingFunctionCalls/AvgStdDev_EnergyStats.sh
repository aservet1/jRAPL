sudo modprobe msr

if [ "$#" -lt 1 ] || [ "$#" -gt 2 ]; then
	echo "Usage: $0 name [mem | cpu | both]" >&2
	exit 1
fi

dirName=""

if [ "$2" = "mem" ]; then
	dirName=$1"EnergyData_stressed_"$2
	stress-ng --vm 4 --vm-bytes 50% --all 0 --timeout 30s >/dev/null 1>&2 &
fi

if [ "$2" = "cpu" ]; then
	dirName=$1"EnergyData_stressed_"$2
	stress-ng --cpu 8 --all	0 --timeout 30s >/dev/null 1>&2 &
fi

if [ "$2" = "both" ]; then
	dirName=$1"EnergyData_stressed_"$2
	stress-ng --vm 4 --cpu 8 --vm-bytes 50% --all 0 --timeout 60s >/dev/null 1>&2 &
fi

if [ "$dirName" = "" ]; then
	dirName=$1"EnergyData_idleCPU"
fi

mkdir $dirName
echo $dirName

cd ../jRaplSourceCode/

sudo java jrapl.EnergyCheckUtils >../TimingFunctionCalls/$dirName/Output

cd ../TimingFunctionCalls/$dirName/

readingAreas="DRAM CORE PACKAGE"

for f in $readingAreas
do
	echo $f.data
	file=$f.data
	grep $f Output > $f.data
done

../../jRaplSourceCode/alejandro-test-place/cleanupScript.sh

python3 ../EnergyStatsStats.py
