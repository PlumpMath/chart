(ns chart.line-chart
  (:require [reagent.core :as r]
            [cljsjs.d3]
            [domina :as dom]
            [domina.events :as evt]
            [cljs.debux :refer-macros [dbg clog break]] ))

(defn- scale
  "<domain' [min max]>
   <return d3.scale.linear>" 
  [domain']
  (.. js/d3.scale linear (domain (clj->js domain'))))
  
(defn render-chart [data]
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


;;; helper functions

(defn- x-start
  [ctx]
  (get-in @ctx [:margins :left]))

(defn- y-start
  [ctx]
  (- (:height @ctx)
     (get-in @ctx [:margins :bottom]) ))

(defn- x-end
  [ctx]
  (- (:width @ctx)
     (get-in @ctx [:margins :right]) ))

(defn- y-end
  [ctx]
  (get-in @ctx [:margins :top]))

(defn- body-width
  [ctx]
  (- (:width @ctx)
     (get-in @ctx [:margins :left])
     (get-in @ctx [:margins :right])))

(defn- body-height
  [ctx]
  (- (:height @ctx)
     (get-in @ctx [:margins :top])
     (get-in @ctx [:margins :bottom]) ))


;;; x/y 축 그리기

(defn- render-x-axis
  [ctx $axes]
  (let [x-axis (.. js/d3.svg 
                   axis
                   (scale (.. (:x-scale @ctx)
                              (range (clj->js [0 (body-width ctx)])) ))
                   (orient "bottom") )]
    (.. $axes
        (append "g")
        (attr "class" "x axis")
        (attr "transform" #(str "translate(" (x-start ctx) "," (y-start ctx) ")"))
        (call x-axis))

    (.. js/d3 (selectAll "g.x g.tick")
              (append "line")
              (classed "grid-line" true)
              (attr "x1" 0)
              (attr "y1" 0)
              (attr "x2" 0)
              (attr "y2" (- (body-height ctx))) )))

(defn- render-y-axis
  [ctx $axes]
  (let [y-axis (.. js/d3.svg 
                   axis
                   (scale (.. (:y-scale @ctx)
                              (range (clj->js [(body-height ctx) 0])) ))
                   (orient "left") )]
    (.. $axes
        (append "g")
        (attr "class" "y axis")
        (attr "transform" #(str "translate(" (x-start ctx) "," (y-end ctx) ")"))
        (call y-axis))

    (.. js/d3 (selectAll "g.y g.tick")
              (append "line")
              (classed "grid-line" true)
              (attr "x1" 0)
              (attr "y1" 0)
              (attr "x2" (body-width ctx))
              (attr "y2" 0 ) )))

(defn- render-axes
  [ctx $svg]
  (let [$axes (.. $svg
                  (append "g")
                  (attr "class" "axes"))]
  (render-x-axis ctx $axes)
  (render-y-axis ctx $axes) ))


;;; chart-body 영역 지정

(defn define-body-clip
  [ctx $svg]
  (let [padding 5]
    (.. $svg
        (append "defs")
        (append "clipPath")
        (attr "id" "body-clip")
        (append "rect")
        (attr "x" (- padding))
        (attr "y" 0)
        (attr "width" (+ (body-width ctx)
                         (* 2 padding) ))
        (attr "height" (body-height ctx)) )))

(defn init-body
  [ctx $svg]
  (define-body-clip ctx $svg)
  (let [$body (.. $svg
                  (append "g")
                  (attr "class" "body")
                  (attr "transform"
                        (str "translate("  (x-start ctx) "," (y-end ctx) ")"))
                  (attr "clip-path" "url(#body-clip)"))]
    (swap! ctx assoc :body $body) ))

(defn init-chart
  "<this reagent-component-this>
   <ctx line-chart-context>"
  [this ctx]
  (let [$svg (.. js/d3
                 (select this)   ; dom this
                 (append "svg")
                 (attr "width" (:width @ctx))
                 (attr "height" (:height @ctx)) )]
    (render-axes ctx $svg)
    (init-body ctx $svg) ))

(defn render-lines
  [ctx]
  (clog @ctx "render-lines")
  (let [line-gen (.. js/d3.svg
                     line
                     (x (fn [d] ((:x-scale @ctx) (aget d "x"))))
                     (y (fn [d] ((:y-scale @ctx) (aget d "y")))))
        lines (.. (:body @ctx)
                  (selectAll "path.line")
                  (data (clj->js (:data @ctx))))]
    (.. lines
        (enter)
        (append "path")                
        (style "stroke" (fn [d i] ((:color-scale @ctx) i)))
        (attr "class" "line"))
    (.. lines
       (transition)
       (attr "d" (fn [d] (line-gen d))) )))

(defn render-dots
  [ctx]
  (let [data (:data @ctx)
        body (:body @ctx)]
    (doseq [[idx line] (partition 2 (interleave (range (count data)) data))]
      (clog [idx line])
      (let [dots (.. body
                     (selectAll (str "circle._" idx))
                     (data (clj->js line)))]
        (.. dots 
            enter
            (append "circle")
            (attr "class" (str "dot _" idx)))
        (.. dots 
            (style "stroke" (fn [d] (:color-scale @ctx)))
            transition
            (attr "cx" (fn [d] ((:x-scale @ctx) (aget d "x"))))
            (attr "cy"  (fn [d] ((:y-scale @ctx) (aget d "y"))))
            (attr "r" 4.5) )))))

(defn render-body
  [ctx]
  (render-lines ctx)
  (render-dots ctx))

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
           :data data
           :body nil} ))

(defn line-chart
  [ctx]
  (r/create-class
    {:component-did-mount
     (fn [this]   ; reagent this
       (init-chart (r/dom-node this) ctx))

     :reagent-render
     (fn [this]
       (if (clog (:body @ctx))
         [:div [:div "Line Chart"]
               [:div (render-body ctx)]]
         [:div] ))}))

(defn set-data
  [ctx data]
  (swap! ctx assoc :data data))

(defn make-data
  []
  (for [line (range 2)]
    (for [i (range 11)]
      {:x i :y (rand-int 10)} )))

(def chart* (init-context 600 300 [0 10] [0 10]))

(r/render [line-chart chart*] (.getElementById js/document "chart"))

(set-data chart* (make-data))

(evt/listen! (dom/by-id "btn") :click (fn [evt] (set-data chart* (make-data))))
