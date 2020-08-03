(ns sv.util-test
  (:require [clojure.test :refer [deftest is]]
            [sv.util]
            [sv.testutil :refer [valid-quickchecked?]]))

(deftest date-string->local-date
  (is (valid-quickchecked? 'sv.util/date-string->local-date))
  (is (nil? (sv.util/date-string->local-date "3000/3000/3000"))))