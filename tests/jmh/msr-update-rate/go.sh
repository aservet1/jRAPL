#!/bin/bash
sudo java -jar target/benchmarks.jar -rf json && mv jmh-result.json results/last-jmh-output_$(hostname).json
