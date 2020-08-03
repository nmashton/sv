(ns sv.cmd.api
  (:require [clojure.tools.cli :refer [parse-opts]]
            [ring.adapter.jetty :as jetty]
            [sv.api :refer [handler]]
            [sv.util :refer [error-msg
                             exit
                             help-message]]))

(def opts
  [["-h" "--help" "Display this help message"]])

(def help-lines
  ["Launches a web server to receive and display records"
   "sorted by a specified sorter."
   ""
   "Options:"])

(defn validate-args
  [args]
  (let [{:keys [options errors summary]} (parse-opts args opts)]
    (cond
      (:help options) {:exit-message (help-message help-lines summary)
                       :ok? true}
      errors {:exit-message (error-msg errors)}
      :else {:start-server? true})))

(defn -main [& args]
  (let [{:keys [exit-message
                ok?
                start-server?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (if start-server?
        (jetty/run-jetty handler {:port 3000})
        (exit 0 nil)))))