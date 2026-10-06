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

(defn click-sort [col]
  {:on-click (fn [] (rf/dispatch [:sort col]))})

(defn sort-dir [{sort-col :col asc? :asc?} col]
  (when (= col sort-col)
    (if asc? "ascending" "descending")))

(def sort-arrows {"ascending" " ▲" "descending" " ▼"})

(defn sort-header [sorting col]
  (let [dir (sort-dir sorting col)]
    [:th.sortable
      (merge (react-key col) (click-sort col) {:aria-sort dir})
      col (sort-arrows dir)]))

(defn sort-rows [{:keys [col asc?]} rows]
  (if col
    (sort-by #(aget % col) (if asc? compare #(compare %2 %1)) rows)
    rows))

(defn data-table [base ^js data sortable?]
  (let [cols (.-cols data)
        sorting (when sortable? @(rf/subscribe [:sort]))
        rows (sort-rows sorting (.-rows data))
        param (.-param data)]
    [:table.striped
      [:thead
        [:tr (map (if sortable? (partial sort-header sorting) (partial react-seq :th)) cols)]]
      [:tbody
        (map (partial data-row base param cols) rows)]]))

(defn api-view [desc base & {:keys [sort-first?]}]
  (let [data @(rf/subscribe [:data])
        sortable? (and sort-first? @(rf/subscribe [:first-table?]))]
    [:div
      [:p desc]
      [:button {:on-click #(rf/dispatch [:load base])} "Start"]
      (if data (data-table base data sortable?) [:p])]))

(def ^:const home-text "Welcome to the Hospice Leads Tool.
Use Search to search and pin potential leads.
Explore lead contacts under Pins.")

(defn home []
  [:p home-text])

(defn search []
  (api-view "Search hospices by State." "/search" :sort-first? true))

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
