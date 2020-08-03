(ns sv.model
  (:require [cheshire.core :refer [generate-string]]
            [clojure.spec.alpha :as s]
            [java-time :as jt]))

(s/def ::last-name string?)
(s/def ::first-name string?)
(s/def ::gender #{"female"
                  "male"
                  "nonbinary"
                  "other"})
(s/def ::date-of-birth #(instance? java.time.LocalDate %))
(s/def ::favorite-color string?)

(s/def ::record
  (s/keys :req [::last-name
                ::first-name
                ::gender
                ::favorite-color
                ::date-of-birth]))

(s/def :sv.display/date-of-birth
  (s/and string?
         #(re-matches #"\d+/\d+/\d+" %)))
(s/def ::display
  (s/keys :req-un [::last-name
                   ::first-name
                   ::gender
                   ::favorite-color
                   :sv.display/date-of-birth]))

(s/fdef record->display
  :args (s/cat :record (s/coll-of ::record))
  :ret (s/coll-of ::display)
  :fn (fn [{:keys [args ret]}]
        (= (count (first args))
           (count ret))))

(defn record->display
  [{::keys [last-name first-name gender favorite-color date-of-birth]}]
  {:last-name last-name
   :first-name first-name
   :gender gender
   :favorite-color favorite-color
   :date-of-birth (jt/format "MM/dd/yyyy" date-of-birth)})

(defn sort-by-gender-and-last-name
  [records]
  (sort-by #(vec [(::gender %) (::last-name %)])
           records))

(defn sort-by-date-asc
  [records]
  (sort-by ::date-of-birth records))

(def sort-by-date-desc
  (comp reverse sort-by-date-asc))

(def -sorters
  {:gender sort-by-gender-and-last-name
   :date-asc sort-by-date-asc
   :date-desc sort-by-date-desc
   :default sort-by-gender-and-last-name})

(defn sorter-for-key
  ([]
   sort-by-gender-and-last-name)
  ([sort-key]
   (if-let [sorter (get -sorters sort-key)]
     sorter
     (sorter-for-key)))
  ([sort-key default]
   (if-let [sorter (get -sorters sort-key)]
     sorter
     default)))

(defn record->json
  [record]
  (-> record
      record->display
      generate-string
      (str \newline)))