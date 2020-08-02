(ns sv.api
  (:require [ring.adapter.jetty :as jetty])
  (:require [compojure.core :refer [defroutes GET]])
  (:require [compojure.route :as route]))


(defn handler
  [{store :store}]
  (swap! store #(conj % :foo))
  {:status 200
   :headers {"content-type" "text/clojure"}
   :body (str @store "\n")})

(defn with-store
  [handler store]
  (fn [req]
    (handler (assoc req :store store))))

(def not-found
  {:status 404
   :headers {"content-type" "text/html"}
   :body "Not found\n"})

(defroutes records-api
  (GET "/records/:sort" req (handler req))
  (route/not-found not-found))

(defn -main []
  (let [store (atom [])]
    (jetty/run-jetty
     (with-store records-api store)
     {:port 3000})))
