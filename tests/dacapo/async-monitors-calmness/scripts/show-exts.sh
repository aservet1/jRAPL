#!/bin/bash

ls $1 | sed 's/[^\.]*//' | sort | uniq
