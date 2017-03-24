(ns template-game.controls)

;; additional keycodes can be obtained from keycode.info
;; arrow keys
(def left-arrow 37)
(def up-arrow 38) 
(def right-arrow 39)
(def down-arrow 40)
;; wasd keys
(def a-key 65)
(def w-key 87)
(def d-key 68)
(def s-key 83)

(def space-key 32)
(def enter-key 13)

(def key-state (js-obj))

(defn game-key-down!
  "Handle event related to when a user presses down on a key. This modifies 
  key-state"
  [event]
  (aset key-state (or (.-keycode event)
                      (.-which event)) true))

(defn game-key-up!
  "Handle event related to when a user releases a key. This modifies key-state"
  [event]
  (aset key-state (or (.-keycode event)
                      (.-which event)) false))

(defn controls-handler
  "Handle user input. The inputs to this fn will change based on context"
  [{:keys [left-fn up-fn right-fn down-fn space-fn enter-fn]
    :or {left-fn (constantly true)
         up-fn (constantly true)
         right-fn (constantly true)
         down-fn (constantly true)
         space-fn (constantly true)
         enter-fn (constantly true)}}]
  ;; cond/condp won't work because you have to account for when two keys
  ;; are held simultaneously!
  ;; NOTE: Camera is currently floating above xy plane, these controls will have
  ;; to be adjusted when the camera is looking down the x-y plane
  ;; left
  (if (or (aget key-state left-arrow)
          (aget key-state a-key))
    (left-fn))
  ;; up
  (if (or (aget key-state up-arrow)
          (aget key-state w-key))
    (up-fn))
  ;; right
  (if (or (aget key-state right-arrow)
          (aget key-state d-key))
    (right-fn))
  ;; down
  (if (or (aget key-state down-arrow)
          (aget key-state s-key))
    (down-fn))
  ;; space
  (if (aget key-state space-key)
    (space-fn))
  ;; enter
  (if (aget key-state enter-key)
    (enter-fn)))


