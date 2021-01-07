#!/bin/bash

if [ ! -f hello ]; then
	echo 'hello does not exist'
	exit 1
else
	echo 'hello exists'
fi

echo 'doing whatever needs to happen now...'
