(defproject {{ name }} "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.9.0-beta2"]
                 [org.clojure/clojurescript "1.9.946"]
                 [cljsjs/three "0.0.87-0"]
                 [cljsjs/howler "2.0.5-0"]
                 [cljsjs/stats "16.0-0"]
                 [reagent "0.8.0-alpha1"]]
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.14"]]
  :npm {:dependencies [[source-map-support "0.5.0"]]}
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
