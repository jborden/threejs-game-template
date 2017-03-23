(defproject {{ name }} "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.494"]
                 [cljsjs/three "0.0.84-0"]
                 [reagent "0.6.1"]]
  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-figwheel "0.5.9"]]
  :npm {:dependencies [[source-map-support "0.4.14"]]}
  :source-paths ["src" "target/classes"]
  :clean-targets ^{:protect false} ["release" "resources/public/js"]
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src"]
                        :figwheel {:on-jsload "{{project-ns}}.dev/on-jsload"}
                        :compiler {:main {{name}}.core
                                   :output-to "resources/public/js/{{sanitized}}.js"
                                   :output-dir "resources/public/js/out"
                                   :asset-path "js/out"
                                   :optimizations :none
                                   :pretty-print true
                                   :source-map true}}
                       {:id "release"
                        :source-paths ["src"]
                        :compiler {:output-to "resources/public/js/{{sanitized}}.min.js"
                                   :optimizations :advanced
                                   :pretty-print false
                                   :externs ["src/js/{{sanitized}}.externs.js"]}}]}
  :target-path "target")
