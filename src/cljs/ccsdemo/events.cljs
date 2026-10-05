(ns ccsdemo.events
  (:require [re-frame.core :as rf]
            [reitit.frontend.easy :as rfe]))

(rf/reg-event-db
  :navigate
  (fn [db [_ route]]
    (assoc (dissoc db :data) :route route)))

(rf/reg-event-fx
  :navigate!
  (fn [_ [_ route params query]]
    (rfe/push-state route params query)
    {}))

(rf/reg-fx
  :fetch
  (fn [req]
    (->
      (js/fetch (:url req))
      (.then (fn [resp] (.json resp)))
      (.then (fn [data] (rf/dispatch [(:on-data req) data]))))))

(rf/reg-event-fx
  :data
  (fn [{:keys [db]} [_ data]]
    (let [redirect (.-redirect data)]
      (if redirect
        {:dispatch [:navigate! (keyword redirect)]
         :db (dissoc db :data)}
        {:db (assoc db :data data)}))))

(defn get-params [params]
  (js/URLSearchParams. (clj->js params)))

(defn get-url [base params]
  (if params
    (str base "?" (get-params params))
    base))

(rf/reg-event-fx
  :load
  (fn [_ [_ base params]]
    {:fetch {:url (get-url base params)
             :on-data :data}}))
