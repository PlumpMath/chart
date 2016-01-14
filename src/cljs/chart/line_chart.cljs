(ns chart.line-chart
  (:require [reagent.core :as r]
            [cljsjs.d3]
            [cljs.debux :refer-macros [dbg clog break]] ))

(defn- scale
  [domain']
  (.. js/d3.scale linear (domain (clj->js domain'))))
  
(defn render-chart [data]
  (clog [data])
  (let [data' (.. js/d3
                  (select "#bar")
                  (selectAll "div.h-bar")
                  (data (clj->js data)))]
    ;; enter
    (.. data'
        enter
        (append "div")
        (attr "class" "h-bar")
        (append "span"))

    ;; update
    (.. data'
        (style "width" #(str (* 3 %) "px"))
        (select "span")
        (text #(identity %)))
                
     ;; exit
     (.. data'
         exit
         remove) ))

(defn init-chart
  [this ctx]
  (let [$svg (.. js/d3
                 (select this)   ; dom this
                 (append "svg"))]
    (.. $svg
        (attr "width" (:width @ctx))
        (attr "height" (:height @ctx)) )))

(defn init-context
  "<width int>  svg 너비  
   <height int> svg 높이
   <x-domain [<min-x int> <max-x int]> x축 데이터 범위
   <y-domain [<min-y int> <max-y int]> y축 데이터 범위
   <margins {:top int :left int :right int :bottom int}>
   <color-scale d3-color-scale>
   <data []>"
  [width height x-domain y-domain & {:keys [margins color-scale data]}]
  (r/atom {:width  width
           :height height
           :margins (or margins {:top 30 :left 30 :right 30 :bottom 30})
           :x-scale (scale x-domain)
           :y-scale (scale y-domain)
           :color-scale (or color-scale (.. js/d3.scale category10))
           :data data} ))

(defn line-chart [ctx]
  (r/create-class
    {:component-did-mount
     (fn [this]   ; reagent this
       (init-chart (r/dom-node this) ctx))

     :reagent-render
     (fn [this]
       [:div [:div "Line Chart"]] )}))


(def chart* (init-context 600 300 [0 10] [0 10]))

(r/render [line-chart chart*] (.getElementById js/document "chart"))




