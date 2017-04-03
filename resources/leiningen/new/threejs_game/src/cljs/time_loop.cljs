(ns {{project-ns}}.time-loop)

(defn raf-previous-time-context
  "Call f in requestAnimationFrame with the additional context of current-time "
  [f previous-time]
  (js/requestAnimationFrame (fn [current-time]
                              (f current-time previous-time))))

(defn time-loop
  "Deref fatom and call it on delta-t where delta-t (Δt) = current-time - previous-time.
  current-time and previous-time are defined by raf-previous-time-context

  delta-t will typically be 1/60 second, based on:
  \"The number of callbacks is usually 60 times per second, but will generally
  match the display refresh rate in most web browsers as per W3C
  recommendation.\"
  https://developer.mozilla.org/en-US/docs/Web/API/window/requestAnimationFrame

  delta-t can optionally be modified by a factor of chi. The default value of chi is 1.

  time-loop should be initially called by start-time-loop.
  ex: (start-time-loop time-fn request-id)"
  [fatom & [chi]]
  (fn [current-time previous-time]
    (let [previous-time (if (= previous-time nil)
                          current-time
                          previous-time)
          delta-t  (- current-time previous-time) ; Δt
          chi (or chi 1)                ; Χ, after Χρόνος aka chronos
          ]
      (@fatom (* delta-t chi))
      (raf-previous-time-context
       (time-loop fatom chi) current-time))))

(defn start-time-loop
  "Start the time loop. fatom contains a reference to a f of
  delta-t. delta-t can optionally be modified by a factor of chi."
  [fatom & [chi]]
  (raf-previous-time-context (time-loop fatom chi) nil))
