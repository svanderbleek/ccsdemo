(ns ccsdemo.core
  (:require [reagent.dom.client   :as reagent]
            [re-frame.core        :as rf]
            [reitit.frontend.easy :as rfe]
            [reitit.frontend      :refer [router]]))

(defn make-key [obj]
  (hash obj))

(defn make-seq-el [el obj]
  [el {:key (make-key obj)} obj])

(defn data-row [base param cols row]
  [:tr.clickable {:key (make-key row)
                  :on-click #(rf/dispatch [::load base {param (aget row param)}])}
    (map (fn [col] (make-seq-el :td (aget row col))) cols)])

(defn data-table [base ^js data]
  (let [cols (.-cols data)
        rows (.-rows data)
        param (.-param data)]
    [:table.striped
      [:thead
        [:tr (map (fn [col] (make-seq-el :th col)) cols)]]
      [:tbody
        (map (partial data-row base param cols) rows)]]))

(rf/reg-sub
  ::data
  (fn [db _]
    (:data db)))

(defn search []
  (let [data @(rf/subscribe [::data])]
    [:div
      [:p "Search hospices by State."]
      [:button {:on-click #(rf/dispatch [::load "/search"])} "Start"]
      (if data (data-table "/search" data) [:p])]))

(rf/reg-fx
  ::fetch
  (fn [req]
    (->
      (js/fetch (:url req))
      (.then (fn [resp] (.json resp)))
      (.then (fn [data] (rf/dispatch [(:on-data req) data]))))))

(rf/reg-event-fx
  ::data
  (fn [{:keys [db]} [_ data]]
    (let [redirect (.-redirect data)]
      (if redirect
        {:dispatch [::navigate! (keyword "ccsdemo.core" redirect)]
         :db (dissoc db :data)}
        {:db (assoc db :data data)}))))

(defn get-params [params]
  (js/URLSearchParams. (clj->js params)))

(defn get-url [base params]
  (if params
    (str base "?" (get-params params))
    base))

(rf/reg-event-fx
  ::load
  (fn [_ [_ base params]]
    {::fetch {:url (get-url base params)
              :on-data ::data}}))

(def home-text "Welcome to the Hospice Leads Tool.
Use Search to search and pin potential leads.
Explore lead contacts under Pins.")

(defn home []
  [:p home-text])

(defn pins []
  (let [data @(rf/subscribe [::data])]
    [:div
      [:p "Explore pinned leads."]
      [:button {:on-click #(rf/dispatch [::load "/pins"])} "Start"]
      (if data (data-table "/pins" data) [:p])]))

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
    (assoc (dissoc db :data) :route route)))

(rf/reg-event-fx
  ::navigate!
  (fn [_ [_ route params query]]
    (rfe/push-state route params query)
    {}))

(rf/reg-sub
  ::route
  (fn [db _]
    (:route db)))

(defonce root (reagent/create-root (.getElementById js/document "app")))

(defn make-li-a [route]
  [:li {:key (make-key route)}
    [:a {:href (rfe/href (-> route second :name))} (-> route second :title)]])

(defn app []
  (let [route @(rf/subscribe [::route])]
    [:div#root.container-fluid
      [:header
        [:nav
          [:ul [:li [:a.unset {:href "/index.html"} [:strong "Hospice Leads Tool"]]]]
          [:ul (map make-li-a (rest routes))]]]
      [:main
        [:section
          [:h1 (-> route :data :title)]
          [(-> route :data :view)]]]]))

(defn init []
  (rfe/start! (router routes) navigate {:use-fragement true})
  (reagent/render root [app]))
