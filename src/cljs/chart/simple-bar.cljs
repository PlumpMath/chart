(ns chart.simple-bar
  (:require [reagent.core :as r]
            [cljsjs.d3]
            [cljs.debux :refer-macros [dbg clog break]] ))

(def data* (r/atom [10 15 30 50 80 65 55 30 20 10 8]))

(defn init-bar [this]
  (let [data (.. js/d3
                 (select "body")
                 (selectAll "div.h-bar")
                 (data (clj->js @data*)))]
    ;; enter
    (.. data
        enter
        (append "div")
        (attr "class" "h-bar")
        (append "span"))

    ;; update
    (.. data
        (style "width" #(str (* 3 %) "px"))
        (select "span")
        (text #(identity %)))
                
     ;; exit
     (.. data
         exit
         remove) ))
  
(defn simple-bar []
  (r/create-class
    {#_:component-did-update
     #_(fn [this old-argv]
       (let [[_ args ...] (r/argv this)]
         ;; This is where we get to actually draw the D3 gauge.
         (draw-d3-gauge @dom-node args ...)))

     :component-did-mount
     init-bar

     :reagent-render
     (fn [this]
       ;; Necessary for Reagent to see that we depend on the dom-node r/atom.
       ;; Note: we don't actually use any of the args here.  This is because
       ;; we cannot render D3 at this point.  We have to wait for the update.
       @data*
       [:div "Simple bar"])}))


(r/render [simple-bar] (.getElementById js/document "bar"))

(clog "hello")
(reset! data* [10 20 30 40 50 60 70 80 90 100 110])