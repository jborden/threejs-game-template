(ns template-game.core
    (:require [reagent.core :as r]
              [cljsjs.three]
              [template-game.components :refer [PauseComponent TitleScreen]]
              [template-game.display :as display]
              [template-game.game-loop :as game-loop]
              [template-game.controls :as controls]))

(def state (r/atom {:paused? false}))

(defn hero
  []
  (let [material (js/THREE.MeshBasicMaterial. (clj->js {:color 0xFF0000}))
        geometry (js/THREE.PlaneGeometry. 200 200 1)
        mesh (js/THREE.Mesh. geometry material)
        move-increment 5]
    (reify
      Object
      (moveLeft [this]
        (.translateX mesh (- move-increment)))
      (moveRight [this]
        (.translateX mesh move-increment))
      (moveUp [this]
        (.translateY mesh move-increment))
      (moveDown [this]
        (.translateY mesh (- move-increment)))
      (getMesh [this] mesh))))

(defn ^:export init-game
  "Function to setup and start the game"
  []
  (let [scene (js/THREE.Scene.)
        camera (display/init-camera!
                (display/create-perspective-camera
                 45
                 (/ (.-innerWidth js/window)
                    (.-innerHeight js/window))
                 0.1
                 20000)
                scene
                [0 0 1300])
        renderer (display/create-renderer)
        render (display/render renderer scene camera)
        request-id (atom nil)
        container (-> js/document
                      (.getElementById "game-container"))
        hero (hero)]
    (swap! state assoc :hero hero :request-id request-id)
    (display/attach-renderer! renderer container)
    (.add scene (.getMesh hero))
    ;; initialize listeners
    (js/addEventListener "keydown" controls/game-key-down! true)
    (js/addEventListener "keyup" controls/game-key-up! true)
    (js/addEventListener "onfocus" #(swap! state assoc :paused? false) true)
    (js/addEventListener "onblur" #(swap! state assoc :paused? false) true)
    (r/render-component
     (let [paused? (r/cursor state [:paused?])]
       [PauseComponent {:paused? paused?
                        :on-click (fn [event] (reset! paused? false))}])
     (.getElementById js/document "reagent-app"))
    ;; the actual game loops
    (game-loop/start-time-frame-loop
     (fn [delta-t]
       (render)
       (controls/controls-handler
        {:left-fn #(.moveLeft hero)
         :right-fn #(.moveRight hero)
         :up-fn #(.moveUp hero)
         :down-fn #(.moveDown hero)}))
     request-id)))

(defn ^:export init-title-screen
  []
  (let [request-id (atom nil)]
    (js/addEventListener "keydown" controls/game-key-down! true)
    (js/addEventListener "keyup" controls/game-key-up! true)
    (game-loop/start-time-frame-loop
     (fn [delta-t]
       (controls/controls-handler
        {:enter-fn #(do (js/removeEventListener "keydown" controls/game-key-down! true)
                        (js/removeEventListener "keyup" controls/game-key-down! true)
                        (js/cancelAnimationFrame @request-id)
                        (reset! request-id "stop")
                        (r/unmount-component-at-node
                         (.getElementById js/document
                                          "reagent-app"))
                        (init-game))}))
     request-id)
    (r/render-component
     [TitleScreen]
     (.getElementById js/document "reagent-app"))))
