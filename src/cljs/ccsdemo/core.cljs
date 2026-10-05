(ns ccsdemo.core
  (:require [reagent.dom.client   :as rd]
            [re-frame.core        :as rf]
            [reitit.frontend.easy :as rfe]
            [reitit.frontend      :refer [router]]
            [ccsdemo.events]
            [ccsdemo.subs]
            [ccsdemo.views        :as viw]))

(defn navigate [route]
  (rf/dispatch [:navigate route]))

(defonce root (rd/create-root (.getElementById js/document "app")))

(defn init []
  (rfe/start! (router viw/routes) navigate {:use-fragement true})
  (rd/render root [viw/app]))
