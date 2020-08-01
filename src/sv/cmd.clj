(ns sv.cmd
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require [clojure.string :as string]))

(def opts
  [["-i" "--ignore-errors" "Ignore errors"
    :default false]
   ["-h" "--help"]])

(defn error-msg
  [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn validate-args
  [args]
  (let [{:keys [options errors]} (parse-opts args opts)]
    (cond
      (:help options) "TODO: add a helpful message"
      errors (error-msg errors)
      :else "TODO: add useful behavior")))

(defn -main [& args]
  (println (validate-args args)))