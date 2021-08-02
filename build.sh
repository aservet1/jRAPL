#!/bin/bash

( [ -z $1 ]  &&   make clean all ) || \
( [ $1 = 'clean' ] && make clean ) || \
(    echo " invalid arg: $1 "    )

