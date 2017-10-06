(ns template-game.core
  (:require-macros [reagent.interop :refer [$ $!]])
  (:require [reagent.core :as r]
            [cljsjs.three]
            [template-game.components :refer [PauseComponent TitleScreen GameContainer GameWonScreen GameLostScreen]]
            [template-game.controls :as controls]
            [template-game.display :as display]
            [template-game.menu :as menu]
            [template-game.time-loop :as time-loop]
            [template-game.utilities :as utilities]))

(def initial-state {:paused? false
                    :key-state {}
                    :selected-menu-item "start"
                    :time-fn (constantly true)
                    :init-game (constantly true)
                    :init-game-won-fn (constantly true)
                    :init-title-screen-fn (constantly true)
                    :font nil})

(defonce state (r/atom initial-state))

(defn enemy
  []
  (let [geometry (js/THREE.PlaneGeometry. 100 100 1)
        material (js/THREE.MeshBasicMaterial. (clj->js {:color 0xFF0000}))
        mesh (js/THREE.Mesh. geometry material)
        object3d ($ (js/THREE.Object3D.) add mesh)
        box-helper (js/THREE.BoxHelper. object3d 0x00ff00)
        bounding-box (js/THREE.Box3.)
        move-increment 5]
    (reify
      Object
      (updateBox [this]
        ($ box-helper update)
        ($ bounding-box setFromObject box-helper))
      (intersectsBox [this box]
        ($ (.getBoundingBox this) intersectsBox box))
      (getObject3d [this] object3d)
      (getBoundingBox [this] bounding-box)
      (getBoxHelper [this] box-helper)
      (moveTo [this x y]
        (let [x-center (/ (- ($ bounding-box :max.x)
                             ($ bounding-box :min.x))
                          2)
              y-center (/
                        (- ($ bounding-box :max.y)
                           ($ bounding-box :min.y))
                        2)]
          ($! object3d :position.x (- x x-center))
          ($! object3d :position.y (- y y-center))
          (.updateBox this)))
      (chaseHero [this hero dL]
        (let [hero-object (.getObject3d hero)
              this-object (.getObject3d this)
              this->hero
              (utilities/normalized-distance-vector
               this-object hero-object)]
          ;; if the distance between hero and this is larger than dL
          ;; pursue hero
          (when (> (utilities/calculate-distance hero-object this-object) dL)
            ($ this-object position.add
               ($ this->hero multiplyScalar dL))
            (.updateBox this)))))))

(defn hero
  []
  (let [geometry (js/THREE.PlaneGeometry. 200 200 1)
        material (js/THREE.MeshBasicMaterial. (clj->js {:color 0x0000FF}))
        mesh (js/THREE.Mesh. geometry material)
        object3d ($ (js/THREE.Object3D.) add mesh)
        box-helper (js/THREE.BoxHelper. object3d 0x00ff00)
        bounding-box (js/THREE.Box3.)
        move-increment 5]
    (reify
      Object
      (updateBox [this]
        ($ box-helper update)
        ($ bounding-box setFromObject box-helper))
      (moveLeft [this]
        ($ object3d translateX (- move-increment))
        (.updateBox this))
      (moveRight [this]
        ($ object3d translateX move-increment)
        (.updateBox this))
      (moveUp [this]
        ($ object3d translateY move-increment)
        (.updateBox this))
      (moveDown [this]
        ($ object3d translateY (- move-increment))
        (.updateBox this))
      (getObject3d [this] object3d)
      (getBoundingBox [this] bounding-box)
      (getBoxHelper [this] box-helper))))

(defn load-font!
  [url font-atom]
  ($ (js/THREE.FontLoader.)
     load
     url
     (fn [font]
       (reset! font-atom font))))

(defn goal
  [font-atom text]
  (let [geometry (js/THREE.TextGeometry. text
                                         (clj->js {:font @font-atom
                                                   :size 50
                                                   :height 10}))
        material (js/THREE.MeshBasicMaterial. (clj->js {:color 0xD4AF37}))
        mesh (js/THREE.Mesh. geometry material)
        object3d ($ (js/THREE.Object3D.) add mesh)
        box-helper (js/THREE.BoxHelper. object3d 0x00ff00)
        bounding-box (js/THREE.Box3.)]
    (reify
      Object
      (getObject3d [this] object3d)
      (getBoundingBox [this] bounding-box)
      (getBoxHelper [this] box-helper)
      (updateBox [this]
        ($ box-helper update)
        ($ bounding-box setFromObject box-helper)
        ;;($! box-helper :visible false)
        )
      (intersectsBox [this box]
        ($ (.getBoundingBox this) intersectsBox box))
      (moveTo [this x y]
        (let [x-center (/ (- ($ bounding-box :max.x)
                             ($ bounding-box :min.x))
                          2)
              y-center (/
                        (- ($ bounding-box :max.y)
                           ($ bounding-box :min.y))
                        2)]
          ($! object3d :position.x (- x x-center))
          ($! object3d :position.y (- y y-center))
          (.updateBox this))))))

(defn game-won-fn
  []
  (menu/menu-screen
   state
   20
   (r/atom
    [{:id "play-again"
      :selected? true
      :on-click (fn [e]
                  (@(r/cursor state [:init-game])))}
     {:id "title-screen"
      :selected? false
      :on-click (fn [e]
                  (@(r/cursor state [:init-title-screen-fn])))}])))

(defn init-game-won-screen
  "The game is won, go to 'you win' screen"
  []
  (let [time-fn (r/cursor state [:time-fn])
        key-state (r/cursor state [:key-state])
        selected-menu-item (r/cursor state [:selected-menu-item])]
    (reset! key-state (:key-state initial-state))
    (reset! selected-menu-item "play-again")
    (reset! time-fn (game-won-fn))
    (r/render
     [GameWonScreen {:selected-menu-item selected-menu-item}]
     ($ js/document getElementById "reagent-app"))))

(defn init-game-lost-screen
  "The game is lost, go to 'Game Over' screen"
  []
  (let [time-fn (r/cursor state [:time-fn])
        key-state (r/cursor state [:key-state])
        selected-menu-item (r/cursor state [:selected-menu-item])]
    (reset! key-state (:key-state initial-state))
    (reset! selected-menu-item "play-again")
    (reset! time-fn (game-won-fn))
    (r/render
     [GameLostScreen {:selected-menu-item selected-menu-item}]
     ($ js/document getElementById "reagent-app"))))

(defn game-fn
  "The main game, as a fn of delta-t and state"
  []
  (let [hero (r/cursor state [:hero])
        enemy (r/cursor state [:enemy])
        goal (r/cursor state [:goal])
        render-fn (r/cursor state [:render-fn])
        key-state (r/cursor state [:key-state])
        paused? (r/cursor state [:paused?])
        key-state (r/cursor state [:key-state])
        ticks-max 20
        ticks-counter (r/cursor state [:ticks-counter])]
    (fn [delta-t]
      (@render-fn)
      (when (.intersectsBox @goal (.getBoundingBox @hero))
        (init-game-won-screen))
      (when (.intersectsBox @enemy (.getBoundingBox @hero))
        (init-game-lost-screen))
      ;; p-key is up, reset the delay
      (if (not (:p @key-state))
        (reset! ticks-counter 0))
      ;; chase hero
;;      (.chaseHero @enemy @hero 1.4)
      ;; move the hero when not paused
      (when-not @paused?
        (controls/key-down-handler
         @key-state
         {:left-fn #(.moveLeft @hero)
          :right-fn #(.moveRight @hero)
          :up-fn #(.moveUp @hero)
          :down-fn #(.moveDown @hero)}))
      ;; listen for the p-key depress
      (controls/key-down-handler
       @key-state
       {:p-fn (fn [] (controls/delay-repeat ticks-max ticks-counter
                                            #(reset! paused? (not @paused?))))}))))

(defn ^:export init-game
  "Function to setup and start the game"
  []
  (let [scene (js/THREE.Scene.)
        camera (display/init-camera!
                (display/create-perspective-camera
                 45
                 (/ ($ js/window :innerWidth)
                    ($ js/window :innerHeight))
                 0.1
                 20000)
                scene
                [0 0 1300])
        renderer (display/create-renderer)
        render-fn (display/render renderer scene camera)
        time-fn (r/cursor state [:time-fn])
        hero (hero)
        enemy (enemy)
        font-atom (r/cursor state [:font])
        goal (goal font-atom "Goal")
        paused? (r/cursor state [:paused?])
        key-state (r/cursor state [:key-state])
        key-state-tracker (r/cursor state [:key-state-tracker])]
    (swap! state assoc
           :render-fn render-fn
           :hero hero
           :goal goal
           :enemy enemy
           :scene scene)
    (.updateBox hero)
    (.updateBox goal)
    (.updateBox enemy)
    ($ scene add (.getObject3d hero))
    ($ scene add (.getBoxHelper hero))
    ($ scene add (.getObject3d goal))
    ($ scene add (.getBoxHelper goal))
    ($ scene add (.getObject3d enemy))
    ($ scene add (.getBoxHelper enemy))
    (.moveTo goal 0 -300)
    (.moveTo enemy 50 400)
    (reset! time-fn (game-fn))
    (r/render
     [:div {:id "root-node"}
      [GameContainer {:renderer renderer
                      :camera camera
                      :state state}]
      [PauseComponent {:paused? paused?
                       :on-click (fn [event]
                                   (reset! paused? false))}]]
     ($ js/document getElementById "reagent-app"))))

(defn load-assets-fn
  []
  (let [font (r/cursor state [:font])]
    (fn [delta-t]
      (when-not (nil? @font)
        (init-game)))))

(defn load-game-assets
  []
  (let [font-url "fonts/helvetiker_regular.typeface.json"
        font-atom (r/cursor state [:font])
        time-fn (r/cursor state [:time-fn])]
    (load-font! font-url font-atom)
    (reset! time-fn (load-assets-fn))))

(defn title-screen-fn
  []
  (menu/menu-screen state
                    20
                    (r/atom
                     [{:id "start"
                       :selected? true
                       :on-click (fn [e]
                                   (load-game-assets))}
                      {:id "foo"
                       :selected? false
                       :on-click (fn [e]
                                   ($ js/console log "foo"))}
                      {:id "bar"
                       :selected? false
                       :on-click (fn [e]
                                   ($ js/console log "foo"))}])))

(defn ^:export init-title-screen
  []
  (let [time-fn (r/cursor state [:time-fn])
        selected-menu-item (r/cursor state [:selected-menu-item])
        key-state (r/cursor state [:key-state])]
    (reset! key-state (:key-state initial-state))
    (reset! selected-menu-item "start")
    ;; reset the time-fn
    (reset! time-fn (title-screen-fn))
    ;; mount the component
    (r/render
     [TitleScreen {:selected-menu-item selected-menu-item}]
     ($ js/document getElementById "reagent-app"))))

(defn ^:export init
  []
  (let [time-fn (r/cursor state [:time-fn])
        init-game-fn (r/cursor state [:init-game])
        key-state (r/cursor state [:key-state])
        init-game-won-fn (r/cursor state [:init-game-won-fn])
        init-title-screen-fn (r/cursor state [:init-title-screen-fn])]
    ;; start controls listeners
    (controls/initialize-key-listeners! key-state)
    ;; set init-fn's
    (reset! init-game-fn init-game)
    (reset! init-title-screen-fn init-title-screen)
    ;; start the loop
    (time-loop/start-time-loop time-fn)
    ;; initialize the title-screen
    (@init-title-screen-fn)))
