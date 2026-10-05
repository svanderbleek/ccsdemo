(ns ccsdemo.routes
  (:require [muuntaja.core                     :as m]
            [reitit.ring.middleware.muuntaja   :as muuntaja]
            [reitit.ring.middleware.parameters :as params]
            [reitit.ring                       :as reitit]
            [ccsdemo.api                       :as api]))

(def routes
  [["/search" {:get (api/handler-for api/search)}]
   ["/pins"   {:get (api/handler-for api/pins)}]])

(def router (reitit/router routes))

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
