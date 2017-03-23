(ns leiningen.new.threejs-game
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files sanitize-ns]]
            [leiningen.core.main :as main]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def render (renderer "threejs-game"))

;; From: https://github.com/http-kit/lein-template/blob/master/src/leiningen/new/http_kit.clj

(defn threejs-game
  [name]
  (let [data {:name name
              :sanitized (name-to-path name)
              :project-ns (sanitize-ns name)}]
    (main/info "Generating fresh 'lein new' threejs-game project.")
    (->files data
             ["project.clj" (render "project.clj" data)]
             [".gitignore" (render ".gitignore" data)]
             ["README.md" (render "README.md" data)]

             ["src/cljs/{{sanitized}}/core.cljs" (render "src/cljs/core.cljs" data)]
             ["src/cljs/{{sanitized}}/controls.cljs" (render "src/cljs/controls.cljs" data)]
             ["src/cljs/{{sanitized}}/display.cljs" (render "src/cljs/display.cljs" data)]
             ["src/cljs/{{sanitized}}/dev.cljs" (render "src/cljs/dev.cljs" data)]
             ["src/cljs/{{sanitized}}/game_loop.cljs" (render "src/cljs/game_loop.cljs" data)]

             ["index.html" (render "index.html" data)]
             ["server.js" (render "server.js" data)]

             )))
