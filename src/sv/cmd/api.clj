(ns sv.cmd.api
  (:require [cheshire.core :refer [generate-string]]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [ring.util.request :refer [body-string]]
            [sv.model :as model]
            [sv.parse :as parse]
            [sv.file :as file]))

(def opts
  [["-h" "--help" "Display this help message"]])

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
      :else {:start-server? true})))

(defn exit
  [status msg]
  (println msg)
  (System/exit status))

(defn display-records-handler
  [{:keys [store sort-by]}]
  {:status 200
   :headers {"content-type" "application/json"}
   :body (str
          (generate-string (map model/record->display (sort-by @store))
                           {:pretty true})
          "\n")})

(def not-found
  {:status 404
   :headers {"content-type" "text/html"}
   :body "Not found\n"})

(defn sorted-or-404
  "Display the records in the store according to
   the sort scheme given in the parameters or return
   404 if the sort scheme is invalid."
  [{{sort-by :sort-by} :params
    :as req}]
  (if-let [sorter (model/sorter-for-key (keyword sort-by) nil)]
    (display-records-handler (assoc req :sort-by sorter))
    not-found))

(defn add-or-400
  "Attempt to add some submitted data to the store and
   echo back the resulting record (in display format)
   on success.
   
   Tries to guess the format of the input line using
   file/guess-format, and if this fails, returns a 400.
   
   Having guessed the format of the input line, tries
   to parse it, and if this fails, returns a 400. If
   it succeeds, adds it to the store and echoes back
   the result."
  [{:keys [store] :as req}]
  (let [raw-line (body-string req)]
    (if-let [fmt (file/guess-format raw-line)]
      (let [{data :sv.parse/data} (parse/parse-raw-line raw-line (get file/separators fmt))]
        (if data
          (do
            (swap! store #(conj % data))
            (str (generate-string (model/record->display data)) "\n"))
          {:status 400
           :body "Errors in input\n"}))
      {:status 400
       :body "Can't guess input format\n"})))

(defroutes records-api
  (POST "/records/" req (add-or-400 req))
  (GET "/records/:sort-by" req (sorted-or-404 req))
  (route/not-found not-found))

(defn with-extra
  "Add some extra data to the handler's context.
   Useful for passing in (for example) some simple
   server state."
  [handler key val]
  (fn [req]
    (handler (assoc req key val))))

(def default-filenames
  ["resources/test.csv"
   "resources/test.psv"
   "resources/test.ssv"])

(defn init-data
  []
  (let [[records _errors]
        (parse/combine-parse-results (map file/parse-filename
                                          default-filenames))
        sorter (model/sorter-for-key)]
    (sorter records)))

(defn -main [& args]
  (let [{:keys [exit-message
                ok?
                start-server?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (if start-server?
        (let [store (atom (init-data))]
          (jetty/run-jetty
           (with-extra records-api :store store)
           {:port 3000}))
        (exit 0 nil)))))