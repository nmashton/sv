(ns sv.api
  (:require [ring.adapter.jetty :as jetty]))


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

(defn -main []
  (let [store (atom [])]
    (jetty/run-jetty
     (with-store handler store)
     {:port 3000})))
