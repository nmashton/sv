(ns sv.util
  (:require [clojure.string :as string])
  (:require [clojure.spec.alpha :as s])
  (:require [clojure.spec.gen.alpha :as gen])
  (:require [java-time :as jt]))

(defn date-string=local-date?
  "Compares a given datestring formatted by MM/dd/yyyy
   with a given local sate to see if they represent the same date."
  [date-string date]
  (let [[m d y] (map #(Integer/parseInt %)
                     (string/split date-string #"/"))]
    (and (= m (jt/as date :month-of-year))
         (= d (jt/as date :day-of-month))
         (= y (jt/as date :year)))))

(defn gen-nonempty-vector
  [& args]
  (gen/such-that
   #(not= [] %)
   (apply gen/vector args)))

(def gen-nonempty-int-string
  (gen/such-that
   #(not= "" %)
   (gen/fmap
    #(str (Math/abs %))
    (gen/int))))

(defn date-string-gen
  "Returns a generator to produce strings that
   may or may not be valid datestrings in the
   MM/dd/yyyy format. Some will, others very"
  []
  (gen/fmap #(->> %
                  (interleave (repeat "/"))
                  rest
                  (apply str))
            (gen-nonempty-vector
             gen-nonempty-int-string)))

(s/fdef date-string->local-date
  :args (s/cat :date-string
               (s/with-gen
                 string?
                 date-string-gen))
  :ret (s/nilable #(instance? java.time.LocalDate %))
  :fn (fn [{:keys [args ret]}]
        (or (not ret)
            (date-string=local-date? (first args) ret))))

(defn date-string->local-date
  "A safe function to convert datestrings into local dates
   according to our specified format. Captures exceptions
   and (silently) returns nil."
  [date-string]
  (try
    (jt/local-date "MM/dd/yyyy" date-string)
    (catch Exception _ nil)))