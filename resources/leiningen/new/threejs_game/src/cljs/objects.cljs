(ns {{project-ns}}.objects
    (:require-macros [reagent.interop :refer [$ $!]])
    (:require [cljsjs.three]
              [{{project-ns}}.utilities :as utilities]))

(defn hero
  []
  (let [geometry (js/THREE.PlaneGeometry. 200 200 1)
        material (js/THREE.MeshBasicMaterial. (clj->js {:color 0x0000FF}))
        mesh (js/THREE.Mesh. geometry material)
        object3d ($ (js/THREE.Object3D.) add mesh)
        box-helper (js/THREE.BoxHelper. object3d 0x00ff00)
        bounding-box (js/THREE.Box3.)
        move-increment 5]
    (reify
      Object
      (updateBox [this]
        ($ box-helper update)
        ($ bounding-box setFromObject box-helper))
      (moveLeft [this]
        ($ object3d translateX (- move-increment))
        (.updateBox this))
      (moveRight [this]
        ($ object3d translateX move-increment)
        (.updateBox this))
      (moveUp [this]
        ($ object3d translateY move-increment)
        (.updateBox this))
      (moveDown [this]
        ($ object3d translateY (- move-increment))
        (.updateBox this))
      (getObject3d [this] object3d)
      (getBoundingBox [this] bounding-box)
      (getBoxHelper [this] box-helper))))

(defn enemy
  [{:keys [texture height width]
    :or {height 100
         width 100
         texture nil}}]
  (let [geometry (js/THREE.PlaneGeometry. height width 1)
        material (js/THREE.MeshBasicMaterial. (if (nil? texture)
                                                (clj->js {:color 0xFF0000})
                                                (clj->js {:map texture
                                                          :side js/THREE.DoubleSide
                                                          :transparent true})))
        mesh (js/THREE.Mesh. geometry material)
        object3d ($ (js/THREE.Object3D.) add mesh)
        box-helper (js/THREE.BoxHelper. object3d 0x00ff00)
        bounding-box (js/THREE.Box3.)
        move-increment 5]
    (reify
      Object
      (updateBox [this]
        ($ box-helper update)
        ($ bounding-box setFromObject box-helper))
      (intersectsBox [this box]
        ($ (.getBoundingBox this) intersectsBox box))
      (getObject3d [this] object3d)
      (getBoundingBox [this] bounding-box)
      (getBoxHelper [this] box-helper)
      (moveTo [this x y]
        (let [x-center (/ (- ($ bounding-box :max.x)
                             ($ bounding-box :min.x))
                          2)
              y-center (/
                        (- ($ bounding-box :max.y)
                           ($ bounding-box :min.y))
                        2)]
          ($! object3d :position.x (- x x-center))
          ($! object3d :position.y (- y y-center))
          (.updateBox this)))
      (chaseHero [this hero dL]
        (let [hero-object (.getObject3d hero)
              this-object (.getObject3d this)
              this->hero
              (utilities/normalized-distance-vector
               this-object hero-object)]
          ;; if the distance between hero and this is larger than dL
          ;; pursue hero
          (when (> (utilities/calculate-distance hero-object this-object) dL)
            ($ this-object position.add
               ($ this->hero multiplyScalar dL))
            (.updateBox this)))))))
