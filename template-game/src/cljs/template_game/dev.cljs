(ns template-game.dev
  (:require-macros [reagent.interop :refer [$]])
  (:require [template-game.core :as core]
            [reagent.core :as r]))

(defn ^:export on-jsload
  []
  (r/unmount-component-at-node
   ($ js/document getElementById "reagent-app"))
  (reset! core/state core/initial-state)
  (core/load-game-assets))
