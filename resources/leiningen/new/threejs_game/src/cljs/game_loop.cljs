(ns {{project-ns}}.game-loop)

(def request-id nil)

(defn request-animation-frame
  "Call the function callback with previous-time"
  [callback previous-time]
  (do (js/requestAnimationFrame (fn [current-time]
                                  (callback current-time previous-time)))))

(defn time-frame-loop
  "Each moment of time occurs within the time-frame-loop aka game-loop.
  The time-frame is an instance of time defined as
  delta-t (Δt) = current-time - previous-time.
  current-time and previous-time are defined by request-animation-frame.
  f is called on dt at each instance.
  delta-t is
  \"usually 60 times per second, but will generally
  match the display refresh rate in most web browsers as per W3C
  recommendation.\"
  https://developer.mozilla.org/en-US/docs/Web/API/window/requestAnimationFrame
  delta-t can be modified by a factor of chi. The default value of chi is 1.
  time-frame-loop must be initially called by request-animation-frame.
  ex: (request-animation-frame
                      (time-frame-loop
                       (fn [delta-t]
                         (do (render)
                             (controls/controls-handler camera)))) nil)"
  [f & [chi]]
  (fn [current-time previous-time]
    (let [previous-time (if (= previous-time nil)
                          current-time
                          previous-time)
          delta-t  (- current-time previous-time) ;  Δt
          chi (or chi 1) ; Χ, after Χρόνος aka chronos
          ]
      (f (* delta-t chi))
      (cond
        :else
        (set! request-id (request-animation-frame
                          (time-frame-loop f chi) current-time))))))

(defn start-time-frame-loop
  "Start time-frame-loop using f, keeping track of the request-id of
  requestAnimationFrame in the request-id atom. chi is optional.
  See time-frame-loop for a description of f and chi."
  [f request-id & [chi]]
  (reset! request-id (request-animation-frame (time-frame-loop f chi) nil)))
