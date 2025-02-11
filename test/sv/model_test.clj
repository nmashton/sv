(ns sv.model-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer [deftest is]]
            [sv.model :as model]
            [sv.testutil :refer [example-records]]))

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

(deftest sort-by-birthdate
  (is (= ["Dashton" "Cashton" "Bashton" "Ashton"]
         (map :sv.model/last-name
              (model/sort-by-birthdate example-records)))))

(deftest sort-by-name
  (is (= ["Dashton" "Cashton" "Bashton" "Ashton"]
         (map :sv.model/last-name
              (model/sort-by-name example-records)))))

(deftest sorter-for-key
  ; default
  (is (= ["Dashton" "Cashton" "Bashton" "Ashton"]
         (map :sv.model/last-name
              ((model/sorter-for-key) example-records))))
  ; with key
  (is (= ["Dashton" "Cashton" "Bashton" "Ashton"]
         (map :sv.model/last-name
              ((model/sorter-for-key :birthdate) example-records))))
  ; bad key -> default
  (is (= ["Dashton" "Cashton" "Bashton" "Ashton"]
         (map :sv.model/last-name
              ((model/sorter-for-key :bad-key) example-records)))))

(deftest record->json
  (is (= (model/record->json (first example-records))
         (str "{\"last-name\":\"Ashton\",\"first-name\":\"Neil\","
              "\"gender\":\"male\",\"favorite-color\":\"indigo\","
              "\"date-of-birth\":\"08/16/1984\"}"))))