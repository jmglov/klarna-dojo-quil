(ns klarna-dojo-quil.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def framerate 30)

(def screen {:width 640
             :height 480
             :color [211 211 211]})

(def paddle {:width 80
             :height 20
             :color [0 0 0]})

(def ball {:circumference 20
           :color [0 0 255]})

(defn setup []
  (q/frame-rate framerate)
  (q/color-mode :rgb)
  {:ball {:x (/ (:width screen) 2)
          :y (/ (:height screen) 2)
          :dx 2
          :dy 2}
   :paddle {:x (- (/ (:width screen) 2) (/ (:width paddle) 2))
            :y (- (:height screen) (:height paddle))}})

(defn bounce-ball [state]
  (let [{:keys [x y dx dy]} (:ball state)
        radius (/ (:circumference ball) 2)
        max-x (- (:width screen) radius)]
    (if (> x max-x)
      (-> state
          (assoc-in [:ball :x] max-x)
          (update-in [:ball :dy] #(* % -1)))
      state)))

(defn update-state [state]
  (let [{:keys [dx dy]} (:ball state)]
    (-> state
        (update-in [:ball :x] #(+ % dx))
        (update-in [:ball :y] #(+ % dy))
        bounce-ball)))

(defn clear-screen! []
  (apply q/background (:color screen)))

(defn set-color! [[r g b]]
  (q/fill r g b)
  (q/stroke r g b))

(defn draw-ball! [state]
  (let [{:keys [x y]} (:ball state)
        {:keys [circumference color]} ball]
    (set-color! color)
    (q/ellipse x y circumference circumference)))

(defn draw-paddle! [state]
  (let [{:keys [x y]} (:paddle state)
        {:keys [color width height]} paddle]
    (set-color! color)
    (q/rect x y width height)))

(defn draw-state! [state]
  (clear-screen!)
  (draw-paddle! state)
  (draw-ball! state))

(q/defsketch klarna-dojo-quil
  :host "klarna-dojo-quil"
  :size [(:width screen) (:height screen)]
  :setup setup
  :update update-state
  :draw draw-state!
  :middleware [m/fun-mode])
