(ns sv.util-test
  (:require [clojure.test :as t])
  (:require [sv.util])
  (:require [sv.testutil :refer [valid-quickchecked?]]))

(t/deftest date-string->local-date
  (t/is (valid-quickchecked? 'sv.util/date-string->local-date))
  (t/is (nil? (sv.util/date-string->local-date "3000/3000/3000"))))