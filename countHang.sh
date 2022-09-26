#!/usr/bin/env bash
rm -f signals.txt
for i in {1..100}; do timeout -s 9 1200 ./train.sh &> /tmp/run.log ; echo $? >> signals.txt; done
echo "Result: "
cat signals.txt | sort | uniq -c
