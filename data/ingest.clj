(ns ingest
  (:require [tablecloth.api :as tc]))

(def data-file-fields
  [{:file "Hospice_CPS_2024.csv"
    :fields ["State",
             "Total Program Payments",
             "Total Persons With Utilization",
             "Total Covered Days of Care"]},
   {:file "Hospice_Enrollments_2026.07.17.csv"
    :fields ["ENROLLMENT ID",
             "ENROLLMENT STATE",
             "PROVIDER TYPE TEXT",
             "PROPRIETARY_NONPROFIT",
             "ORGANIZATION NAME",
             "ORGANIZATION TYPE STRUCTURE"]},
   {:file "Hospice_All_Owners_2026.07.17.csv"
    :fields ["ENROLLMENT ID",
             "TYPE - OWNER",
             "ROLE TEXT - OWNER",
             "FIRST NAME - OWNER",
             "MIDDLE NAME - OWNER",
             "LAST NAME - OWNER",
             "TITLE - OWNER"]}])

(defn data-file-path [file]
  (str "data/files/" file))

(defn load-dataset [data-map]
  (->
    (tc/dataset (-> data-map :file data-file-path))
    (tc/select-columns (:fields data-map))))
