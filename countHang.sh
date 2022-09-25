#!/usr/bin/env bash
rm -f signals.txt
for i in {1..100}; do timeout -s 9 60 clojure train.clj &> /dev/null ; echo $? >> signals.txt; done
echo "Result: "
cat signals.txt | sort | uniq -c
