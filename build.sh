#!/bin/bash
([ -z $1 ] && make) || ([ $1 = 'clean' ] && make clean)
