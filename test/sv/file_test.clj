(ns sv.file-test
  (:require [clojure.test :as t])
  (:require [sv.file :as file]))

(t/deftest filename->fmt
  (t/is (= :csv (file/filename->fmt "foo.csv")))
  (t/is (= :psv (file/filename->fmt "bar.psv")))
  (t/is (= :ssv (file/filename->fmt "baz.ssv")))
  (t/is (nil? (file/filename->fmt "qux.xml"))))
