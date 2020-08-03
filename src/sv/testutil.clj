(ns sv.testutil
  (:require [clojure.pprint :as pp]
            [clojure.spec.test.alpha :as st]
            [clojure.string :as string]
            [java-time :as jt]))

(def example-records
  [{:sv.model/last-name "Ashton"
    :sv.model/first-name "Neil"
    :sv.model/gender "male"
    :sv.model/favorite-color "indigo"
    :sv.model/date-of-birth (jt/local-date "MM/dd/yyyy" "08/16/1984")}
   {:sv.model/last-name "Bashton"
    :sv.model/first-name "Beil"
    :sv.model/gender "female"
    :sv.model/favorite-color "indigo"
    :sv.model/date-of-birth (jt/local-date "MM/dd/yyyy" "08/15/1984")}
   {:sv.model/last-name "Cashton"
    :sv.model/first-name "Ceil"
    :sv.model/gender "nonbinary"
    :sv.model/favorite-color "indigo"
    :sv.model/date-of-birth (jt/local-date "MM/dd/yyyy" "08/14/1984")}
   {:sv.model/last-name "Dashton"
    :sv.model/first-name "Deil"
    :sv.model/gender "male"
    :sv.model/favorite-color "indigo"
    :sv.model/date-of-birth (jt/local-date "MM/dd/yyyy" "08/13/1984")}])

(defn valid-quickchecked?
  "Helper to wrap quickcheck calls in unit tests."
  [sym]
  (let [result (-> (st/check sym)
                   first
                   :clojure.spec.test.check/ret)]
    (if (:pass? result)
      true
      (do (pp/pprint result)
          false))))

(defn example-line
  [components separator]
  (string/join separator components))

(defn example-data
  [separator]
  (string/join
   \newline
   (map #(example-line % separator)
        [["Ashton" "Neil" "nonbinary" "indigo" "08/17/1984"]
         ["Blashton" "Neil" "male" "crimson" "08/16/1984"]
         ["malformed" "line"]
         ["Clashton" "Neil" "male" "crimson" "08/16/1984" "malforming"]])))