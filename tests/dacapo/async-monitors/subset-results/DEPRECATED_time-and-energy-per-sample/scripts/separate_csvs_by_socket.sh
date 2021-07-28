#!/bin/bash

if [ -z $2 ] || [ -z $1 ]
then
  echo usage: $0 srcdir outdir
  exit 2
fi

srcdir=$1
outdir=$2

if [ -d $outdir ]
then
  echo $outdir' already exists.'
  exit 3
fi

mkdir $outdir

for f in $(cd $srcdir && ls *.csv)
do
  echo '<<' $f
  head -1 $srcdir/$f > $outdir/Socket1_$f
  head -1 $srcdir/$f > $outdir/Socket2_$f

  grep -E '^1' $srcdir/$f >> $outdir/Socket1_$f
  grep -E '^2' $srcdir/$f >> $outdir/Socket2_$f
  echo '>>' $f
done
