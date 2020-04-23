if [ "$#" -ne 2]; then
    echo USAGE $0 name iterations
    exit
fi
./DataCollectionScripts/TimeJavaOrCFunctions.sh $1 c $2

./DataCollectionScripts/TimeJavaOrCFunctions.sh $1 java $2



cd RuntimeResults$1


python3 ../DataCollectionScripts/bar_graphs.py CFunctions JavaFunctions