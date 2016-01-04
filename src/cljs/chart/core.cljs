(ns chart.core
  (:require [cljsjs.d3]))

(.. js/d3
    (select "#someDiv")
    (style "background" "yellow"))


