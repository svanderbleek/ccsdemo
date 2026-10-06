(ns ingest
  (:require [tablecloth.api    :as tc]
            [honey.sql         :as sql]
            [honey.sql.helpers :as h]
            [next.jdbc         :as jdbc]))

(def data-maps
  [{:file "Hospice_CPS_2024.csv"
    :fields ["State",
             "Total Program Payments",
             "Total Persons With Utilization",
             "Total Covered Days of Care"]
    :table :hospice_stats
    :schema [[:state   [:char 2] [:not nil] [:primary-key]]
             [:payout  :bigint]
             [:persons :bigint]
             [:days    :bigint]]},
   {:file "Hospice_Enrollments_2026.07.17.csv"
    :fields ["ENROLLMENT ID",
             "ENROLLMENT STATE",
             "PROVIDER TYPE TEXT",
             "PROPRIETARY_NONPROFIT",
             "ORGANIZATION NAME",
             "ORGANIZATION TYPE STRUCTURE"]
    :table :hospice_enrolls
    :schema [[:enroll    [:char 15] [:not nil] [:primary-key]]
             [:state     [:char 2] [:not nil]]
             [:provider  [:varchar 200]]
             [:nonprofit [:char 1] [:not nil]]
             [:org_name  [:varchar 70]]
             [:org_type  [:varchar 60]]]},
   {:file "Hospice_All_Owners_2026.07.17.csv"
    :fields ["ENROLLMENT ID",
             "TYPE - OWNER",
             "TITLE - OWNER",
             "ROLE TEXT - OWNER",
             "FIRST NAME - OWNER",
             "MIDDLE NAME - OWNER",
             "LAST NAME - OWNER"]
    :table :hospice_owners
    :schema [[:enroll [:char 15] [:not nil]]
             [:type   [:char 1] [:not nil]]
             [:title  [:varchar 35]]
             [:role   [:varchar 100]]
             [:first  [:varchar 25]]
             [:middle [:varchar 25]]
             [:last   [:varchar 25]]]}])

(defn file-path [data-map]
  (str "data/files/" (:file data-map)))

(defn load-data [data-map]
  (->
    (tc/dataset (file-path data-map))
    (tc/select-columns (:fields data-map))))

(defn make-table [data-map]
  (->
    (h/create-table (:table data-map))
    (h/with-columns (:schema data-map))
    (sql/format)))

(defn columns [data-map]
  (map first (:schema data-map)))

(defn insert-table-values [data-map values]
  (->
    (h/insert-into (:table data-map))
    (#(apply h/columns % (columns data-map)))
    (h/values values)
    (sql/format)))

(defn insert-table-all [data-map]
  (insert-table-values data-map (tc/rows (load-data data-map))))

(defn insert-table-chunks [data-map chunks]
  (->>
    (load-data data-map)
    tc/rows
    (partition-all chunks)
    (map (partial insert-table-values data-map))))

(defonce db (jdbc/get-datasource {:dbtype "postgres" :dbname "ccsdemo"}))

(defn make-table! [data-map]
  (jdbc/execute! db (make-table data-map)))

(defn insert-table! [inserts]
  (run! (partial jdbc/execute! db) inserts))

(defn exec! [statement]
  (jdbc/execute! db statement))

(comment
  ;; make all tables
  (run! make-table! data-maps)
  ;; make hospice_stats table
  (make-table! (first data-maps))
  ;; make hospice_enrolls table
  (make-table! (second data-maps))
  ;; make hospice_owners table
  (make-table! (last data-maps))
  ;; insert hospice_stats data
  (insert-table! [insert-table-all (first data-maps)])
  ;; insert hospice_enrolls data
  (insert-table! (insert-table-chunks (second data-maps) 100))
  ;; insert hospice_owners data - Dangerous, no primary key so delete rows first
  (insert-table! (insert-table-chunks (last data-maps) 1000)))
