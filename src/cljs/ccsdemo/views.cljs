(ns ccsdemo.views
  (:require [re-frame.core        :as rf]
            [reitit.frontend.easy :as rfe]
            [clojure.string       :as str]))

(defn react-key [obj]
  {:key (hash obj)})

(defn react-seq [el obj]
  [el (react-key obj) obj])

(defn react-seq-cnt [el obj cnt]
  [el (react-key obj) cnt])

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

(defn owner-query [row]
  (let [owner (js->clj row {:keywordize-keys true})]
    (str/join " " ((juxt :org_name :title :first :middle :last) owner))))

(defn google-contact [row]
  (let [query (js/encodeURIComponent (str "contact for " (owner-query row)))]
    (js/window.open (str "https://google.com?q=" query) "_blank")))

(defn click-row [base param google? row]
  (if google?
    {:on-click (fn [] (google-contact row))}
    {:on-click (fn [] (rf/dispatch [:load base {param (aget row param)}]))}))

(defn data-row [base param cols google? row]
  [:tr.clickable
    (merge (react-key row) (click-row base param google? row))
    (map (fn [col] (react-seq-cnt :td (str col row) (aget row col))) cols)])

(defn data-table [base ^js data]
  (let [cols     (.-cols data)
        param    (.-param data)
        sort?    (= param "state")
        google?  (= param "owner")
        sorting  (when sort? @(rf/subscribe [:sort]))
        rows     (sort-rows sorting (.-rows data))]
    [:table.striped
      [:thead
        [:tr (map (if sort? (partial sort-header sorting) (partial react-seq :th)) cols)]]
      [:tbody
        (map (partial data-row base param cols google?) rows)]]))

(defn api-view [desc base]
  (let [data @(rf/subscribe [:data])]
    [:div
      [:p desc]
      [:button {:on-click #(rf/dispatch [:load base])} "Start"]
      (if data (data-table base data) [:p])]))

(def ^:const home-text "Welcome to the Hospice Leads Tool.
Use Search to search and pin potential leads.
Find contacts for pinned leads using Pins.
Manage the contact pipeline under Contacts.")

(defn home []
  [:p home-text])

(defn search []
  (api-view "Search hospices by State." "/search"))

(defn pins []
  (api-view "Explore pinned leads." "/pins"))

(defn contacts [] [:p "Coming soon!"])

(defn link-route [route]
  [:li (react-key route)
    [:a {:href (rfe/href (-> route second :name))} (-> route second :title)]])

(def routes
  ["/"
    [""         {:name :home     :title "Home"     :view home}]
    ["search"   {:name :search   :title "Search"   :view search}]
    ["pins"     {:name :pins     :title "Pins"     :view pins}]
    ["contacts" {:name :contacts :title "Contacts" :view contacts}]])

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
