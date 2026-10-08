(ns server
  (:require [ring.adapter.jetty     :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ccsdemo.routes         :as    routes]
            [ccsdemo.config         :as conf])
  (:gen-class))

(def dev-app (wrap-reload #'routes/app {:dirs ["src/clj"]}))

(defn -main []
  (run-jetty #'dev-app (:server conf/values)))
