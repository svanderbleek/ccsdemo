(ns ccsdemo.main
  (:require [ring.adapter.jetty            :refer [run-jetty]]
            [ring.middleware.reload        :refer [wrap-reload]]
            [muuntaja.middleware           :as muuntaja]
            [reitit.ring                   :as reitit])
  (:gen-class))

(defn search-handler [_]
  {:status 300})

(defn pins-handler [_]
  {:status 200})

(def routes
  [["/search" {:get search-handler}]
   ["/pins"   {:get pins-handler}]])

(def router (reitit/router routes))

(defn handler [request]
  {:status 404 :headers {"Content-Type" "text/plain"} :body "Not found"})

(def app
  (reitit/ring-handler
    (reitit/router routes)
    (reitit/routes
      (reitit/create-resource-handler {:path "/"})
      (reitit/create-default-handler))))

(def dev-app (wrap-reload #'app {:dirs ["src/clj"]}))

(defn -main []
  (run-jetty #'dev-app {:port 3001 :join? false}))
