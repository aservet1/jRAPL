#!/bin/bash
(cd src/main/resources/native && make) && mvn clean install
