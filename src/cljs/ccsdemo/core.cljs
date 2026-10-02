(ns ccsdemo.core
  (:require [reagent.dom.client   :as rdc]
            [re-frame.core        :as rfc]
            [reitit.frontend.easy :as rfe]
            [reitit.frontend      :as rfr]))

(defn home []
  [:p "Welcome to the Hospice Leads Tool"])

(defn search []
  [:p "Search hospices by State"])

(defn pins []
  [:p "Manage pinned leads"])

(def routes
  ["/"
   [""       {:name ::home   :title "Home"   :view home}]
   ["search" {:name ::search :title "Search" :view search}]
   ["pins"   {:name ::pins   :title "Pins"   :view pins}]])

(defn navigate [route]
  (rfc/dispatch [::navigate route]))

(rfc/reg-event-db
  ::init
  (fn [_ _]
    {:route ::home}))

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

(defn make-li-a [route]
  [:li
    [:a {:key (make-key route) :href (rfe/href (-> route second :name))} (-> route second :title)]])

(defn app []
  (let [route @(rfc/subscribe [::route])]
    [:div#root.container-fluid
      [:header
        [:nav
          [:ul [:li [:strong "Hospice Leads Tool"]]]
          [:ul (map make-li-a (rest routes))]]]
      [:main
        [:section
          [:h1 (-> route :data :title)]
          [(-> route :data :view)]]]]))

(defn init []
  (rfc/clear-subscription-cache!)
  (rfc/dispatch-sync [::init])
  (rfe/start! (rfr/router routes) navigate {:use-fragement true})
  (rdc/render root [app]))
