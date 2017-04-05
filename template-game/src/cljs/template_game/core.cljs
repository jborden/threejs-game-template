(ns template-game.core
  (:require-macros [reagent.interop :refer [$ $!]])
  (:require [reagent.core :as r]
            [cljsjs.three]
            [template-game.components :refer [PauseComponent TitleScreen GameContainer]]
            [template-game.controls :as controls]
            [template-game.display :as display]
            [template-game.time-loop :as time-loop]))

(def initial-state {:paused? false
                    :key-state {}
                    :selected-menu-item "start"
                    :time-fn (constantly true)
                    :font nil})

(defonce state (r/atom initial-state))

(defn hero
  []
  (let [material (js/THREE.MeshBasicMaterial. (clj->js {:color 0xFF0000}))
        geometry (js/THREE.PlaneGeometry. 200 200 1)
        mesh (js/THREE.Mesh. geometry material)
        move-increment 5]
    (reify
      Object
      (moveLeft [this]
        ($ mesh translateX (- move-increment)))
      (moveRight [this]
        ($ mesh translateX move-increment))
      (moveUp [this]
        ($ mesh translateY move-increment))
      (moveDown [this]
        ($ mesh translateY (- move-increment)))
      (getMesh [this] mesh))))

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
        group (js/THREE.Group.)
        mesh (js/THREE.Mesh. geometry material)]
    ($ geometry computeBoundingBox)
    (reify
      Object
      (getMesh [this] mesh)
      (getGeometry [this] geometry)
      (moveTo [this x y]
        (let [x-center (/ (- ($ geometry :boundingBox.max.x)
                             ($ geometry :boundingBox.min.x))
                          2)
              y-center (/ (- ($ geometry :boundingBox.max.y)
                             ($ geometry :boundingBox.min.y))
                          2)]
          ($! mesh :position.x (- x x-center))
          ($! mesh :position.y (- y y-center)))))))

(defn game-fn
  "The main game, as a fn of delta-t and state"
  []
  (let [hero (r/cursor state [:hero])
        render-fn (r/cursor state [:render-fn])
        key-state (r/cursor state [:key-state])
        paused? (r/cursor state [:paused?])
        key-state (r/cursor state [:key-state])
        ticks-max 20
        ticks-counter (r/cursor state [:ticks-counter])]
    (fn [delta-t]
      (@render-fn)
      ;; p-key is up, reset the delay
      (if (not (:p @key-state))
        (reset! ticks-counter 0))
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
        font-atom (r/cursor state [:font])
        goal (goal font-atom "Goal")
        paused? (r/cursor state [:paused?])
        key-state (r/cursor state [:key-state])
        key-state-tracker (r/cursor state [:key-state-tracker])]
    (swap! state assoc
           :render-fn render-fn
           :hero hero
           :goal goal)
    ($ scene add (.getMesh hero))
    ($ scene add (.getMesh goal))
    (.moveTo goal 0 -400)
    ($! js/window :onblur #(do (swap! state assoc :paused? true)))
    (display/attach-window-resize! renderer camera)
    (reset! time-fn (game-fn))
    (r/render-component
     [:div
      [GameContainer {:renderer renderer}]
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
  (let [key-state (r/cursor state [:key-state])
        menu-items (r/atom
                    [{:id "start"
                      :selected? true
                      :on-click (fn [e]
                                  (r/unmount-component-at-node
                                   ($ js/document getElementById "reagent-app"))
                                  (load-game-assets))}
                     {:id "foo"
                      :selected? false
                      :on-click (fn [e]
                                  ($ js/console log "foo"))}
                     {:id "bar"
                      :selected? false
                      :on-click (fn [e]
                                  ($ js/console log "foo"))}])
        selected-menu-item (r/cursor
                            state [:selected-menu-item])
        controls-context (r/cursor
                          state [:controls-context])
        ticks-max 20
        down-ticks-counter (r/atom 0)
        up-ticks-counter (r/atom 0)
        enter-ticks-counter (r/atom 0)]
    (fn [delta-t]
      ;; reset the delay when no keys pressed
      (if (and (not (:down-arrow @key-state))
               (not (:s @key-state)))
        (reset! down-ticks-counter 0))
      (if (and (not (:up-arrow @key-state))
               (not (:w @key-state)))
        (reset! up-ticks-counter 0))
      (if-not (:enter @key-state)
        (reset! enter-ticks-counter 0))
      ;; react to the controls
      (controls/key-down-handler
       @key-state
       {:enter-fn
        (fn [] (controls/delay-repeat ticks-max enter-ticks-counter
                                      (fn [] ((:on-click
                                               (first (filterv #(= @selected-menu-item (:id %)) @menu-items)))))))
        :down-fn (fn []
                   (let [move-cursor! (fn []
                                        (let [menu-selection (mapv :selected? @menu-items)
                                              menu-ids (mapv :id @menu-items)
                                              current-selection (.indexOf menu-selection true)
                                              next-selection (if (>= current-selection
                                                                     (- (count menu-selection) 1))
                                                               0
                                                               (+ 1 current-selection))
                                              new-menu-id (get menu-ids next-selection)]
                                          (reset! menu-items (mapv #(assoc % :selected? (= new-menu-id (:id %))) @menu-items))
                                          (reset! selected-menu-item new-menu-id)))]
                     (controls/delay-repeat ticks-max down-ticks-counter move-cursor!)))
        :up-fn (fn []
                 (let [move-cursor! (fn []
                                      (let [menu-selection (mapv :selected? @menu-items)
                                            menu-ids (mapv :id @menu-items)
                                            current-selection (.indexOf menu-selection true)
                                            next-selection (if (= current-selection
                                                                  0)
                                                             (- (count menu-selection) 1)
                                                             (- current-selection 1))
                                            new-menu-id (get menu-ids next-selection)]
                                        (reset! menu-items (mapv #(assoc % :selected? (= new-menu-id (:id %))) @menu-items))
                                        (reset! selected-menu-item new-menu-id)))]
                   (controls/delay-repeat ticks-max up-ticks-counter move-cursor!)))}))))

(defn ^:export init-title-screen
  []
  (let [time-fn (r/cursor state [:time-fn])
        key-state (r/cursor state [:key-state])
        selected-menu-item (r/cursor state [:selected-menu-item])]
    (controls/initialize-key-listeners! key-state)
    ;; reset the time-fn
    (reset! time-fn (title-screen-fn))
    ;; mount the component
    (r/render-component
     [TitleScreen {:selected-menu-item selected-menu-item}]
     ($ js/document getElementById "reagent-app"))
    ;; start the loop
    (time-loop/start-time-loop time-fn)))
