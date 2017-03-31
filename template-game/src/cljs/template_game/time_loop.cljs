(ns template-game.time-loop)

(defn request-animation-frame
  "Call the function callback with previous-time"
  [callback previous-time]
  (js/requestAnimationFrame (fn [current-time]
                              (callback current-time previous-time))))

(defn time-frame-loop
  "Each moment of time occurs within the time-frame-loop aka game-loop.
  A moment is defined as
  delta-t (Δt) = current-time - previous-time.
  current-time and previous-time are defined by request-animation-frame.

  f is an atom which is a reference to a function called on delta-t on each cycle.

  delta-t is
  \"usually 60 times per second, but will generally
  match the display refresh rate in most web browsers as per W3C
  recommendation.\"
  https://developer.mozilla.org/en-US/docs/Web/API/window/requestAnimationFrame

  delta-t can be modified by a factor of chi. The default value of chi is 1.

  time-frame-loop must be initially called by state-time-frame-loop.
  ex: (start-time-frame-loop time-fn request-id)"
  [f request-id & [chi]]
  (fn [current-time previous-time]
    (let [previous-time (if (= previous-time nil)
                          current-time
                          previous-time)
          delta-t  (- current-time previous-time) ; Δt
          chi (or chi 1) ; Χ, after Χρόνος aka chronos
          ]
      (@f (* delta-t chi))
      (reset! request-id (request-animation-frame
                          (time-frame-loop f request-id) current-time)))))

(defn start-time-frame-loop
  "Start the time-frame-loop, passing the atom f which is a reference
  to a function of delta-t. request-id is an atom which will store the
  request id called by requestAnimationFrame in the request-id
  atom. chi is an optional modifier on t. See time-frame-loop for a
  description of f and chi."
  [f request-id & [chi]]
  (reset! request-id (request-animation-frame
                      (time-frame-loop f request-id chi) nil)))
