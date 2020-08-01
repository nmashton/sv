(ns sv.model-test
  (:require [clojure.test :as t])
  (:require [sv.model :as model])
  (:require [clojure.spec.alpha :as s])
  (:require [java-time :as jt]))

(def example-record
  {:sv.model/last-name "Ashton"
   :sv.model/first-name "Neil"
   :sv.model/gender "male"
   :sv.model/favorite-color "indigo"
   :sv.model/date-of-birth (jt/local-date "MM/dd/yyyy" "08/16/1984")})
(def example-display
  {:last-name "Ashton"
   :first-name "Neil"
   :gender "male"
   :favorite-color "indigo"
   :date-of-birth "08/16/1984"})

; Generative testing is causing me issues, so in order to not lose
; further time to it, I'm going to use some example-based tests
; and move on.
(t/deftest display-date
  (t/is (s/valid? :sv.model/record example-record))
  (t/is (s/valid? :sv.model/display (model/record->display example-record)))
  (t/is (= example-display
           (model/record->display example-record))))