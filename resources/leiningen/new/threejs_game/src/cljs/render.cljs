(ns {{project.ns}}.render
    (:require [cljsjs.three]))

(defn create-renderer
  []
  (let [renderer
        (if (.-webgl js/Detector)
          (js/THREE.WebGLRenderer. (js-obj "antialias" true))
          (js/THREE.CanvasRender.))]
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
