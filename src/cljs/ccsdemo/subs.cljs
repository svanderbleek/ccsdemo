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
