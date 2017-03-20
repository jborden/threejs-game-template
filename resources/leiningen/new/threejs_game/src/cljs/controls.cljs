(ns {{project-ns}}.controls)

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
;; space key
(def space-key 32)

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


