(ns ccsdemo.core
  (:require [reagent.dom.client :as rdc]
            [re-frame.core :as rf]))

(rf/reg-event-db
  :init
  (fn [_ _]
    {:display "initializing..."}))

(rf/reg-fx
  :fetch
  (fn [req]
    (-> (js/fetch (:url req))
        (.then (fn [resp] (.text resp)))
        (.then (fn [body] (rf/dispatch [:later :loaded body]))))))

(rf/reg-event-fx
  :load
  (fn [{:keys [db]} _]
    {:fetch {:url "/api"}
     :db (assoc db :display "loading...")}))

(rf/reg-event-db
  :loaded
  (fn [db [_ body]]
    (assoc db :display body)))

(rf/reg-sub
  :display
  (fn [db _]
    (:display db)))

(rf/reg-event-fx
 :later
 (fn [_ [_ event & args]]
   {:dispatch-later {:ms 1000 :dispatch (into [event] args)}}))

(defonce root (rdc/create-root (.getElementById js/document "app")))

(defn app []
  (let [display (rf/subscribe [:display])]
    [:h1 @display]))

(defn init []
  (rf/dispatch-sync [:init])
  (rf/dispatch [:later :load])
  (rdc/render root [app]))
