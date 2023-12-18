(ns talltale.rand)

(defn rand-in [min max]
  (let [rnd (java.util.Random.)]
    (when (< min max)
      (.getAsLong (.findFirst (.longs rnd min max))))))
