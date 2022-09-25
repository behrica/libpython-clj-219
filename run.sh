#!/usr/bin/env bash

for i in {1..10}; do echo $i " - " `date`;time clj train.clj &> /dev/null; done
