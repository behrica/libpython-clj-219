(ns train
  (:require
   [tablecloth.api :as tc]
   [tech.v3.libs.arrow :as arrow]
   [libpython-clj2.python.ffi :as ffi]))

(require
         '[libpython-clj2.python :refer [py.- py.] :as py])

(println "-------- manual-gil: " ffi/manual-gil)
;; (py/initialize!)

(def gil (ffi/lock-gil))

(def pd (py/import-module "pandas"))
(def st (py/import-module "simpletransformers.classification"))

(def data
  (->
   (arrow/stream->dataset "train.arrow" {:key-fn keyword})
   (tc/select-columns [:text :labels])))



(def split
  (tc/split->seq data))

(def pd-train
  (-> split
      first
      :train
      (tc/rows :as-seqs)
      ((py/py.- pd DataFrame))))

(def pd-eval
  (-> split
      first
      :test
      (tc/rows :as-seqs)
      ((py/py.- pd DataFrame))))
   


(def model ((py.- st ClassificationModel)
            ;; "bert" "prajjwal1/bert-tiny"
            "electra" "google/electra-base-discriminator"
            :use_cuda true
            :args {:use_multiprocessing false
                   :use_multiprocessing_for_evaluation false
                   :process_count 1
                   :evaluate_during_training true
                   :overwrite_output_dir true}))
                   

(println "start training")
(println :result  (py. model train_model pd-train :eval_df pd-eval))

(println "finished training")
(ffi/unlock-gil gil)
