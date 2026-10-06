(ns ccsdemo.views
  (:require [re-frame.core        :as rf]
            [reitit.frontend.easy :as rfe]))

(defn react-key [obj]
  {:key (hash obj)})

(defn react-seq [el obj]
  [el (react-key obj) obj])

(defn click-row [base param row]
  {:on-click (fn [] (rf/dispatch [:load base {param (aget row param)}]))})

(defn data-row [base param cols row]
  [:tr.clickable
    (merge (react-key row) (click-row base param row))
    (map (fn [col] (react-seq :td (aget row col))) cols)])

(defn data-table [base ^js data]
  (let [cols (.-cols data)
        rows (.-rows data)
        param (.-param data)]
    [:table.striped
      [:thead
        [:tr (map (fn [col] (react-seq :th col)) cols)]]
      [:tbody
        (map (partial data-row base param cols) rows)]]))

(defn api-view [desc base]
  (let [data @(rf/subscribe [:data])]
    [:div
      [:p desc]
      [:button {:on-click #(rf/dispatch [:load base])} "Start"]
      (if data (data-table base data) [:p])]))

(def ^:const home-text "Welcome to the Hospice Leads Tool.
Use Search to search and pin potential leads.
Explore lead contacts under Pins.")

(defn home []
  [:p home-text])

(defn search []
  (api-view "Search hospices by State." "/search"))

(defn pins []
  (api-view "Explore pinned leads." "/pins"))

(defn link-route [route]
  [:li (react-key route)
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
          [:ul (map link-route (rest routes))]]]
      [:main
        [:section
          [:h1 (-> route :data :title)]
          [(-> route :data :view)]]]]))
