(ns {{project-ns}}.dev
    (:require [{{project-ns}}.core :as core]))

(defn ^:export on-jsload
  []
  (core/init-title-screen))
