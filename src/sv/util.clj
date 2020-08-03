(ns sv.util
  (:require [cheshire.core :refer [generate-string]]
            [clojure.string :as string]
            [java-time :as jt]))

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

(defn help-message
  [help-lines options-summary]
  (->> (conj help-lines options-summary)
       (string/join \newline)))

(def error-prefix
  "The following errors occurred while parsing your command:\n\n")
(defn error-msg
  "Format a vector of errors as a string and display it
   with an explanatory prefix."
  [errors]
  (str error-prefix
       (string/join \newline errors)))

(defn json-response
  [data]
  {:status 200
   :headers {"content-type" "application/json"}
   :body (generate-string data)})

(defn with-extra
  "Add some extra data to the handler's context.
   Useful for passing in (for example) some simple
   server state."
  [handler key val]
  (fn [req]
    (handler (assoc req key val))))