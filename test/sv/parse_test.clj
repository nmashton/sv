(ns sv.parse-test
  (:require [clojure.test :refer [deftest is]]
            [sv.parse :as parse]
            [sv.testutil :refer [valid-quickchecked?]]))

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