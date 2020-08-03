(ns sv.file
  (:require [clojure.java.io :as io]
            [sv.parse :as parse]))

(def separators
  {::csv ","
   ::psv "\\n"
   ::ssv " "})

(defn filename->fmt
  [filename]
  (cond
    (re-matches #"\S+\.csv" filename) ::csv
    (re-matches #"\S+\.psv" filename) ::psv
    (re-matches #"\S+\.ssv" filename) ::ssv
    :else nil))

(defn guess-format
  [raw-line]
  (cond
    (re-matches #"^(\S+,){4}\S+$" raw-line) ::csv
    (re-matches #"^(\S+\|){4}\S+$" raw-line) ::psv
    (re-matches #"^(\S+ ){4}\S+$" raw-line) ::ssv
    :else nil))

(defn filenames-with-errors
  "Given a list of filenames, returns any that had problems,
   either because their format couldn't be guessed from their
   name or because they did not actually exist."
  [filenames]
  (filter
   #(not (and (filename->fmt %)
              (.exists (io/file %))))
   filenames))

(defn parse-filename
  [filename]
  (parse/parse-file filename (get separators (filename->fmt filename))))