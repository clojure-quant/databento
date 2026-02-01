(ns longus.market
  (:require
   [tablecloth.api :as tc]
   [tech.v3.dataset :as ds]
   [tech.v3.datatype.functional :as dfn]
   [tech.v3.datatype.rolling :as rolling]))

(defn bars-since-high
  "Returns [high bars-since-high] for the last element.
  col -> numeric column (vector / techml column / buffer)
  n   -> length to scan (usually (count col))"
  [n col]
  (loop [i 0
         last-idx 0
         maxv Double/NEGATIVE_INFINITY]
    (if (= i n)
      ;; finished → compute result
      [maxv (- (dec n) last-idx)]
      ; not finished -> recur
      (let [v (double (col i))]
        (if (> v maxv)
          (recur (inc i) i v)
          (recur (inc i) last-idx maxv))))))

(defn split-high-ago
  "col -> column of [high ago]
   returns {:high double-array :ago int-array}"
  [col]
  (let [n (count col)
        highs (double-array n)
        agos  (int-array n)]
    (dotimes [i n]
      (let [[h a] (col i)]
        (aset-double highs i (double h))
        (aset-int agos i (int a))))
    {:trailing-high highs
     :trailing-ago  agos}))

(defn add-trailing-decline [asset-ds n]
  (let [{:keys [high low close]} asset-ds
        trailing-high-count (rolling/fixed-rolling-window high n (partial bars-since-high n) {:relative-window-position :left})
        result (split-high-ago trailing-high-count)
        prct-decline (dfn/- (dfn// low (:trailing-high result)) 1.0)
        trailing-max-decline (rolling/fixed-rolling-window prct-decline n #(apply dfn/min %) {:relative-window-position :left})]
    (-> asset-ds
        (tc/add-columns
         (assoc result :prct-decline prct-decline
                :trailing-max-decline trailing-max-decline)))))



(defn add-trailing-decline-signal [bar-ds n max-decline min-n]
  (let [ds (add-trailing-decline bar-ds n)
        {:keys [prct-decline trailing-ago trailing-max-decline]} ds
        c1 (dfn/>  trailing-ago min-n)
        c2 (dfn/>  trailing-max-decline max-decline)
        c3 (dfn/>  prct-decline -0.02)]
    (-> ds
        (tc/add-column :signal  (dfn/and (dfn/and c1 c2) c3)))))




(comment
  (bars-since-high 6 [0 1 2 1 0 1])
  ((partial bars-since-high 6)  [0 1 2 1 0 1])
  (split-high-ago [[1 2] [3 4] [4 6]]))



