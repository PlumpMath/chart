(ns chart.simple-bar
  (:require [reagent.core :as r]
            [cljsjs.d3]
            [cljs.debux :refer-macros [dbg clog break]] ))


(def data* (r/atom [10 20 30 40 50 60 70 80 90 100]))

(defn render-bar [data]
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
  
(defn simple-bar []
  (r/create-class
    {:reagent-render
     (fn [this]
       [:div "Simple bar" (render-bar @data*)])}))


(r/render [simple-bar] (.getElementById js/document "bar"))

(defn shift [data]
  (conj (vec (drop 1 data)) 110))

(swap! data* shift)


