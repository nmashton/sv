(ns sv.cmd
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require [clojure.string :as string])
  (:require [clojure.java.io :as io])
  (:require [clojure.pprint :refer [print-table]])
  (:require [sv.model :as model])
  (:require [sv.parse :as parse]))

(def opts
  [["-s" "--sort-by SORT-TYPE" "Sort by (gender, date-asc, date-desc)"
    :default :gender
    :parse-fn keyword
    :validate [#{:gender :date-asc :date-desc}
               "Must be one of gender, date-asc, or date-desc"]]
   ["-i" "--ignore-errors" "Ignore errors"]
   ["-h" "--help" "Display this help message"]])

(def separators
  {:csv ","
   :psv "[|]"
   :ssv " "})
(def sorters
  {:gender model/sort-by-gender-and-last-name
   :date-asc model/sort-by-date-asc
   :date-desc model/sort-by-date-desc})

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
  (let [{:keys [options arguments errors summary]} (parse-opts args opts)]
    (cond
      (:help options) {:exit-message (help-message summary)
                       :ok? true}
      errors {:exit-message (error-msg errors)}
      (not (seq args))
      {:exit-message (error-msg ["Missing filename arguments."])}
      (seq (filenames->errors arguments))
      {:exit-message (error-msg (map #(str "Bad filename or file not found: " %)
                                     (filenames->errors arguments)))}
      :else {:parse? true
             :filenames arguments
             :options options})))

(defn exit
  [status msg]
  (println msg)
  (System/exit status))

(defn handle-filenames
  [filenames options]
  (let [[records errors] (parse/combine-parse-results (map parse-filename filenames))
        sorter (get sorters (:sort-by options))]
    (if (and (seq errors)
             (not (:ignore-errors options)))
      (exit 1 (error-msg ["Some files had errors."])) ; TODO: say more!!
      (exit 0 (with-out-str
                (print-table (->> records
                                  sorter
                                  (map model/record->display))))))))

(defn -main [& args]
  (let [{:keys [exit-message ok? parse? filenames options]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (if parse?
        (handle-filenames filenames options)
        (exit 0 nil)))))