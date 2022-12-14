(ns train
  (:require
   [tablecloth.api :as tc]
   [tech.v3.libs.arrow :as arrow]
   [libpython-clj2.python.ffi :as ffi]
   [libpython-clj2.python :refer [py.- py.] :as py]))





;; should be "true" for tis code to work, enabled by java property libpython_clj.manual_gil=true
(println "-------- manual-gil: " ffi/manual-gil)

;; (py/initialize!)  ;; not needed in embedded, called by clojurebridge

;; needed, because we use manual GIL management, enabled by java property libpython_clj.manual_gil=true
;; without it, we get JVM crash
(def gil (ffi/lock-gil))

(require
  '[libpython-clj2.require :as py-req])

(py-req/require-python '[pandas :as pd])
(py-req/require-python '[simpletransformers.classification :as st])

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
      (pd/DataFrame)))

(def pd-eval
  (-> split
      first
      :test
      (tc/rows :as-seqs)
      (pd/DataFrame)))
   


(def model (st/ClassificationModel
            "electra" "google/electra-base-discriminator"
            :use_cuda true
            :args {
                   :num_train_epochs 3
                   :evaluate_during_training_silent false
                   :evaluate_during_training true
                   :evaluate_during_training_verbose true
                   :overwrite_output_dir true}))
                   

(println "start training")
(println :result  (py. model train_model pd-train :eval_df pd-eval))

(println "finished training")
;; needed, because we use manual GIL management, enabled by java property libpython_clj.manual_gil=true
;; without it, it will hang forever
(ffi/unlock-gil gil)
