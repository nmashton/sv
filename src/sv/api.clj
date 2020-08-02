(ns sv.api
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require [clojure.pprint :refer [pprint]])
  (:require [clojure.string :as string])
  (:require [ring.adapter.jetty :as jetty])
  (:require [compojure.core :refer [defroutes GET]])
  (:require [compojure.route :as route])
  (:require [sv.parse :as parse])
  (:require [sv.file :as file]))

(def opts
  [["-s" "--sort-by SORT-TYPE" "Sort by (gender, date-asc, date-desc)"
    :default :gender
    :parse-fn keyword
    :validate [#{:gender :date-asc :date-desc}
               "Must be one of gender, date-asc, or date-desc"]]
   ["-h" "--help" "Display this help message"]])

(defn help-message
  [options-summary]
  (->> ["Launches a web server to receive and display records"
        "sorted by a specified sorter."
        ""
        "Options:"
        options-summary]
       (string/join \newline)))

(defn error-msg
  [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn validate-args
  [args]
  (let [{:keys [options errors summary]} (parse-opts args opts)]
    (cond
      (:help options) {:exit-message (help-message summary)
                       :ok? true}
      errors {:exit-message (error-msg errors)}
      :else {:start-server? true
             :options options})))

(defn exit
  [status msg]
  (println msg)
  (System/exit status))

(defn handler
  [{store :store
    {_sort :sort-by} :options}]
  ;; (swap! store #(conj % sort))
  {:status 200
   :headers {"content-type" "text/clojure"}
   :body (with-out-str (pprint @store))})

(def not-found
  {:status 404
   :headers {"content-type" "text/html"}
   :body "Not found\n"})

(defroutes records-api
  (GET "/records/:sort" req (handler req))
  (route/not-found not-found))

(defn with-extra
  [handler key val]
  (fn [req]
    (handler (assoc req key val))))

(def default-filenames
  ["resources/test.csv"
   "resources/test.psv"
   "resources/test.ssv"])

(defn init-data
  []
  (let [[records _errors] (parse/combine-parse-results (map file/parse-filename default-filenames))
        sorter (get file/sorters :default)]
    (sorter records)))

(defn -main [& args]
  (let [{:keys [exit-message ok? start-server? options]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (if start-server?
        (let [store (atom (init-data))]
          (jetty/run-jetty
           (-> records-api
               (with-extra :store store)
               (with-extra :options options))
           {:port 3000}))
        (exit 0 nil)))))