(ns ccsdemo.main
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :refer [resource-response]])
  (:gen-class))

(defn handler [request]
  {:status 404 :headers {"Content-Type" "text/plain"} :body "Not found"})

(def app
  (wrap-resource handler "public"))

(defn -main []
  (run-jetty app {:port 3001 :join? true}))
