(ns sv.model
  (:require [clojure.spec.alpha :as s]
            [java-time :as jt]
            [sv.util :refer [date-string-gen date-gen]]))

(s/def ::last-name string?)
(s/def ::first-name string?)
(s/def ::gender #{"female"
                  "male"
                  "nonbinary"
                  "other"})
(s/def ::date-of-birth (s/with-gen
                        #(instance? java.time.LocalDate %)
                        date-gen))
(s/def ::favorite-color string?)

(s/def ::record
  (s/keys :req [::last-name
                ::first-name
                ::gender
                ::favorite-color
                ::date-of-birth]))

(s/def :sv.display/date-of-birth (s/with-gen
                                   string?
                                   date-string-gen))
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