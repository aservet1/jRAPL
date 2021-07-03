#!/bin/bash
set -e

sudo -v

mvn clean install
sudo java -jar target/benchmarks.jar -rf json
sudo mv jmh-result.json results/last-jmh-output_$(hostname).json
