(ns ccsdemo.main
  (:require [ring.adapter.jetty                :refer [run-jetty]]
            [ring.middleware.reload            :refer [wrap-reload]]
            [next.jdbc                         :as jdbc]
            [next.jdbc.result-set              :as rs]
            [muuntaja.core                     :as m]
            [reitit.ring.middleware.muuntaja   :as muuntaja]
            [reitit.ring.middleware.parameters :as params]
            [reitit.ring                       :as reitit])
  (:gen-class))

(def search-api
  {nil      {:cols ["state" "payout" "persons" "days"]
             :param "state"
             :query ["SELECT * FROM hospice_stats"]}
   "state"  {:cols ["org_name" "org_type" "provider" "nonprofit"]
             :param "enroll"
             :query ["SELECT * FROM hospice_enrolls WHERE state = ? LIMIT 100"]}
   "enroll" {:redirect "pins"
             :query ["INSERT INTO hospice_pins (enroll) VALUES (?) ON CONFLICT DO NOTHING"]}})

(defonce db (jdbc/get-datasource {:dbtype "postgres" :dbname "ccsdemo"}))

(defn db-query [query]
  (jdbc/execute! db query {:builder-fn rs/as-unqualified-maps}))

(defn search-query [param params query]
  (if param
    (db-query (conj query (get params param)))
    (db-query query)))

(defn search-body [params]
  (let [param (ffirst params)
        body (get search-api param)]
    (->
      body
      (assoc :rows (search-query param params (:query body)))
      (dissoc :query))))

(defn search-handler [req]
  {:body (search-body (:query-params req))})

(defn pins-handler [_]
  {:body {:cols ["enroll"]
          :rows (db-query ["SELECT * FROM hospice_pins"])}})

(def routes
  [["/search" {:get search-handler}]
   ["/pins"   {:get pins-handler}]])

(def router (reitit/router routes))

(defn handler [request]
  {:status 404 :headers {"Content-Type" "text/plain"} :body "Not found"})

(def app
  (reitit/ring-handler
    (reitit/router
      routes
      {:data {:muuntaja m/instance
              :middleware [params/parameters-middleware
                           muuntaja/format-middleware]}})
    (reitit/routes
      (reitit/create-resource-handler {:path "/"})
      (reitit/create-default-handler))))

(def dev-app (wrap-reload #'app {:dirs ["src/clj"]}))

(defn -main []
  (run-jetty #'dev-app {:port 3001 :join? false}))
