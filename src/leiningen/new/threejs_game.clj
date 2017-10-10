(ns leiningen.new.threejs-game
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files sanitize-ns]]
            [leiningen.core.main :as main]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def render (renderer "threejs-game"))

(defn binary [file]
  (io/input-stream (io/resource (str/join "/" ["leiningen" "new" "threejs_game" file]))))

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
             ["src/cljs/{{sanitized}}/components.cljs" (render "src/cljs/components.cljs" data)]
             ["src/cljs/{{sanitized}}/display.cljs" (render "src/cljs/display.cljs" data)]
             ["src/cljs/{{sanitized}}/dev.cljs" (render "src/cljs/dev.cljs" data)]
             ["src/cljs/{{sanitized}}/menu.cljs" (render "src/cljs/menu.cljs" data)]
             ["src/cljs/{{sanitized}}/time_loop.cljs" (render "src/cljs/time_loop.cljs" data)]
             ["src/cljs/{{sanitized}}/utilities.cljs" (render "src/cljs/utilities.cljs" data)]
             ["src/cljs/{{sanitized}}/xhr.cljs" (render "src/cljs/xhr.cljs" data)]
             ["src/cljs/{{sanitized}}/cookies.cljs" (render "src/cljs/cookies.cljs" data)]
             ["src/cljs/{{sanitized}}/sounds.cljs" (render "src/cljs/sounds.cljs" data)]
             ["src/cljs/{{sanitized}}/textures.cljs" (render "src/cljs/textures.cljs" data)]
             ["src/cljs/{{sanitized}}/fonts.cljs" (render "src/cljs/fonts.cljs" data)]
             ["src/cljs/{{sanitized}}/objects.cljs" (render "src/cljs/objects.cljs" data)]

             ["src/js/{{sanitized}}.externs.js" (render "src/js/externs.js" data)]
             ["resources/public/index.html" (render "resources/public/index.html" data)]
             ["resources/public/index_release.html" (render "resources/public/index_release.html" data)]

             ["resources/public/css/{{sanitized}}.css" (render "resources/public/css/main.css" data)]
             ["resources/public/fonts/helvetiker_regular.typeface.json" (render "resources/public/fonts/helvetiker_regular.typeface.json" data)]
             ["resources/public/server.js" (render "resources/public/server.js" data)]

             ["resources/public/audio/Hit_Hurt19.wav" (binary "resources/public/audio/Hit_Hurt19.wav")]
             ["resources/public/audio/Powerup9.wav" (binary "resources/public/audio/Powerup9.wav")]
             ["resources/public/images/enemy.png" (binary "resources/public/images/enemy.png")]
             )))
