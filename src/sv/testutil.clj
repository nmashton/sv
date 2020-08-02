(ns sv.testutil
  (:require [clojure.spec.test.alpha :as st])
  (:require [clojure.pprint :as pp]))

(defn valid-quickchecked?
  "Helper to wrap quickcheck calls in unit tests."
  [sym]
  (let [result (-> (st/check sym)
                   first
                   :clojure.spec.test.check/ret)]
    (if (:pass? result)
      true
      (do (pp/pprint result)
          false))))