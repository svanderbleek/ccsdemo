(ns ccsdemo.core
  (:require [reagent.dom.client   :as reagent]
            [re-frame.core        :as reframe]
            [reitit.frontend.easy :as reitit]
            [reitit.frontend      :refer [router]]))

(def home-text "Welcome to the Hospice Leads Tool.
Use Search to search and pin leads.
Track contacts under Pins.")

(defn home []
  [:p home-text])

(defn search []
  [:p "Search hospices by State."])

(defn pins []
  [:p "Manage pinned leads."])

(def routes
  ["/"
    [""       {:name ::home   :title "Home"   :view home}]
    ["search" {:name ::search :title "Search" :view search}]
    ["pins"   {:name ::pins   :title "Pins"   :view pins}]])

(defn navigate [route]
  (reframe/dispatch [::navigate route]))

(reframe/reg-event-db
  ::navigate
  (fn [db [_ route]]
    (assoc db :route route)))

(reframe/reg-sub
  ::route
  (fn [db _]
    (:route db)))

(defonce root (reagent/create-root (.getElementById js/document "app")))

(defn make-key [obj]
  (hash obj))

(defn make-li-a [route]
  [:li {:key (make-key route)}
    [:a {:href (reitit/href (-> route second :name))} (-> route second :title)]])

(defn app []
  (let [route @(reframe/subscribe [::route])]
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
  (reitit/start! (router routes) navigate {:use-fragement true})
  (reagent/render root [app]))
