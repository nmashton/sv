(ns sv.cmd.cli
  (:require [clojure.tools.cli :refer [parse-opts]]
            [sv.cli :refer [check-filenames
                            handle-filenames]]
            [sv.file :as file]
            [sv.util :refer [error-msg exit help-message]]))

(def opts
  [["-s" "--sort-by SORT-TYPE" "Sort by (gender, date-asc, date-desc)"
    :default :gender
    :parse-fn keyword
    :validate [#{:gender :date-asc :date-desc}
               "Must be one of gender, date-asc, or date-desc"]]
   ["-i" "--ignore-errors" "Ignore errors"]
   ["-h" "--help" "Display this help message"]])

(def help-lines
  ["Prints a collection of records extracted from a file in"
   "a 'separated values' format (comma-separated, pipe-separated,"
   "or space-separated), sorted according to either gender,"
   "date ascending, or date descending."
   ""
   "Options:"])

(defn validate-args
  [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args opts)]
    (cond
      (:help options) {:exit-message (help-message help-lines summary)
                       :ok? true}
      errors {:exit-message (error-msg errors)}
      (not (seq args))
      {:exit-message (error-msg ["Missing filename arguments."])}
      (check-filenames arguments)
      {:exit-message (error-msg (map #(str "Bad filename or file not found: " %)
                                     (file/filenames-with-errors arguments)))}
      :else {:parse? true
             :filenames arguments
             :options options})))

(defn -main [& args]
  (let [{:keys [exit-message ok? parse? filenames options]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (if parse?
        (let [{:keys [error output]} (handle-filenames filenames options)]
          (if error
            (exit 1 (error-msg [error]))
            (exit 0 output)))
        (exit 0 nil)))))