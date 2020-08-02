(ns sv.file
  (:require [sv.model :as model])
  (:require [sv.parse :as parse])
  (:require [clojure.java.io :as io]))

(def separators
  {:csv ","
   :psv "[|]"
   :ssv " "})

(def sorters
  {:gender model/sort-by-gender-and-last-name
   :date-asc model/sort-by-date-asc
   :date-desc model/sort-by-date-desc
   :default model/sort-by-gender-and-last-name})

(defn filename->fmt
  [filename]
  (cond
    (re-matches #"\S+\.csv" filename) :csv
    (re-matches #"\S+\.psv" filename) :psv
    (re-matches #"\S+\.ssv" filename) :ssv
    :else nil))

(defn guess-format
  [raw-line]
  (cond
    (re-matches #"^(\S+,){4}\S+$" raw-line) :csv
    (re-matches #"^(\S+\|){4}\S+$" raw-line) :psv
    (re-matches #"^(\S+ ){4}\S+$" raw-line) :ssv
    :else nil))

(defn filenames->errors
  [filenames]
  (filter
   #(not (and (filename->fmt %)
              (.exists (io/file %))))
   filenames))

(defn parse-filename
  [filename]
  (parse/parse-file filename (get separators (filename->fmt filename))))