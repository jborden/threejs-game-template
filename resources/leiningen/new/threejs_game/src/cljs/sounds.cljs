(ns {{project-ns}}.sounds
    (:require-macros [reagent.interop :refer [$]])
    (:require [reagent.core :as r]
              [cljsjs.howler]
              [{{project-ns}}.utilities :as utilities]))

;; sounds generated with bfxr
;; http://www.bfxr.net/
(def urls (mapv #(str "audio/" %)
                ["Hit_Hurt19.wav"
                 "Powerup9.wav"]))

(defn play-sound
  [state filename]
  ($ @(r/cursor state [:sounds filename]) play))

(defn stop-sound
  [state filename]
  ($ @(r/cursor state [:sounds filename]) stop))

(defn sound-loader
  [state url]
  (let [sounds (r/cursor state [:sounds])
        sound (js/Howl. (clj->js {:src [url]}))]
    (swap! sounds assoc (utilities/url->filename url) sound)))

