(ns sv.cmd.api
  (:require [clojure.tools.cli :refer [parse-opts]]
            [ring.adapter.jetty :as jetty]
            [sv.api :refer [init-handler]]
            [sv.util :refer [error-msg
                             exit
                             help-message]]))

(def opts
  [["-p" "--port PORT" "Server port"
    :default 3000
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ["-h" "--help" "Display this help message"]])

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
      :else {:start-server? true
             :options options})))

(defn -main [& args]
  (let [{:keys [exit-message
                ok?
                start-server?
                options]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (if start-server?
        (jetty/run-jetty (init-handler) {:port (:port options)})
        (exit 0 nil)))))