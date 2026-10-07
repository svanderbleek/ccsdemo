(ns ccsdemo.db
  (:require [next.jdbc            :as jdbc]
            [next.jdbc.result-set :as rs]
            [ccsdemo.config       :as conf]))

(defonce db (jdbc/get-datasource (:database conf/values)))

(defn query! [query]
  (jdbc/execute! db query {:builder-fn rs/as-unqualified-maps}))
