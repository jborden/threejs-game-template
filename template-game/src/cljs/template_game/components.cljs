(ns template-game.components
  (:require [reagent.core :as r]))

(defn TitleScreen
  []
  (fn [props]
    [:div {:id "title-screen"}
     [:div {:id "title"
            :style {:color "#FFF"}}
       "template-game"]
     [:div {:id "start-menu"}
      "→ Start Game"]]))

(defn PauseComponent
  "Props is:
  {:paused? ; r/atom boolean
  :on-click ; fn
  }
  "
  [props]
  (fn [props]
    (let [{:keys [paused? on-click]} props]
      [:div {:id "blocker"
             :style (if @paused?
                      {}
                      {:display "none"})}
       [:div {:id "instructions"
              :style (if @paused?
                       {}
                       {:display "none"})
              :on-click on-click}
        [:span {:style {:font-size "40px"}}
         "Click to Unpause"]
        [:br]
        "W, A, S, D / Arrow Keys = Move, "]])))
