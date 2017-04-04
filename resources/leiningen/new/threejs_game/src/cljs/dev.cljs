(ns {{project-ns}}.dev
  (:require [{{project-ns}}.core :as core]))

(defn ^:export on-jsload
  []
  (reset! core/state core/initial-state)
  (core/load-game-assets))
