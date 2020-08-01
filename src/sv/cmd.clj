(ns sv.cmd
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require [clojure.string :as string])
  (:require [clojure.java.io :as io])
  (:require [clojure.pprint :refer [pprint]])
  (:require [sv.parse :as parse]))

(def opts
  [["-s" "--sort-by" "Sort by (gender, date-asc, date-desc)"
    :default "gender"
    :parse-fn keyword
    :validate #{"gender"
                "date-asc"
                "date-desc"}]
   ["-i" "--ignore-errors" "Ignore errors"
    :default false]
   ["-h" "--help" "Display this help message"]])

(def separators
  {:csv ","
   :psv "[|]"
   :ssv " "})

(defn help-message
  [options-summary]
  (->> ["Prints a collection of records extracted from a file in"
        "a 'separated values' format (comma-separated, pipe-separated,"
        "or space-separated), sorted according to either gender,"
        "date ascending, or date descending."
        ""
        "Options:"
        options-summary]
       (string/join \newline)))

(defn error-msg
  [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn filename->fmt
  [filename]
  (cond
    (re-matches #"\S+\.csv" filename) :csv
    (re-matches #"\S+\.psv" filename) :psv
    (re-matches #"\S+\.ssv" filename) :ssv
    :else nil))

(defn filenames->errors
  [filenames]
  (filter
   #(not (and (filename->fmt %)
              (.exists (io/file %))))
   filenames))

(defn parse-filename
  [filename]
  (parse/parse-file filename (get separators (filename->fmt filename))))

(defn validate-args
  [args]
  (let [{:keys [options errors summary]} (parse-opts args opts)]
    (cond
      (:help options) {:exit-message (help-message summary)
                       :ok? true}
      errors {:exit-message (error-msg errors)}
      (not (seq args))
      {:exit-message (error-msg ["Missing filename arguments."])}
      (seq (filenames->errors args))
      {:exit-message (error-msg (map #(str "Bad filename or file not found: " %)
                                     (filenames->errors args)))}
      :else {:parse? true
             :filenames args
             :options options})))

(defn exit
  [status msg]
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [exit-message ok? parse? filenames]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (if parse?
        (exit 0 (pprint (parse/combine-parse-results (map parse-filename filenames))))
        (exit 0 nil)))))