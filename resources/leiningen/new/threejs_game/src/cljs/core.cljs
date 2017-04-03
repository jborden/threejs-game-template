(ns {{project-ns}}.core
  (:require [reagent.core :as r]
            [cljsjs.three]
            [{{project-ns}}.components :refer [PauseComponent TitleScreen]]
            [{{project-ns}}.controls :as controls]
            [{{project-ns}}.display :as display]
            [{{project-ns}}.time-loop :as time-loop]))

(def state (r/atom {:paused? false
                    :key-state {}
                    :selected-menu-item "start"
                    :time-fn (constantly true)
                    }))

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
      ;; listen for the p-key action
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
                 (/ (.-innerWidth js/window)
                    (.-innerHeight js/window))
                 0.1
                 20000)
                scene
                [0 0 1300])
        renderer (display/create-renderer)
        render-fn (display/render renderer scene camera)
        time-fn (r/cursor state [:time-fn])
        container (-> js/document
                      (.getElementById "game-container"))
        hero (hero)
        paused? (r/cursor state [:paused?])
        key-state (r/cursor state [:key-state])
        key-state-tracker (r/cursor state [:key-state-tracker])]
    (swap! state assoc :hero hero :render-fn render-fn)
    (display/attach-renderer! renderer container)
    (.add scene (.getMesh hero))
    (set! (.-onblur js/window) #(do (swap! state assoc :paused? true)))
    (reset! time-fn (game-fn))
    (r/render-component
     [PauseComponent {:paused? paused?
                      :on-click (fn [event]
                                  (reset! paused? false))}]
     (.getElementById js/document "reagent-app"))))

(defn title-screen-fn
  []
  (let [key-state (r/cursor state [:key-state])
        menu-items (r/atom
                    [{:id "start"
                      :selected? true
                      :on-click (fn [e]
                                  (r/unmount-component-at-node
                                   (.getElementById js/document
                                                    "reagent-app"))
                                  (init-game))}
                     {:id "foo"
                      :selected? false
                      :on-click (fn [e]
                                  (.log js/console "foo"))}
                     {:id "bar"
                      :selected? false
                      :on-click (fn [e]
                                  (.log js/console "bar"))}])
        selected-menu-item (r/cursor state [:selected-menu-item])
        controls-context (r/cursor state [:controls-context])
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
     (.getElementById js/document "reagent-app"))
    ;; start the loop
    (time-loop/start-time-loop time-fn)))
