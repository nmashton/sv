(ns sv.model-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer [deftest is]]
            [java-time :as jt]
            [sv.model :as model]))

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

(deftest record->display
  ; Data spot checks
  (is (s/valid? :sv.model/record (first example-records)))
  (is (s/valid? :sv.model/display (model/record->display  (first example-records))))
  (is (= example-display
         (model/record->display  (first example-records)))))

(deftest sort-by-gender-and-last-name
  (is (= ["Bashton" "Ashton" "Dashton" "Cashton"]
         (map :sv.model/last-name
              (model/sort-by-gender-and-last-name example-records)))))

(deftest sort-by-date-asc
  (is (= ["Dashton" "Cashton" "Bashton" "Ashton"]
         (map :sv.model/last-name
              (model/sort-by-date-asc example-records)))))

(deftest sort-by-date-desc
  (is (= ["Ashton" "Bashton" "Cashton" "Dashton"]
         (map :sv.model/last-name
              (model/sort-by-date-desc example-records)))))

(deftest sorter-for-key
  ; default
  (is (= ["Bashton" "Ashton" "Dashton" "Cashton"]
         (map :sv.model/last-name
              ((model/sorter-for-key) example-records))))
  ; with key
  (is (= ["Dashton" "Cashton" "Bashton" "Ashton"]
         (map :sv.model/last-name
              ((model/sorter-for-key :date-asc) example-records))))
  ; bad key -> default
  (is (= ["Bashton" "Ashton" "Dashton" "Cashton"]
         (map :sv.model/last-name
              ((model/sorter-for-key :bad-key) example-records)))))

(deftest record->json
  (is (= (model/record->json (first example-records))
         (str "{\"last-name\":\"Ashton\",\"first-name\":\"Neil\","
              "\"gender\":\"male\",\"favorite-color\":\"indigo\","
              "\"date-of-birth\":\"08/16/1984\"}\n"))))