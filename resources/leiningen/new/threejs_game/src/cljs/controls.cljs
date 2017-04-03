(ns {{project-ns}}.controls)

;; additional keycodes can be obtained from keycode.info
(def key-definitions
  {"37" :left-arrow
   "38" :up-arrow
   "39" :right-arrow
   "40" :down-arrow
   "65" :a
   "87" :w
   "68" :d
   "83" :s
   "80" :p
   "32" :space
   "13" :enter})

(defn game-key-down!
  "Handle event related to when a user presses down on a key. This modifies 
  key-state"
  [key-state event]
  (swap! key-state assoc (get key-definitions (str (or (.-keycode event)
                                                       (.-which event))))
         true))

(defn game-key-up!
  "Handle event related to when a user releases a key. This modifies key-state"
  [key-state event]
  (swap! key-state assoc (get key-definitions (str (or (.-keycode event)
                                                       (.-which event))))
         false))

(defn key-down-handler
  "Handle user input. The inputs to this fn will change based on context"
  [key-state {:keys [left-fn up-fn right-fn down-fn space-fn enter-fn p-fn]
              :or {left-fn (constantly true)
                   up-fn (constantly true)
                   right-fn (constantly true)
                   down-fn (constantly true)
                   space-fn (constantly true)
                   enter-fn (constantly true)
                   p-fn (constantly true)}}]
  ;; cond/condp won't work because you have to account for when two keys
  ;; are held simultaneously!
  ;; NOTE: Camera is currently floating above xy plane, these controls will have
  ;; to be adjusted when the camera is looking down the x-y plane
  ;; left
  (if (or (:left-arrow key-state)
          (:a key-state))
    (left-fn))
  ;; up
  (if (or (:up-arrow key-state)
          (:w key-state))
    (up-fn))
  ;; right
  (if (or (:right-arrow key-state)
          (:s key-state))
    (right-fn))
  ;; down
  (if (or (:down-arrow key-state)
          (:s key-state))
    (down-fn))
  ;; space
  (if (:space key-state)
    (space-fn))
  ;; enter
  (if (:enter key-state)
    (enter-fn))
  ;; p
  (if (:p key-state)
    (p-fn)))

(defn delay-repeat
  "Delay repeating the call to function by ticks-max, keeping track of the amount of ticks in the ticks-counter atom"
  [ticks-max ticks-counter f]
  (let [increase-ticks-counter (fn [] (reset! ticks-counter (+ @ticks-counter 1)))]
    (cond (= @ticks-counter 0)
          (do (f)
              (increase-ticks-counter))
          (< 0 @ticks-counter ticks-max)
          (increase-ticks-counter)
          (= @ticks-counter ticks-max)
          (do (f)
              (reset! ticks-counter 1)))))

(defn initialize-key-listeners!
  "Remove any event listeners that are currently in place. Initialize
  the keyboard inputs listeners that act on key-state r/atom."
  [key-state]
  (js/removeEventListener "keydown" (partial game-key-down! key-state) true)
  (js/removeEventListener "keyup" (partial game-key-up! key-state) true)
  (js/addEventListener "keydown" (partial game-key-down! key-state) true)
  (js/addEventListener "keyup" (partial game-key-up! key-state) true))
