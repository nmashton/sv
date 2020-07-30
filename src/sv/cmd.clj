(ns sv.cmd
  (:require [clojure.tools.cli :refer [parse-opts]]))

(def opts
  [["-f" "--format FORMAT" "File format"
    :default "csv"
    :parse-fn keyword
    :validate [#(contains? ["csv" "psv" "ssv"] %)
               "Must be one of csv, psv, or ssv"]]])

(defn -main [& args]
  (println (parse-opts args opts)))