(ns {{project-ns}}.components
  (:require [reagent.core :as r]))

(defn TitleScreen
  []
  (fn [{:keys [selected-menu-item]}]
    [:div {:id "title-screen"}
     [:div {:id "title"
            :style {:color "#FFF"}}
      "template-game"]
     [:div {:id "title-menu"}
      [:div {:id "start"}
       [:div {:class "selection-symbol"}
        (str (if (= @selected-menu-item
                    "start")
               "→"
               ""))]
       " Start"]
      [:div {:id "foo"}
       [:div {:class "selection-symbol"}
        (str (if (= @selected-menu-item
                    "foo")
               "→"
               ""))]
       " Foo"]
      [:div {:id "bar"}
       [:div {:class "selection-symbol"}
        (str (if (= @selected-menu-item
                    "bar")
               "→"
               ""))]
       " Bar" ]]]))

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
