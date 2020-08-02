(ns sv.parse-test
  (:require [clojure.test :as t])
  (:require [sv.parse :as parse])
  (:require [sv.testutil :refer [valid-quickchecked?]]))

(t/deftest parse-lines
  (t/is (valid-quickchecked? 'sv.parse/parse-lines)))

(def example-results-errors
  [[{:sv.parse/data :valid-data-a}
    {:sv.parse/data :valid-data-b}]
   [{:sv.parse/line-number 1}
    {:sv.parse/line-number 2}
    {:sv.parse/line-number 3}]])

(t/deftest prep-for-cmd
  (t/is (= (parse/prep-for-cmd example-results-errors "test-data")
           ['(:valid-data-a :valid-data-b)
            ["test-data" '(1 2 3)]])))