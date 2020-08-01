(ns sv.parse-test
  (:require [clojure.test :as t])
  (:require [sv.parse])
  (:require [sv.testutil :refer [valid-quickchecked?]]))

(t/deftest parse-lines
  (t/is (valid-quickchecked? 'sv.parse/parse-lines)))