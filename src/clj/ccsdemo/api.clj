(ns ccsdemo.api
  (:require [ccsdemo.db :as db]))

(def search
  {nil      {:cols ["state" "payout" "persons" "days"]
             :param "state"
             :query ["SELECT * FROM hospice_stats"]}
   "state"  {:cols ["org_name" "org_type" "provider" "nonprofit"]
             :param "enroll"
             :query ["SELECT * FROM hospice_enrolls WHERE state = ?"]}
   "enroll" {:redirect "pins"
             :query ["INSERT INTO hospice_pins (enroll) VALUES (?) ON CONFLICT DO NOTHING"]}})

(def ^:const pins_join_enrolls
  "SELECT p.enroll, e.org_name FROM hospice_pins p
INNER JOIN hospice_enrolls e ON p.enroll = e.enroll")

(def ^:const owners_join_enrolls
  "SELECT o.title, o.role, o.first, o.middle, o.last, o.owner, e.org_name
FROM hospice_owners o INNER JOIN hospice_enrolls e ON o.enroll = e.enroll
WHERE o.type = 'I' AND o.enroll = ?")

(def pins
  {nil      {:cols ["org_name"]
             :param "enroll"
             :query [pins_join_enrolls]}
   "enroll" {:cols ["title" "role" "first" "middle" "last"]
             :param "owner"
             :query [owners_join_enrolls]}})

(defn param-query [param params query]
  (if param
    (db/query! (conj query (get params param)))
    (db/query! query)))

(defn body-with-rows [api params]
  (let [param (ffirst params)
        body (get api param)]
    (->
      body
      (assoc :rows (param-query param params (:query body)))
      (dissoc :query))))

(defn handler-for [api]
  (fn [req]
    {:body (body-with-rows api (:query-params req))}))
