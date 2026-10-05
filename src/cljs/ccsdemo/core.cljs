(ns ccsdemo.core
  (:require [reagent.dom.client   :as reagent]
            [re-frame.core        :as rf]
            [reitit.frontend.easy :as reitit]
            [reitit.frontend      :refer [router]]))

(defn make-key [obj]
  (hash obj))

(defn make-seq-el [el obj]
  [el {:key (make-key obj)} obj])

(defn search-row [param cols row]
  [:tr.clickable {:key (make-key row)
                  :on-click #(rf/dispatch [::search-load {param (aget row param)}])}
    (map (fn [col] (make-seq-el :td (aget row col))) cols)])

(defn search-table [^js data]
  (let [cols (.-cols data)
        rows (.-rows data)
        param (.-param data)]
    [:table.striped
      [:thead
        [:tr (map (fn [col] (make-seq-el :th col)) cols)]]
      [:tbody
        (map (partial search-row param cols) rows)]]))

(rf/reg-sub
  ::search-data
  (fn [db _]
    (:search-data db)))

(defn search []
  (let [data @(rf/subscribe [::search-data])]
    [:div
      [:p "Search hospices by State."]
      [:button {:on-click #(rf/dispatch [::search-load])} "Start"]
      (if data (search-table data) [:p "Empty"])]))

(rf/reg-fx
  ::fetch
  (fn [req]
    (->
      (js/fetch (:url req))
      (.then (fn [resp] (.json resp)))
      (.then (fn [data] (rf/dispatch [(:on-data req) data]))))))

(rf/reg-event-db
  ::search-data
  (fn [db [_ data]]
    (assoc db :search-data data)))

(defn get-params [params]
  (js/URLSearchParams. (clj->js params)))

(defn get-url [base params]
  (if params
    (str base "?" (get-params params))
    base))

(rf/reg-event-fx
  ::search-load
  (fn [_ [_ params]]
    {::fetch {:url (get-url "/search" params)
              :on-data ::search-data}}))

(def home-text "Welcome to the Hospice Leads Tool.
Use Search to search and pin leads.
Track contacts under Pins.")

(defn home []
  [:p home-text])

(defn pins []
  [:p "Manage pinned leads."])

(def routes
  ["/"
    [""       {:name ::home   :title "Home"   :view home}]
    ["search" {:name ::search :title "Search" :view search}]
    ["pins"   {:name ::pins   :title "Pins"   :view pins}]])

(defn navigate [route]
  (rf/dispatch [::navigate route]))

(rf/reg-event-db
  ::navigate
  (fn [db [_ route]]
    (assoc db :route route)))

(rf/reg-sub
  ::route
  (fn [db _]
    (:route db)))

(defonce root (reagent/create-root (.getElementById js/document "app")))

(defn make-li-a [route]
  [:li {:key (make-key route)}
    [:a {:href (reitit/href (-> route second :name))} (-> route second :title)]])

(defn app []
  (let [route @(rf/subscribe [::route])]
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
