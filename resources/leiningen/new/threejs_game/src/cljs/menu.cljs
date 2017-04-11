(ns {{project-ns}}.menu
  (:require [reagent.core :as r]
            [{{project-ns}}.controls :as controls]))

(defn menu-screen
  "Select through menu items"
  [state ticks-max menu-items]
  (let [key-state (r/cursor state [:key-state])
        selected-menu-item (r/cursor
                            state [:selected-menu-item])
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
