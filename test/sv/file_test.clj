(ns sv.file-test
  (:require [clojure.test :refer [deftest is]]
            [sv.file :as file]))

(deftest filename->fmt
  (is (= :csv (file/filename->fmt "foo.csv")))
  (is (= :psv (file/filename->fmt "bar.psv")))
  (is (= :ssv (file/filename->fmt "baz.ssv")))
  (is (nil? (file/filename->fmt "qux.xml"))))
