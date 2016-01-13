(ns chart.line-chart
  (:require [reagent.core :as r]
            [cljsjs.d3]
            [cljs.debux :refer-macros [dbg clog break]] ))

(defn init-context
  [width height & {:keys [margins color-scale]}]
  (r/atom {:width  width
           :height height
           :margins (or margins {:top 30 :left 30 :right 30 :bottom 30})
           :color-scale (or color-scale (.. js/d3.scale category10))
           :x-scale nil
           :y-scale nil
           :data nil
           :svg nil
           :body-g nil
           :line-gen nil} ))


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
  
(defn line-chart [ctx]
  (r/create-class
    {:component-did-mount
     (fn [this]
       (clog this :js)
       (.. js/d3
           (select (r/dom-node this))
           (append "svg")
           (attr "width" (:width @ctx))
           (attr "height" (:height @ctx)) ))

     :reagent-render
     (fn [this]
       [:div "Line Chart"] )}))


(def chart* (init-context 600 300))

(r/render [line-chart chart*] (clog (.getElementById js/document "chart")))




