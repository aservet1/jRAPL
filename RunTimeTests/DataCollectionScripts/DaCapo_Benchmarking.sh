if [ "$#" -ne $2 ]
then
	echo "usage: $0 [number of trials] [name to tag onto results folder]"
	exit 1
fi

benches="xalan tradebeans avrova fop h2 jython luindex lusearch lusearch-fix pmd sunflow"

for bench in $benches; do
    ./DataCollectionScripts/GetEnergyData.sh $2 $1 dacapo $bench
    killall java
    sleep 2
done