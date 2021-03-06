(ns {{project-ns}}.components
  (:require-macros [reagent.interop :refer [$ $!]])
  (:require [goog.dom :as dom]
            [reagent.core :as r]
            [{{project-ns}}.display :as display]))

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

(defn GameWonScreen
  []
  (fn [{:keys [selected-menu-item]}]
    [:div {:id "title-screen"}
     [:div {:id "title"
            :style {:color "#FFF"}}
      "You Win!"]
     [:div {:id "title-menu"}
      [:div {:id "start"}
       [:div {:class "selection-symbol"}
        (str (if (= @selected-menu-item
                    "play-again")
               "→"
               ""))]
       " Play Again"]
      [:div {:id "foo"}
       [:div {:class "selection-symbol"}
        (str (if (= @selected-menu-item
                    "title-screen")
               "→"
               ""))]
       " Title Screen"]]]))

(defn GameLostScreen
  []
  (fn [{:keys [selected-menu-item]}]
    [:div {:id "title-screen"}
     [:div {:id "title"
            :style {:color "#FFF"}}
      "Game Over"]
     [:div {:id "title-menu"}
      [:div {:id "start"}
       [:div {:class "selection-symbol"}
        (str (if (= @selected-menu-item
                    "play-again")
               "→"
               ""))]
       " Play Again"]
      [:div {:id "foo"}
       [:div {:class "selection-symbol"}
        (str (if (= @selected-menu-item
                    "title-screen")
               "→"
               ""))]
       " Title Screen"]]]))

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

(defn AssetLoadingComponent
  "Props is:
   {:assets-loaded-percent ; r/atom number}"
  [props]
  (fn [props]
    (let [{:keys [assets-loaded-percent]} props]
      [:div {:style {:display (str (if (= @assets-loaded-percent 1)
                                     "none"
                                     "block"))
                     :position "absolute"
                     :width "100%"
                     :height "100%"}}
       [:div {:id "instructions"}
        [:div {:style {:width "100%"
                       :position "fixed"
                       :bottom "1em"}}
         [:div {:style {:background-color "darkgray"
                        :height "20px"
                        :width "10em"
                        :margin "0 auto"}}
          [:div {:style {:background-color "white"
                         :width (str (* @assets-loaded-percent 100) "%")
                         :height "20px"}}]]]]])))

(defn GameContainer
  [{:keys [renderer camera state]}]
  (let [on-blur #(swap! state assoc :paused? true)
        on-resize #(display/window-resize! renderer camera)]
    (r/create-class
     {:display-name "game-container"

      :component-did-mount
      (fn [this]
        (dom/appendChild (r/dom-node this) ($ renderer :domElement))
        ($ js/window addEventListener "blur" on-blur)
        ($ js/window addEventListener "resize" on-resize false))

      :component-will-unmount
      (fn [this]
        ($ renderer forceContextLoss)
        ($ js/window removeEventListener "blur" on-blur)
        ($ js/window removeEventListener "resize" on-resize))

      :reagent-render (fn []
                        [:div {:id "game-container"
                               :style {:position "absolute"
                                       :left "0px"
                                       :top "0px"}}])})))
