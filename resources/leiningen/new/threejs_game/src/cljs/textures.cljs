(ns {{project-ns}}.textures
    (:require-macros [reagent.interop :refer [$ $!]])
    (:require [reagent.core :as r]
              [cljsjs.three]
              [{{project-ns}}.utilities :as utilities]))

;; note: https://wiki.nesdev.com/w/index.php/Sprite_size
;;       NES has 8x8 and 8x16 sprites. Some on-screen sprites were composed of multiple sprites
;;       NES resolution is 256 x 240
;;
;;       https://wiki.superfamicom.org/snes/show/SNES+Sprites
;;       https://en.wikibooks.org/wiki/Super_NES_Programming/Animated_Sprites
;;       http://web.ics.purdue.edu/~dherring/cgt141/project1/comparison.html
;;
;;       SNES is limited to 16kB sprite patterns this is:
;;       512 8x8 sprites 128 16x16 sprites 32 32x32 sprites 8 64x64 sprites
;;       max sprite size is 64x64
;;       SNES resolution is from 256x224 to 512x448
(def urls (mapv #(str "images/" %)
                ["enemy.png"]))

(defn texture-loader
  [state url]
  (let [textures (r/cursor state [:textures])
        loader (js/THREE.TextureLoader.)]
    ($ loader load
       url
       (fn [texture]
         (swap! textures assoc
                (utilities/url->filename url) texture))
       ;; onLoad and onProgress don't work in THREE.js
       ;; but kept because they appear in
       ;; https://threejs.org/docs/#api/loaders/TextureLoader
       ;; for reasons why, see
       ;; see: https://github.com/mrdoob/three.js/issues/7734
       ;;      https://github.com/mrdoob/three.js/issues/10439
       (fn [xhr]
         (.log js/console (str (* 100 (/ ($ xhr :loaded)
                                         ($ xhr :total)))) url "% loaded"))
       (fn [xhr]
         (.log js/console "An error occured when loaded " url)))))
