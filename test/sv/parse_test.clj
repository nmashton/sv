(ns sv.parse-test
  (:require [clojure.test :refer [deftest is]]
            [java-time :as jt]
            [sv.parse :as parse]
            [sv.testutil :refer [example-data valid-quickchecked?]]))

(deftest parse-lines
  (is (valid-quickchecked? 'sv.parse/parse-lines)))

(def example-results-errors
  [[{:sv.parse/data :valid-data-a}
    {:sv.parse/data :valid-data-b}]
   [{:sv.parse/line-number 1}
    {:sv.parse/line-number 2}
    {:sv.parse/line-number 3}]])

(deftest prep-for-cmd
  (is (= (parse/prep-for-cmd example-results-errors "test-data")
         ['(:valid-data-a :valid-data-b)
          ["test-data" '(1 2 3)]])))

(defn gen-example-parse-result
  [filename]
  [[{:sv.model/last-name "Blashton"
     :sv.model/first-name "Neil"
     :sv.model/gender "male"
     :sv.model/favorite-color "crimson"
     :sv.model/date-of-birth (jt/local-date "MM/dd/yyyy" "08/16/1984")}
    {:sv.model/last-name "Ashton"
     :sv.model/first-name "Neil"
     :sv.model/gender "nonbinary"
     :sv.model/favorite-color "indigo"
     :sv.model/date-of-birth (jt/local-date "MM/dd/yyyy" "08/17/1984")}]
   [filename '(2 3)]])

(defn check-with-separator
  [example-data separator extension]
  (let [temp-file (java.io.File/createTempFile "tmp" extension)
        filename (.getAbsolutePath temp-file)]
    (try
      (spit temp-file example-data)
      (= (parse/parse-file filename separator)
         (gen-example-parse-result filename))
      (finally (.delete temp-file)))))

(deftest parse-file
  (is (check-with-separator (example-data ",") "," ".csv"))
  (is (check-with-separator (example-data "|") "\\|" ".psv"))
  (is (check-with-separator (example-data " ") " " ".ssv")))