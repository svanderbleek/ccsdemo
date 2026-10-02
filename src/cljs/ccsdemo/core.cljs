(ns ccsdemo.core
  (:require [reagent.dom.client   :as rdc]
            [re-frame.core        :as rfc]
            [reitit.frontend.easy :as rfe]
            [reitit.frontend      :as rfr]))

(def routes
  ["/"
   [""       {:name ::home   :title "Home"}]
   ["search" {:name ::search :title "Search"}]
   ["pins"   {:name ::pins   :title "Pins"}]])

(defn navigate [route]
  (when route (rfc/dispatch [::navigate route])))

(defn init-routes! []
  (rfe/start! (rfr/router routes) navigate {:use-fragement true}))

(rfc/reg-event-db
  ::init
  (fn [db _]
    (if db db {:route ::home})))

(rfc/reg-event-db
  ::navigate
  (fn [db [_ route]]
    (assoc db :route route)))

(rfc/reg-sub
 ::route
 (fn [db _]
   (:route db)))

(defonce root (rdc/create-root (.getElementById js/document "app")))

(defn make-key [obj]
  (hash obj))

(defn make-a [route]
  [:a {:key (make-key route) :href (rfe/href (-> route second :name))} (-> route second :title)])

(defn app []
  (let [route @(rfc/subscribe [::route])]
    [:div
      [:nav (map make-a (rest routes))]
      [:h1 (-> route :data :title)]]))

(defn init []
  (rfc/clear-subscription-cache!)
  (rfc/dispatch-sync [::init])
  (init-routes!)
  (rdc/render root [app]))
