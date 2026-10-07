(ns ccsdemo.config
  (:require [clojure.edn     :as edn]
            [clojure.java.io :as io]))

(defonce values (edn/read-string (slurp (io/resource "config.edn"))))
