(ns ccsdemo.main
  (:require [ring.adapter.jetty       :refer [run-jetty]]
            [ring.middleware.resource :as ring]
            [reitit.ring              :as reitit])
  (:gen-class))

(defn handler [request]
  {:status 404 :headers {"Content-Type" "text/plain"} :body "Not found"})

(def app
  (-> handler
    (ring/wrap-resource "public")
    (ring/wrap-content-type)
    (ring/wrap-not-modified)
    (ring/wrap-params))

(defn -main []
  (run-jetty app {:port 3001 :join? true}))
