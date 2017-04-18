(ns {{project-ns}}.dev
  (:require-macros [reagent.interop :refer [$]])
  (:require [{{project-ns}}.core :as core]
            [reagent.core :as r]))

(defn ^:export on-jsload
  []
  (r/unmount-component-at-node
   ($ js/document getElementById "reagent-app"))
  (core/load-game-assets))
