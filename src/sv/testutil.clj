(ns sv.testutil
  (:require [clojure.spec.test.alpha :as st]))

(defn valid-quickchecked?
  "Helper to wrap quickcheck calls in unit tests."
  [sym]
  (-> (st/check sym)
      first
      :clojure.spec.test.check/ret
      :pass?))