(ns sv.util-test
  (:require [clojure.string :as string]
            [clojure.test :refer [deftest is]]
            [java-time :as jt]
            [sv.util :as util]))

(deftest date-string->local-date
  (is (= (util/date-string->local-date "08/16/1984")
         (jt/local-date "MM/dd/yyyy" "08/16/1984")))
  (is (nil? (util/date-string->local-date "3000/3000/3000"))))

(def example-help-lines
  ["foo" "bar"])
(def example-options-summary
  "baz qux")
(def example-help-message
  (string/join \newline
               (conj example-help-lines
                     example-options-summary)))
(deftest help-message
  (is (= (util/help-message example-help-lines example-options-summary)
         example-help-message)))

(def example-errors
  ["foo" "bar"])
(deftest error-msg
  (is (= (util/error-msg example-errors)
         (str util/error-prefix
              (string/join \newline example-errors)))))

(deftest json-response
  (is (= (util/json-response ["foo" "bar" {"baz" "qux"}])
         {:status 200
          :headers {"content-type" "application/json"}
          :body "[\"foo\",\"bar\",{\"baz\":\"qux\"}]"})))

(deftest with-extra
  (is (= (let [wrapped (util/with-extra identity :foo "bar")]
           (wrapped {}))
         {:foo "bar"})))