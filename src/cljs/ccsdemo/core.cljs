(ns ccsdemo.core
  (:require [reagent.dom.client   :as rd]
            [re-frame.core        :as rf]
            [reitit.frontend.easy :as rfe]
            [reitit.frontend      :refer [router]]
            [ccsdemo.events]
            [ccsdemo.subs]
            [ccsdemo.views        :as vw]))

(defonce root (rd/create-root (.getElementById js/document "app")))

(defn navigate [route]
  (rf/dispatch [:navigate route]))

(defn init []
  (rfe/start! (router vw/routes) navigate {:use-fragement true})
  (rd/render root [vw/app]))
