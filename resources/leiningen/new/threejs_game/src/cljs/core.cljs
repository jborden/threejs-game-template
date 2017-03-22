(ns {{project-ns}}.core
    (:require [reagent.core :as r]
              [cljsjs.three]
              [weasel.repl :as repl]
              [{{project.ns}}.display :as display]
              [{{project.ns}}.game-loop :as game-loop]))

(defn hero
  []
  (let [material (js/THREE.MeshBasicMaterial. (clj->js {:color 0xFF0000}))
        geometry (js/THREE.PlaneGeometry. 2 1)
        mesh (js/THREE.Mesh. geometry material)
        move-increment 5]
    (reify
      Object
      (moveLeft [this]
        (.translateX mesh move-increment))
      (moveRight [this]
        (.translateX mesh (- move-increment)))
      (moveUp [this]
        (.translateY mesh move-increment))
      (moveDown [this]
        (.translateY mesh (- move-increment)))
      (getMesh [this] mesh))))

;; likely need to take this out below
;; (defn main-loop-fn
;;   [delta-t]
;; ()
;;   )

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
        hero (hero)]

    (display/attach-renderer! renderer container)
    (.add scene (.getMesh hero))
    ;; initialize listeners
    (js/addEventListener "keydown" controls/game-key-down! true)
    (js/addEventListener "keyup" controls/game-key-up! true)
    ;; the actual game loops
    (game-loop/start-time-frame-loop
     (fn [delta-t]
       (render)
       (controls/controls-handler
        {:left-fn #(.moveLeft hero)
         :right-fn #(.moveRight hero)
         :up-fn #(.moveUp hero)
         :down-fn #(.moveDown hero)
         :space-fn #(false)})))))
