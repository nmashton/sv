(ns sv.file-test
  (:require [clojure.test :refer [deftest is]]
            [sv.file :as file]))

(deftest filename->fmt
  (is (= :sv.file/csv (file/filename->fmt "foo.csv")))
  (is (= :sv.file/psv (file/filename->fmt "bar.psv")))
  (is (= :sv.file/ssv (file/filename->fmt "baz.ssv")))
  (is (nil? (file/filename->fmt "qux.xml"))))

(deftest guess-format
  (is (= :sv.file/csv (file/guess-format "foo 123,bar abc,baz 456,qux def,norf 789")))
  (is (= :sv.file/psv (file/guess-format "foo 123|bar abc|baz 456|qux def|norf 789")))
  (is (= :sv.file/ssv (file/guess-format "foo bar baz qux norf")))
  (is (nil? (file/guess-format "not,a,valid,csv")))
  (is (nil? (file/guess-format "not|a|valid|psv")))
  (is (nil? (file/guess-format "not a valid ssv")))
  (is (nil? (file/guess-format "just ..... some gibberish"))))

(defn check-file-with-extension
  [extension]
  (let [temp-file (java.io.File/createTempFile "file" extension)
        path (.getAbsolutePath temp-file)]
    (try
      (doall (file/filenames-with-errors [path]))
      (finally (.delete temp-file)))))
(deftest filenames-with-errors
  (is (file/filenames-with-errors ["foo.bar"]))
  (is (seq (check-file-with-extension ".bad-extension")))
  (is (empty? (check-file-with-extension ".csv"))))