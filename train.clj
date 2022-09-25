(ns train
  (:require
   [tablecloth.api :as tc]
   [tech.v3.libs.arrow :as arrow]))

(require
         '[libpython-clj2.python :refer [py.- py.] :as py])

(py/initialize!)

(def pd (py/import-module "pandas"))
(def st (py/import-module "simpletransformers.classification"))

(def pd-train
  ((py/py.- pd DataFrame)
   (->
    (arrow/stream->dataset "train.arrow" {:key-fn keyword})
    (tc/select-columns [:text :labels])
    ;; (tc/head 102)
    (tc/rows :as-seqs))))

(def model ((py.- st ClassificationModel)
            "bert" "prajjwal1/bert-tiny"


            :use_cuda true
            :args {:silent true
                   :overwrite_output_dir true}))

(println "start training")
(println :result  (py. model train_model pd-train))

(println "finished training")
;; just in case
(shutdown-agents)
