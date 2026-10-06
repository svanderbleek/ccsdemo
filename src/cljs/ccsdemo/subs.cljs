(ns ccsdemo.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  :data
  (fn [db _]
    (:data db)))

(rf/reg-sub
  :route
  (fn [db _]
    (:route db)))

(rf/reg-sub
  :sort
  (fn [db _]
    (:sort db)))
