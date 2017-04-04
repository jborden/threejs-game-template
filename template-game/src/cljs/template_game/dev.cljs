(ns template-game.dev
  (:require [template-game.core :as core]))

(defn ^:export on-jsload
  []
  (reset! core/state core/initial-state)
  (core/load-game-assets))
