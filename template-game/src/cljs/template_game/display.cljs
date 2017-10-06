(ns template-game.display
  (:require-macros [reagent.interop :refer [$ $!]])
  (:require [cljsjs.three]))

(defn init-camera!
  "Given a camera, initiliaze it in scene (Three.Scene object) with
  [x y z] vector for initial position"
  [camera scene position]
  ($ scene add camera)
  (apply #($ camera position.set %1 %2 %3) position)
  ($ camera lookAt ($ scene :position))
  camera)

(defn create-perspective-camera
  "Create a THREE.PerspectiveCamera with camera frustrum fov (field of view), 
  aspect (aspect ratio), near (near plane) and far (far plane).
  see: http://threejs.org/docs/#Reference/Cameras/PerspectiveCamera"
  [fov aspect near far]
  (js/THREE.PerspectiveCamera. fov aspect near far))

;; in repl, must be called like
;; (spacetime.camera/change-position! camera [0 0 -50000])
;; outside of namespace
(defn change-position!
  "Change the position of camera by delta vector [x y z]"
  [camera delta]
  (let [x ($ camera :position.x)
        y ($ camera :position.y)
        z ($ camera :position.z)]
    ($ camera position.set
       (+ x (nth delta 0))
       (+ y (nth delta 1))
       (+ z (nth delta 2)))))

;; sometimes there are issues with specific chrome builds
;; and you will get the console error
;; THREE.WebGLRenderer: Error creating WebGL context.
;; see: https://github.com/mrdoob/three.js/issues/9936
;; check GPU support: chrome://gpu
;; possible fix is to simply update Chrome by visiting chrome://help
(defn create-renderer
  []
  (let [renderer (js/THREE.WebGLRenderer. (clj->js {:antialias true}))]
    ($ renderer setSize
       ($ js/window :innerWidth)
       ($ js/window :innerHeight))
    renderer))

(defn render
  [renderer scene camera]
  (fn [] ($ renderer render scene camera)))

(defn window-resize!
  "Update the renderer size and camera aspect based upon window size"
  [renderer camera]
  (let [width ($ js/window :innerWidth)
        height ($ js/window :innerHeight)
        resize-renderer (fn [renderer] ($ renderer setSize width height))
        resize-camera (fn [camera]
                        ($! camera :aspect (/ width height))
                        ($ camera updateProjectionMatrix))]
    (resize-renderer renderer)
    (resize-camera camera)))
