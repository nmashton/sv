(ns sv.model-test
  (:require [clojure.test :as t])
  (:require [sv.model :as model])
  (:require [clojure.spec.alpha :as s])
  (:require [java-time :as jt]))

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
(def example-display
  {:last-name "Ashton"
   :first-name "Neil"
   :gender "male"
   :favorite-color "indigo"
   :date-of-birth "08/16/1984"})

; Generative testing is causing me issues, so in order to not lose
; further time to it, I'm going to use some example-based tests
; and move on.
(t/deftest display-date
  (t/is (s/valid? :sv.model/record (first example-records)))
  (t/is (s/valid? :sv.model/display (model/record->display  (first example-records))))
  (t/is (= example-display
           (model/record->display  (first example-records)))))

(t/deftest sort-by-gender-and-last-name
  (t/is (= ["Bashton" "Ashton" "Dashton" "Cashton"]
           (map :sv.model/last-name
                (model/sort-by-gender-and-last-name example-records)))))

(t/deftest sort-by-date-asc
  (t/is (= ["Dashton" "Cashton" "Bashton" "Ashton"]
           (map :sv.model/last-name
                (model/sort-by-date-asc example-records)))))

(t/deftest sort-by-date-desc
  (t/is (= ["Ashton" "Bashton" "Cashton" "Dashton"]
           (map :sv.model/last-name
                (model/sort-by-date-desc example-records)))))