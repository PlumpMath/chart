(set-env!
  :source-paths #{"src/cljs"}
  :resource-paths #{"html"}

  :dependencies '[[org.clojure/clojure "1.7.0"]
                  [org.clojure/clojurescript "1.7.228"]
                  [cljsjs/d3 "3.5.7-1"]
                  [reagent "0.5.1"]
                  [datascript "0.13.3"]
                  [posh "0.3.3.1"]
                  [philoskim/debux "0.1.0"]

                  [adzerk/boot-cljs "1.7.170-3" :scope "test"]       ; CLJS compiler
                  [pandeiro/boot-http "0.7.0" :scope "test"]         ; web server
                  [adzerk/boot-reload "0.4.2" :scope "test"]         ; live reload
                  [adzerk/boot-cljs-repl "0.3.0" :scope "test"]      ; add bREPL
                  [com.cemerick/piggieback "0.2.1" :scope "test"]    ; needed by bREPL 
                  [weasel "0.7.0" :scope "test"]                     ; needed by bREPL
                  [org.clojure/tools.nrepl "0.2.12" :scope "test"]   ; needed by bREPL
                  [adzerk/boot-test "1.0.7" :scope "test"]           ; clj test
                  [crisptrutski/boot-cljs-test "0.2.1" :scope "test"]   ; cljs test
                 ])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[pandeiro.boot-http :refer [serve]]
         '[adzerk.boot-reload :refer [reload]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
         '[adzerk.boot-test :refer [test]]
         '[crisptrutski.boot-cljs-test :refer [test-cljs]])

;;; add dev task
(deftask dev 
  "Launch immediate feedback dev environment"
  []
  (comp
    #_(serve :dir "target"                                
           :handler 'chart.core/app   ; ring hanlder
           :resource-root "target"    ; root classpath
           :reload true)  
    (watch)
    (reload)
    ;(cljs-repl)
    (cljs :compiler-options {:main "chart.line-chart"}) ))

