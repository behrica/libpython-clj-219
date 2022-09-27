#!/usr/bin/env bash
#export TOKENIZERS_PARALLELISM=false
export _JAVA_OPTIONS="-Dlibpython_clj.manual_gil=true"
rm -rf runs/
python -c 'from clojurebridge import cljbridge;cljbridge.load_clojure_file(clj_file="train.clj",mvn_local_repo="/home/carsten/.m2/repository")'
