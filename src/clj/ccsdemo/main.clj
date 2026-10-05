(ns ccsdemo.main
  (:require [ring.adapter.jetty                :refer [run-jetty]]
            [ring.middleware.reload            :refer [wrap-reload]]
            [next.jdbc                         :as jdbc]
            [muuntaja.core                     :as m]
            [reitit.ring.middleware.muuntaja   :as muuntaja]
            [reitit.ring.middleware.parameters :as params]
            [reitit.ring                       :as reitit])
  (:gen-class))

(defonce db (jdbc/get-datasource {:dbtype "postgres" :dbname "ccsdemo"}))

(defn search-handler [req]
  (println (:query-params req))
  {:body (jdbc/execute! db ["SELECT * FROM hospice_stats"])})

(defn pins-handler [_]
  {})

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
