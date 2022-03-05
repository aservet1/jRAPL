 
function panic() {
    errorcode=$1
    shift
    echo $@
    exit $errorcode
}

cd ~/jRAPL

[ $? != 0 ] && panic $? "cd ~/jRAPL failed"

make clean all
mkdir -p running-just-from-compiled-files-on-disk
cp src/java/target/ running-just-from-compiled-files-on-disk/ -r # -v
