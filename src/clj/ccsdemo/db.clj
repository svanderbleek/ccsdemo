(ns ccsdemo.db
  (:require [next.jdbc            :as jdbc]
            [next.jdbc.result-set :as rs]))

(defonce db (jdbc/get-datasource {:dbtype "postgres" :dbname "ccsdemo"}))

(defn query! [query]
  (jdbc/execute! db query {:builder-fn rs/as-unqualified-maps}))
