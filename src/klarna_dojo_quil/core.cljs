(ns klarna-dojo-quil.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def framerate 30)

(def screen {:width 640
             :height 480
             :color [211 211 211]})

(def scoreboard {:x 0
                 :y 0
                 :color [0 0 255]})

(def paddle {:width 80
             :height 20
             :color [0 0 0]})

(def ball {:circumference 20
           :color [0 0 255]
           :velocity {:dx 2
                      :dy 2}})

(defn setup []
  (q/frame-rate framerate)
  (q/color-mode :rgb)
  {:ball (merge {:x (/ (:width screen) 2)
                 :y (/ (:height screen) 2)}
                (:velocity ball))
   :paddle {:x (- (/ (:width screen) 2) (/ (:width paddle) 2))
            :y (- (:height screen) (:height paddle))}})

(defn assoc-in-if [m truthy? path v]
  (if truthy?
    (assoc-in m path v)
    m))

(defn update-in-if [m truthy? path f]
  (if truthy?
    (update-in m path f)
    m))

(defn bounce-off-left [state]
  (let [{:keys [x dx]} (:ball state)
        radius (/ (:circumference ball) 2)
        min-x radius]
    (-> state
        (assoc-in-if (< x min-x) [:ball :x] (+ min-x (- min-x x)))
        (update-in-if (< x min-x) [:ball :dx] #(* % -1)))))

(defn bounce-off-right [state]
  (let [{:keys [x dx]} (:ball state)
        radius (/ (:circumference ball) 2)
        max-x (- (:width screen) radius)]
    (-> state
        (assoc-in-if (> x max-x) [:ball :x] (- max-x (- x max-x)))
        (update-in-if (> x max-x) [:ball :dx] #(* % -1)))))

(defn bounce-off-top [state]
  (let [{:keys [y dy]} (:ball state)
        radius (/ (:circumference ball) 2)
        min-y radius]
    (-> state
        (assoc-in-if (< y min-y) [:ball :y] (- min-y (- min-y y)))
        (update-in-if (< y min-y) [:ball :dy] #(* % -1)))))

(defn bounce-off-bottom [state]
  (let [{:keys [y dy]} (:ball state)
        radius (/ (:circumference ball) 2)
        max-y (- (:height screen) radius)]
    (-> state
        (assoc-in-if (> y max-y) [:ball :y] (- max-y (- y max-y)))
        (update-in-if (> y max-y) [:ball :dy] #(* % -1)))))

(defn bounce-ball [state]
  (-> state
      bounce-off-left
      bounce-off-right
      bounce-off-top
      bounce-off-bottom))

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

(defn draw-score! [state]
  (set-color! (:color scoreboard))
  (q/text-align :left :top)
  (q/text (pr-str state) (:x scoreboard) (:y scoreboard)))

(defn draw-state! [state]
  (clear-screen!)
  (draw-paddle! state)
  (draw-ball! state)
  (draw-score! state))

(q/defsketch klarna-dojo-quil
  :host "klarna-dojo-quil"
  :size [(:width screen) (:height screen)]
  :setup setup
  :update update-state
  :draw draw-state!
  :middleware [m/fun-mode])
