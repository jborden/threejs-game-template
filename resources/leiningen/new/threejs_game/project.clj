(defproject {{ name }} "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.494"]
                 [cljsjs/three "0.0.84-0"]
                 [weasel "0.7.0" :exclusions [org.clojure/clojurescript]]
                 [reagent "0.6.1"]]
  :plugins [[lein-cljsbuild "1.1.5"]]
  :npm {:dependencies [[source-map-support "0.4.14"]]}
  :source-paths ["src" "target/classes"]
  :clean-targets ["out" "release"]
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src"]
                        :compiler {
                                   :main {{name}}.core
                                   :output-to "resources/public/js/{{sanitized}}.js"
                                   :output-dir "resources/public/js/out"
                                   :optimizations :none
                                   :pretty-print true
                                   :source-map true}}
                       {:id "release"
                        :source-paths ["src"]
                        :compiler {
                                   :main {{name}}.core
                                   :output-to "release/{{sanitized}}.min.js"
                                   :optimizations :advanced
                                   :pretty-print false
                                   :externs ["js/{{sanitized}}.externs.js"]}}]}
  :target-path "target")
