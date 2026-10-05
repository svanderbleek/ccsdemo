(ns ccsdemo.views
  (:require [re-frame.core        :as rf]
            [reitit.frontend.easy :as rfe]))

(defn make-key [obj]
  (hash obj))

(defn make-seq-el [el obj]
  [el {:key (make-key obj)} obj])

(defn data-row [base param cols row]
  [:tr.clickable {:key (make-key row)
                  :on-click #(rf/dispatch [:load base {param (aget row param)}])}
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

(def home-text "Welcome to the Hospice Leads Tool.
Use Search to search and pin potential leads.
Explore lead contacts under Pins.")

(defn home []
  [:p home-text])

(defn search []
  (let [data @(rf/subscribe [:data])]
    [:div
      [:p "Search hospices by State."]
      [:button {:on-click #(rf/dispatch [:load "/search"])} "Start"]
      (if data (data-table "/search" data) [:p])]))

(defn pins []
  (let [data @(rf/subscribe [:data])]
    [:div
      [:p "Explore pinned leads."]
      [:button {:on-click #(rf/dispatch [:load "/pins"])} "Start"]
      (if data (data-table "/pins" data) [:p])]))

(defn make-li-a [route]
  [:li {:key (make-key route)}
    [:a {:href (rfe/href (-> route second :name))} (-> route second :title)]])

(def routes
  ["/"
    [""       {:name :home   :title "Home"   :view home}]
    ["search" {:name :search :title "Search" :view search}]
    ["pins"   {:name :pins   :title "Pins"   :view pins}]])

(defn app []
  (let [route @(rf/subscribe [:route])]
    [:div#root.container-fluid
      [:header
        [:nav
          [:ul [:li [:a.unset {:href "/index.html"} [:strong "Hospice Leads Tool"]]]]
          [:ul (map make-li-a (rest routes))]]]
      [:main
        [:section
          [:h1 (-> route :data :title)]
          [(-> route :data :view)]]]]))
