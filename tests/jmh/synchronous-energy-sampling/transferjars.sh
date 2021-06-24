#!/bin/bash

## quick hack script to copy all files from $src to $dest jar files
##   This is a hack because I don't know how to deal with maven jar dependency stuff! Ie the jRAPL files allowing for compliation but not
##   ending up in the httpRAPL-server.jar file, and therefore not being available at runtime
##   Consider this as a source of bugs and replace it with something more proper when you get a chance

function usage() {
  echo "usage: $1 <src>.jar <dest>.jar"
  echo "  must be relative paths. no ~ or anything that'll end up starting from root / directory"
  exit 2
}
[ -z $2 ] && usage $0

echo "========================================"
echo "  Copying all files from $src to $dest  "
echo "========================================"


src=$1; dest=$2

TMP=tmp-$RANDOM
while [ -d $TMP ]; do
  TMP=tmp-$RANDOM
done

mkdir $TMP ; cd $TMP

jar -xvf ../$src
rm -rf META-INF
jar -uvf ../$dest $(ls)
cd ..
rm -r $TMP
