(ns klarna-dojo-quil.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :rgb))

(defn line [[r g b] [x y]]
  (q/stroke r g b)
  (q/line x y (q/mouse-x) (q/mouse-y)))

(defn draw [state]
  (line [255 0 0] [0 0])
  (line [255 255 0] [500 0])
  (line [0 255 0] [0 500])
  (line [0 0 255] [500 500]))

(q/defsketch klarna-dojo-quil
  :title "Yay! Rainbows!"
  :host "klarna-dojo-quil"
  :size [510 510]
  :setup setup
  :draw draw
  :features [:keep-on-top])
