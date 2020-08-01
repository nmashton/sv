(ns sv.parse
  (:require [sv.model :refer []])
  (:require [clojure.spec.alpha :as s])
  (:require [clojure.string :as string])
  (:require [clojure.java.io :as io])
  (:require [sv.util :refer [date-string->local-date]]))

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

(defn combine-results
  "Reducer function to combine all results
   of parsing into two vectors (valid results
   and errors)."
  [[rs es] {errors ::errors :as data}]
  (if errors
    [rs (conj es data)]
    [(conj rs data) es]))

(defn parse-lines
  "Splits, parses, and groups the lines from an
   input sequence into the expected collections of
   records."
  [lines separator]
  (reduce
   combine-results
   [nil nil]
   (eduction
    (map #(string/split % (re-pattern separator)))
    (map #(validate-and-parse ::line %))
    (map-indexed (fn [i val] (assoc val ::line-number i)))
    lines)))

(defn parse-file
  [filename separator]
  (with-open [r (io/reader filename)]
    (parse-lines (line-seq r) separator)))
