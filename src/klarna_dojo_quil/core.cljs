(ns klarna-dojo-quil.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def width 510)
(def height 510)

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :rgb))

(defn- ->color-component [c]
  (if (sequential? c)
    (let [pct-x (/ (q/mouse-x) width)
          pct-y (/ (q/mouse-y) height)
          pct (/ (+ pct-x pct-y) 2)
          [start end] c
          range-size (Math/abs (- start end))
          delta (* range-size pct)]
      (if (< start end)
        (+ start delta)
        (+ end delta)))
    c))

(defn- line [color [x y]]
  (let [[r g b] (map ->color-component color)]
    (q/stroke r g b)
    (q/line x y (q/mouse-x) (q/mouse-y))))

(defn draw [state]
  (line [255 [0 127] 0] [0 0])
  (line [[255 0] 255 0] [500 0])
  (line [0 [255 0] [0 255]] [0 500])
  (line [[0 143] 0 255] [500 500]))

(q/defsketch klarna-dojo-quil
  :title "Yay! Rainbows!"
  :host "klarna-dojo-quil"
  :size [width height]
  :setup setup
  :draw draw
  :features [:keep-on-top])
