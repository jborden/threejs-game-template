(ns {{project-ns}}.fonts
    (:require-macros [reagent.interop :refer [$ $!]])
    (:require [cljsjs.three]
              [reagent.core :as r]
              [{{project-ns}}.utilities :as utilities]))

(def urls (mapv #(str "fonts/" %)
                ["helvetiker_regular.typeface.json"]))

(defn font-loader
  [state url]
  (let [fonts (r/cursor state [:fonts])]
    ($ (js/THREE.FontLoader.)
       load
       url
       (fn [font]
         (swap! fonts assoc (utilities/url->filename url) font)))))

(defn text
  "Given the state, a font-name corresponding to one defined in urls, the text to be used and an optional color, return an object that can be added to a THREE.js scene"
  [state font-name text & [color]]
  (let [geometry (js/THREE.TextGeometry. text
                                         (clj->js {:font @(r/cursor state [:fonts font-name])
                                                   :size 50
                                                   :height 10}))
        material (js/THREE.MeshBasicMaterial. (clj->js {:color (or color 0xD4AF37)}))
        mesh (js/THREE.Mesh. geometry material)
        object3d ($ (js/THREE.Object3D.) add mesh)
        box-helper (js/THREE.BoxHelper. object3d 0x00ff00)
        bounding-box (js/THREE.Box3.)]
    (reify
      Object
      (getObject3d [this] object3d)
      (getBoundingBox [this] bounding-box)
      (getBoxHelper [this] box-helper)
      (updateBox [this]
        ($ box-helper update)
        ($ bounding-box setFromObject box-helper)
        ;;($! box-helper :visible false)
        )
      (intersectsBox [this box]
        ($ (.getBoundingBox this) intersectsBox box))
      (moveTo [this x y]
        ($! object3d :position.x x)
        ($! object3d :position.y y)
        (.updateBox this)))))
