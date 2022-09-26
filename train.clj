(ns train
  (:require
   [tablecloth.api :as tc]
   [tech.v3.libs.arrow :as arrow]
   [libpython-clj2.python.ffi :as ffi]))

(require
         '[libpython-clj2.python :refer [py.- py.] :as py])

(println "-------- manual-gil: " ffi/manual-gil)


(def gil (ffi/lock-gil))

(def pd (py/import-module "pandas"))
(def st (py/import-module "simpletransformers.classification"))

(def pd-train
  ((py/py.- pd DataFrame)
   (->
    (arrow/stream->dataset "train.arrow" {:key-fn keyword})
    (tc/select-columns [:text :labels])
    (tc/head 1)
    (tc/rows :as-seqs))))


(def model ((py.- st ClassificationModel)
            "bert" "prajjwal1/bert-tiny"
            ;; "electra" "google/electra-base-discriminator"
            :use_cuda true
            :args {:use_multiprocessing false
                   :use_multiprocessing_for_evaluation false
                   :process_count 1
                   :overwrite_output_dir true}))
                   

(println "start training")
(println :result  (py. model train_model pd-train))

(println "finished training")
(ffi/unlock-gil gil)
