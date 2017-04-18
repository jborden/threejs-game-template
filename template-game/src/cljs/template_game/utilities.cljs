(ns template-game.utilities
  (:require-macros [reagent.interop :refer [$ $!]]))

(defn calculate-distance
  "Given two THREE.Object3d objects, calculate the distance between them"
  [a b]
  ($ ($ a getWorldPosition)
     ;; from https://threejs.org/docs/api/math/Vector3.html
     ;; "If you are just comparing the distance with another distance, you should compare the distance squared instead as it is slightly more efficient to calculate."
     distanceToSquared
     ($ b getWorldPosition)))

(defn normalized-distance-vector
  "Given two THREE.Object3d objects, return the normalized distance vector of a->b"
  [a b]
  (let [point-a ($ a position.clone)
        point-b ($ b position.clone)
        a->b ($ point-b sub point-a)]
    ($ a->b normalize)))

(defn find-nearest-object
  "Given a THREE.Object3d target, find the closest THREE.Object3d object in object-pool list"
  [target object-pool nearest-object]
  (cond
    (empty? object-pool) ;; if the object-pool was completely consumed, nearest-object is it
    nearest-object
    (nil? nearest-object) ;; if there isn't a nearest-object, initialize it's value with the first object in object-pool
    (find-nearest-object target (rest object-pool) (first object-pool))
    :else
    (let [nearest-object-distance (calculate-distance target nearest-object)
          next-object (first object-pool)
          next-object-distance (calculate-distance target next-object)]
      (if (< nearest-object-distance next-object-distance)
        (find-nearest-object target (rest object-pool) nearest-object) ;; nearest object still closer
        (find-nearest-object target (rest object-pool) next-object) ;; the next object was closer
        ))))
