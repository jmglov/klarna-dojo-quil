(ns klarna-dojo-quil.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def num-rows 25)
(def num-cols 15)
(def block-size 30)
(def empty-block [0 0 0])
(def drop-timer 3)

(def color {:green [255 0 0]})

(defn concatv [xs ys]
  (into [] (concat xs ys)))

(defn make-row
  ([]
   (mapv (constantly empty-block) (range num-cols)))
  ([_]
   (make-row)))

(defn make-rows [n]
  (mapv make-row (range n)))

(def initial-state
  {:countdown drop-timer
   :rows (make-rows num-rows)})

(defn make-piece []
  {:color (:green color)
   :blocks [[7 -1]]})

(defn block-in-piece [piece target-point]
  (if (some (fn [point] (= point target-point)) (:blocks piece))
    (:color piece)
    empty-block))

(defn setup []
  (q/frame-rate 10)
  (q/color-mode :rgb)
  (q/stroke 0 0 0)
  (-> initial-state
      (assoc :piece (make-piece))))

(defn drop-piece [piece]
  (update piece :blocks #(mapv (fn [[x y]] [x (inc y)]) %)))

(defn raise-piece [piece]
  (update piece :blocks #(mapv (fn [[x y]] [x (dec y)]) %)))

(defn anchor-piece [{:keys [piece rows] :as state}]
  (let [piece (raise-piece piece)]
    (-> state
        (assoc :piece (make-piece))
        (assoc :rows (reduce (fn [rows [x y]]
                               (assoc-in rows [y x] (:color piece)))
                             rows
                             (:blocks piece))))))

(defn update-state [state]
  (if (zero? (:countdown state))
    (let [new-state (-> state
                        (update :piece drop-piece)
                        (assoc :countdown drop-timer))]
      (if (some (fn [[_ y]] (= num-rows y)) (get-in new-state [:piece :blocks]))
        (anchor-piece new-state)
        new-state))
    (update state :countdown dec)))

(defn draw-block! [x y]
  (q/rect (* block-size x) (* block-size y) block-size block-size))

(defn draw! [{:keys [piece rows]}]
  (dotimes [x num-cols]
    (dotimes [y num-rows]
      (let [block (get-in rows [y x])
            block (if (= empty-block block)
                    (block-in-piece piece [x y])
                    block)]
        (apply q/fill block)
        (draw-block! x y)))))

(q/defsketch klarna-dojo-quil
  :host "klarna-dojo-quil"
  :size [(* num-cols block-size)
         (* num-rows block-size)]
  :setup setup
  :update update-state
  :draw draw!
  :middleware [m/fun-mode])
