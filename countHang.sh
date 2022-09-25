#!/usr/bin/env bash
rm -f signals.txt
for i in {1..10}; do timeout -s 9 60 clojure train.clj &> /dev/null ; echo $? >> signals.txt; done
