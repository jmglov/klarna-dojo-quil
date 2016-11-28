(ns klarna-dojo-quil.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :rgb)
  (q/stroke 255 0 0))

(defn draw [state])

(q/defsketch klarna-dojo-quil
  :host "klarna-dojo-quil"
  :size [500 500]
  :setup setup
  :draw draw)
