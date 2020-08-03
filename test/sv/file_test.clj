(ns sv.file-test
  (:require [clojure.test :refer [deftest is]]
            [sv.file :as file]))

(deftest filename->fmt
  (is (= :sv.file/csv (file/filename->fmt "foo.csv")))
  (is (= :sv.file/psv (file/filename->fmt "bar.psv")))
  (is (= :sv.file/ssv (file/filename->fmt "baz.ssv")))
  (is (nil? (file/filename->fmt "qux.xml"))))

(deftest guess-format
  (is (= :sv.file/csv (file/guess-format "foo,bar,baz,qux,norf")))
  (is (= :sv.file/psv (file/guess-format "foo|bar|baz|qux|norf")))
  (is (= :sv.file/ssv (file/guess-format "foo bar baz qux norf")))
  (is (nil? (file/guess-format "not,a,valid,csv")))
  (is (nil? (file/guess-format "not|a|valid|psv")))
  (is (nil? (file/guess-format "not a valid ssv")))
  (is (nil? (file/guess-format "just ..... some gibberish"))))

(deftest filenames->errors
  (is (file/filenames->errors ["foo.bar"]))
  (is (not (empty (let [temp-file (java.io.File/createTempFile "file" ".bad-extension")]
                    (try
                      (file/filenames->errors [(.getAbsolutePath temp-file)])
                      (finally (.delete temp-file)
                               nil))))))
  (is (empty? (let [temp-file (java.io.File/createTempFile "file" ".csv")]
                (try
                  (doall (file/filenames->errors [(.getAbsolutePath temp-file)]))
                  (finally (.delete temp-file)))))))