(ns data.ingest
  (:require [tablecloth.api :as tc]))

(def hospice-enrollments
  (->
    (tc/dataset "data/Hospice_Enrollments_2026.07.17.csv")
    (tc/select-columns ["ENROLLMENT ID", "ENROLLMENT STATE"])))
