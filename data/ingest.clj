(ns ingest
  (:require [tablecloth.api :as tc]
            [clojure.string :as cs]))

(def data-maps
  [{:file "Hospice_CPS_2024.csv"
    :table "hospice_stats"
    :fields ["State",
             "Total Program Payments",
             "Total Persons With Utilization",
             "Total Covered Days of Care"]},
   {:file "Hospice_Enrollments_2026.07.17.csv"
    :table "hospice_enrolls"
    :fields ["ENROLLMENT ID",
             "ENROLLMENT STATE",
             "PROVIDER TYPE TEXT",
             "PROPRIETARY_NONPROFIT",
             "ORGANIZATION NAME",
             "ORGANIZATION TYPE STRUCTURE"]},
   {:file "Hospice_All_Owners_2026.07.17.csv"
    :table "hospice_owners"
    :fields ["ENROLLMENT ID",
             "TYPE - OWNER",
             "ROLE TEXT - OWNER",
             "FIRST NAME - OWNER",
             "MIDDLE NAME - OWNER",
             "LAST NAME - OWNER",
             "TITLE - OWNER"]}])

(defn file-path [file]
  (str "data/files/" file))

(defn table-column [field]
  (->
    field
    (cs/replace #"[- ]" "_")
    (cs/replace #"(_)+" "_")
    cs/lower-case))

(defn load-data [data-map]
  (->
    data-map
    (tc/dataset (-> data-map :file file-path))
    (tc/select-columns (:fields data-map))))
