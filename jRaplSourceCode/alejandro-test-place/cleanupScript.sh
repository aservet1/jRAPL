#simple script to run the cleanup py script and rename all the files to their original names

python3 ~/jRAPL/jRaplSourceCode/alejandro-test-place/cleanup_data.py 

for f in *.data; do mv "$f" "$(echo "$f" | sed s/_cleaned//)"; done