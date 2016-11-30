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
             :speed 10
             :color [0 0 0]})

(def ball {:radius 10
           :color [0 0 255]
           :velocity {:dx 5
                      :dy 5}})

(def pressed-key (atom nil))

(def keycode-right 39)
(def keycode-left 37)

(defn install-event-handlers! []
  (.addEventListener js/window
                     "keydown"
                     (fn [ev] (reset! pressed-key (.-keyCode ev))))
  (.addEventListener js/window
                     "keyup"
                     (fn [_] (reset! pressed-key nil))))

(defn setup []
  (install-event-handlers!)
  (q/frame-rate framerate)
  (q/color-mode :rgb)
  {:ball (merge {:x (/ (:width screen) 2)
                 :y (/ (:height screen) 2)}
                (:velocity ball))
   :paddle {:x (- (/ (:width screen) 2) (/ (:width paddle) 2))
            :y (- (:height screen) (:height paddle))}})

(defn move-left [x]
  (let [new-x (- x (:speed paddle))
        min-x 0]
    (if (< new-x min-x)
      min-x
      new-x)))

(defn move-right [x]
  (let [new-x (+ x (:speed paddle))
        max-x (- (:width screen) (:width paddle))]
    (if (> new-x max-x)
      max-x
      new-x)))

(defn move-paddle [state]
  (cond
    (= @pressed-key keycode-left)
    (update-in state [:paddle :x] move-left)

    (= @pressed-key keycode-right)
    (update-in state [:paddle :x] move-right)

    :default
    state))

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
        min-x (:radius ball)]
    (-> state
        (assoc-in-if (< x min-x) [:ball :x] (+ min-x (- min-x x)))
        (update-in-if (< x min-x) [:ball :dx] #(* % -1)))))

(defn bounce-off-right [state]
  (let [{:keys [x dx]} (:ball state)
        max-x (- (:width screen) (:radius ball))]
    (-> state
        (assoc-in-if (> x max-x) [:ball :x] (- max-x (- x max-x)))
        (update-in-if (> x max-x) [:ball :dx] #(* % -1)))))

(defn bounce-off-top [state]
  (let [{:keys [y dy]} (:ball state)
        min-y (:radius ball)]
    (-> state
        (assoc-in-if (< y min-y) [:ball :y] (- min-y (- min-y y)))
        (update-in-if (< y min-y) [:ball :dy] #(* % -1)))))

(defn bounce-off-paddle [state]
  (let [{ball-x :x, ball-y :y, dy :dy} (:ball state)
        {paddle-x :x, paddle-y :y} (:paddle state)
        max-y (- paddle-y (:radius ball))
        min-x (- paddle-x (/ (:radius ball) 2))
        max-x (+ paddle-x (:width paddle) (/ (:radius ball) 2))
        collision? (and (> ball-y max-y)
                        (> ball-x min-x)
                        (< ball-x max-x))]
    (-> state
        (assoc-in-if collision? [:ball :y] (- max-y (- max-y ball-y)))
        (update-in-if collision? [:ball :dy] #(* % -1)))))

(defn bounce-ball [state]
  (-> state
      bounce-off-left
      bounce-off-right
      bounce-off-top
      bounce-off-paddle))

(defn handle-out-of-bounds [state]
  (let [max-y (+ (:height screen) (:radius ball))]
    (if (> (get-in state [:ball :y]) max-y)
      (assoc state :game-over? true)
      state)))

(defn update-state [state]
  (let [{:keys [dx dy]} (:ball state)]
    (-> state
        (update-in [:ball :x] #(+ % dx))
        (update-in [:ball :y] #(+ % dy))
        move-paddle
        bounce-ball
        handle-out-of-bounds)))

(defn clear-screen! []
  (apply q/background (:color screen)))

(defn set-color! [[r g b]]
  (q/fill r g b)
  (q/stroke r g b))

(defn draw-ball! [state]
  (let [{:keys [x y]} (:ball state)
        {:keys [radius color]} ball
        circumference (* 2 radius)]
    (set-color! color)
    (q/ellipse x y circumference circumference)))

(defn draw-paddle! [state]
  (let [{:keys [x y]} (:paddle state)
        {:keys [color width height]} paddle]
    (set-color! color)
    (q/rect x y width height)))

(defn draw-score! [state]
  (let [text (if (:game-over? state)
               "GAME OVER!"
               (-> state
                   (assoc :pressed-key @pressed-key)
                   pr-str))]
    (set-color! (:color scoreboard))
    (q/text-align :left :top)
    (q/text text (:x scoreboard) (:y scoreboard))))

(defn draw-state! [state]
  (clear-screen!)
  (draw-paddle! state)
  (draw-ball! state)
  (draw-score! state)
  (when (:game-over? state)
    (q/no-loop)))

(q/defsketch klarna-dojo-quil
  :host "klarna-dojo-quil"
  :size [(:width screen) (:height screen)]
  :setup setup
  :update update-state
  :draw draw-state!
  :middleware [m/fun-mode])
