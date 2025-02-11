(ns sv.parse
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [sv.file :as file]
            [sv.model :refer []]
            [sv.util :refer [date-string->local-date]]))

(s/def ::line (s/cat :sv.model/last-name :sv.model/last-name
                     :sv.model/first-name :sv.model/first-name
                     :sv.model/gender :sv.model/gender
                     :sv.model/favorite-color :sv.model/favorite-color
                     ::date-of-birth ::date-of-birth))
(s/def ::line-number integer?)

(s/def ::date-of-birth date-string->local-date)
(s/def ::validated-data
  (s/keys :req [:sv.model/last-name
                :sv.model/first-name
                :sv.model/gender
                :sv.model/favorite-color
                ::date-of-birth]))
(s/def ::parsed
  (s/keys :req [::line-number
                :sv.model/record]))
(s/def ::error
  (s/keys :req [::line-number
                ::errors]))
(s/def ::result (s/cat ::data (s/nilable (s/coll-of ::parsed))
                       ::errors (s/nilable (s/coll-of ::error))))
(s/fdef parse-result->record
  :args (s/cat :result ::validated-data)
  :ret :sv.model/record)
(s/fdef parse-lines
  :args (s/cat :lines (s/coll-of string?)
               :separator string?)
  :ret ::result
  :fn (fn [{:keys [args ret]}]
        (= (count (:lines args))
           (+ (count (::data ret))
              (count (::errors ret))))))

(defn parse-result->record
  "Applies whatever transformations are necessary to
   process a valid parse result into a record of
   the expected form."
  [result]
  (as-> result result
    (assoc result
           :sv.model/date-of-birth
           (date-string->local-date (::date-of-birth result)))
    (dissoc result ::date-of-birth)))

(defn validate-and-parse
  "Returns a map that tags the results of applying
   a spec to an input value with either :errors
   or :data, depending on whether it passes."
  [spec value]
  (let [parsed (s/conform spec value)]
    (if (= parsed ::s/invalid)
      {::errors (s/explain-data spec value)}
      {::data (parse-result->record parsed)})))

(defn combine-parse-results
  "Given a sequence of results-errors pairs of the type returned
   by parse/parse-file, combine them into a single big pair."
  [results-and-errors]
  (reduce
   (fn [[rs es] [next-rs next-es]]
     [(concat rs next-rs)
      (concat es next-es)])
   [nil nil]
   results-and-errors))

(defn parse-lines
  "Splits, parses, and groups the lines from an
   input sequence into the expected collections of
   records."
  [lines separator]
  (reduce
   (fn [[rs es] {errors ::errors :as data}]
     (if errors
       [rs (conj es data)]
       [(conj rs data) es]))
   [nil nil]
   (eduction
    (map #(string/split % (re-pattern separator)))
    (map #(validate-and-parse ::line %))
    (map-indexed (fn [i val] (assoc val ::line-number i)))
    lines)))

(defn parse-raw-line
  "Apply the same parsing logic as is found in parse-lines
   to a raw line of text."
  ([raw-line separator]
   (as-> raw-line line
     (string/split line (re-pattern separator))
     (validate-and-parse ::line line)))
  ([raw-line]
   (if-let [separator (file/guess-separator raw-line)]
     (parse-raw-line raw-line separator)
     nil)))

(defn prep-for-cmd
  "Given data with the shape of the output of parse-lines
   and a filename with which to tag results, returns
   a pair containing:
   
   1. the sequence of records extracted from the file
   2. a pair containing:
   2a. the filename
   2b. the sorted sequence of lines with errors"
  [[results errors] filename]
  [(map ::data results)
   [filename (->> errors
                  (map ::line-number)
                  set
                  sort)]])

(defn parse-file
  [filename separator]
  (with-open [r (io/reader filename)]
    (prep-for-cmd (parse-lines (line-seq r) separator)
                  filename)))

(defn parse-filename
  [filename]
  (parse-file filename (file/filename->separator filename)))