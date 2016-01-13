(ns chart.core
  (:require [reagent.core :as r]
            [cljsjs.d3]
            [cljs.debux :refer-macros [dbg clog break]] ))

;; (defn simple-component []
;;   [:div
;;    [:p "I am a component!"]
;;    [:p.someclass
;;     "I have " [:strong "bold"]
;;     [:span {:style {:color "red"}} " and red "] "text."]])

;; (defn render-simple []
;;   (r/render simple-component
;;             (.getElementById js/document "app") ))

;; (render-simple)


;; (.. js/d3
;;     (select "#someDiv")
;;     (style "background" "yellow"))

