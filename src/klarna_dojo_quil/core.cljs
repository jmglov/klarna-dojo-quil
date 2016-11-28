(ns klarna-dojo-quil.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :rgb)
  (q/stroke 255 0 0))

(defn draw [state]
  (q/line 0 0 (q/mouse-x) (q/mouse-y))
  (q/line 200 0 (q/mouse-x) (q/mouse-y))
  (q/line 0 200 (q/mouse-x) (q/mouse-y))
  (q/line 200 200 (q/mouse-x) (q/mouse-y)))

(q/defsketch klarna-dojo-quil
  :title "You can see lines"
  :host "klarna-dojo-quil"
  :size [500 500]
  :setup setup
  :draw draw
  :features [:keep-on-top])
