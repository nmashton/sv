(ns sv.util
  (:require [cheshire.core :refer [generate-string]]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.string :as string]
            [java-time :as jt]))

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

(defn date-gen
  []
  (gen/fmap #(apply jt/local-date
                    (let [date (jt/java-date (* 100000000 %))]
                      [(.getYear date)
                       (inc (.getMonth date))
                       (inc (.getDay date))]))
            (gen/such-that
             #(< 0 %)
             (gen/int))))

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

(defn exit
  "Print a message and exit with a given status."
  [status msg]
  (println msg)
  (System/exit status))

(defn error-msg
  "Format a vector of errors as a string and display it
   with an explanatory prefix."
  [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn json-response
  [data]
  {:status 200
   :headers {"content-type" "application/json"}
   :body (str
          (generate-string data {:pretty true})
          \newline)})

(defn with-extra
  "Add some extra data to the handler's context.
   Useful for passing in (for example) some simple
   server state."
  [handler key val]
  (fn [req]
    (handler (assoc req key val))))