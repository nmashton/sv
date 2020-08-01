(ns sv.cmd-test
  (:require [clojure.test :as t])
  (:require [sv.cmd :as cmd]))

(t/deftest filename->fmt
  (t/is (= :csv (cmd/filename->fmt "foo.csv")))
  (t/is (= :psv (cmd/filename->fmt "bar.psv")))
  (t/is (= :ssv (cmd/filename->fmt "baz.ssv")))
  (t/is (nil? (cmd/filename->fmt "qux.xml"))))
