#!/usr/bin/env bash

for i in {1..10}; do time clj train.clj &> /dev/null; done
