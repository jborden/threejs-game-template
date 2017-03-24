(ns {{project-ns}}.components
  (:require [reagent.core :as r]))

(defn TitleScreen
  []
  (fn [props]
    [:div {:id "title-screen"}
     [:div {:id "title"
            :style {:color "#FFF"}}
       "{{name}}"]
     [:div {:id "start-menu"}
      "→ Start Game"]]))
