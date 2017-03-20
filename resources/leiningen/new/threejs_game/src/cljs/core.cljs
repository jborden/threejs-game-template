(ns {{project-ns}}.core
    (:require [reagent.core :as r]
              [cljsjs.three]
              [weasel.repl :as repl]
              [{{project.ns}}.display]))

(defn ^:export init
  "Function to setup and start the game"
  []
  (let [scene (js/THREE.Scene.)
        camera (display/init-camera!
                (display/create-perspective-camera
                 75
                 (/ (.-innerWidth js/window)
                    (.-innerHeight js/window))
                 1
                 1000)
                scene
                [0 0 0])
        renderer (display/create-renderer)
        render (display/render renderer scene camera)
        container (-> js/document
                      (.getElementById "game-container"))
        ])
  )
