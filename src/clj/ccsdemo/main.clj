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
             :query ["SELECT * FROM hospice_enrolls WHERE state = ?"]}
   "enroll" {:redirect "pins"
             :query ["INSERT INTO hospice_pins (enroll) VALUES (?) ON CONFLICT DO NOTHING"]}})

(def pins
  "SELECT p.enroll, e.org_name FROM hospice_pins p INNER JOIN hospice_enrolls e ON p.enroll = e.enroll")

(def pins-api
  {nil      {:cols ["org_name"]
             :param "enroll"
             :query [pins]}
   "enroll" {:cols ["title" "role" "first" "middle" "last"]
             :query ["SELECT * FROM hospice_owners WHERE type = 'I' AND enroll = ?"]}})

(defonce db (jdbc/get-datasource {:dbtype "postgres" :dbname "ccsdemo"}))

(defn db-query [query]
  (jdbc/execute! db query {:builder-fn rs/as-unqualified-maps}))

(defn api-query [param params query]
  (if param
    (db-query (conj query (get params param)))
    (db-query query)))

(defn api-body [api params]
  (let [param (ffirst params)
        body (get api param)]
    (->
      body
      (assoc :rows (api-query param params (:query body)))
      (dissoc :query))))

(defn api-handler [api]
  (fn [req]
    {:body (api-body api (:query-params req))}))

(def routes
  [["/search" {:get (api-handler search-api)}]
   ["/pins"   {:get (api-handler pins-api)}]])

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
