(ns ccsdemo.main
  (:require [ring.adapter.jetty     :refer [run-jetty]]
            [ccsdemo.routes         :as    routes]
            [ccsdemo.config         :as conf])
  (:gen-class))

(defn -main []
  (run-jetty routes/app (:server conf/values)))
