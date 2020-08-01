(ns sv.cmd
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require [clojure.string :as string]))

(def opts
  [["-s" "--sort-by" "Sort by"
    :default "gender"
    :parse-fn keyword
    :validate #{"gender"
                "date-asc"
                "date-desc"}]
   ["-i" "--ignore-errors" "Ignore errors"
    :default false]
   ["-h" "--help"]])

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
  (filter #(not (filename->fmt %)) filenames))

(defn validate-args
  [args]
  (let [{:keys [options errors]} (parse-opts args opts)]
    (cond
      (:help options) {:exit-message "TODO: add a helpful message"
                       :ok? true}
      errors {:exit-message (error-msg errors)}
      (seq (filenames->errors args))
      {:exit-message (error-msg (map #(str "Bad filename: " %)
                                     (filenames->errors args)))}
      :else {:exit-message "TODO: add useful behavior"
             :ok? true})))

(defn exit
  [status msg]
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [exit-message ok?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      "TODO: add action handlers")))