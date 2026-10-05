(ns ccsdemo.main
  (:require [ring.adapter.jetty     :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ccsdemo.routes         :as    routes])
  (:gen-class))

(def dev-app (wrap-reload #'routes/app {:dirs ["src/clj"]}))

(defn -main []
  (run-jetty #'dev-app {:port 3001 :join? false}))
