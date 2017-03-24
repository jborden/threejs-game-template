(ns template-game.dev
    (:require [template-game.core :as core]))

(defn ^:export on-jsload
  []
  (core/init-title-screen))
