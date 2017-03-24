(ns template-game.display
    (:require [cljsjs.three]))

(defn init-camera!
  "Given a camera, initiliaze it in scene (Three.Scene object) with
  [x y z] vector for initial position"
  [camera scene position]
  (.add scene camera)
  (apply #(.position.set camera %1 %2 %3) position)
  (.lookAt camera (.-position scene))
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
  (let [x (.-position.x camera)
        y (.-position.y camera)
        z (.-position.z camera)]
    (.position.set camera
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
    (.setSize renderer
              (.-innerWidth js/window)
              (.-innerHeight js/window))
    renderer))

(defn render
  [renderer scene camera]
  (fn [] (.render renderer scene camera)))

(defn attach-renderer!
  "Attach renderer to container with div-id"
  [renderer container]
  (.appendChild container (.-domElement renderer)))
