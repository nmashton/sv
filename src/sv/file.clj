(ns sv.file
  (:require [clojure.java.io :as io]))

(def separators
  {::csv ","
   ::psv "\\|"
   ::ssv " "})

(defn filename->fmt
  "Given a string representing a filename, attempts to
   guess the file format by looking at its extension."
  [filename]
  (cond
    (re-matches #"\S+\.csv$" filename) ::csv
    (re-matches #"\S+\.psv$" filename) ::psv
    (re-matches #"\S+\.ssv$" filename) ::ssv
    :else nil))

(defn filename->separator
  "Given a string representing a filename, attempts to
   guess its separator value."
  [filename]
  (get separators (filename->fmt filename)))

(defn guess-format
  "Given a raw line of data, attempts to guess its
   data format by matching it against a pattern
   for that type of data."
  [raw-line]
  (cond
    (re-matches #"^([^,]+,){4}[^,]+$" raw-line) ::csv
    (re-matches #"^([^\|]+\|){4}[^\|]+$" raw-line) ::psv
    (re-matches #"^(\S+ ){4}\S+$" raw-line) ::ssv
    :else nil))

(defn guess-separator
  "Given a raw line of data, attempts to guess its
   appropriate separator."
  [raw-line]
  (get separators (guess-format raw-line)))

(defn filenames-with-errors
  "Given a list of filenames, returns any that had problems,
   either because their format couldn't be guessed from their
   name or because they did not actually exist."
  [filenames]
  (filter
   #(not (and (filename->fmt %)
              (.exists (io/file %))))
   filenames))
