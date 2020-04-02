if [ "$#" -ne 2 ]; then
  echo "Usage: $0 NAME" >&2
  exit 1
fi

sudo modprobe msr

dirName=$1"EnergyData"

mkdir $dirName

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
