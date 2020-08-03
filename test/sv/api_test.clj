(ns sv.api-test
  (:require [clojure.test :refer [deftest is]]
            [sv.api :as api]
            [sv.model :as model]
            [sv.testutil :as testutil]
            [sv.util :as util]))

(defn check-with-key-and-sorter
  [k sorter]
  (let [store (atom testutil/example-records)
        req {:params {:sort-by k}
             :store store}]
    (= (api/sorted-or-404 req)
       (util/json-response
        (map model/record->display
             (sorter @store))))))
(deftest sorted-or-404
  (is (check-with-key-and-sorter "gender" model/sort-by-gender-and-last-name))
  (is (check-with-key-and-sorter "date-asc" model/sort-by-date-asc))
  (is (check-with-key-and-sorter "date-desc" model/sort-by-date-desc))
  (is (= (api/sorted-or-404 {:params {:sort-by "invalid"}})
         api/not-found)))

(def example-components
  ["Ashton" "Neil" "male" "indigo" "08/16/1984"])
(def example-record
  (first testutil/example-records))
(defn check-with-separator
  [separator]
  (let [store (atom [])
        req {:store store
             :body (testutil/example-line example-components separator)}
        result (api/add-or-400 req)]
    (and (= result (model/record->json example-record))
         (= @store [example-record]))))
(deftest add-or-400
  (is (check-with-separator ","))
  (is (check-with-separator "|"))
  (is (check-with-separator " "))
  (is (= (api/add-or-400 {:body "bad input"})
         {:status 400
          :body "Errors in input\n"})))