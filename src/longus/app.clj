(ns longus.app
  (:require
   [longus.market :refer [add-trailing-decline add-trailing-decline-signal]]
   [tablecloth.api :as tc]
   [tech.v3.dataset :as ds]
   [tech.v3.datatype.functional :as dfn]
   [tech.v3.datatype.rolling :as rolling]))


(defn load-dataset [path]
  (-> (tc/dataset path {:key-fn keyword})
      (tc/order-by [:asset :date])
      (tc/rename-columns {:symbol2 :asset
                          :ts_event :date})
      (tc/select-columns [:date :asset :open :high :low :close :volume])
      (tc/add-column :turnover #(dfn/* (:close %) (:volume %)))))


(def ds
  (-> "csv/eod-market-mapped.csv"
      (load-dataset)))

ds


(defn filter-liquid-assets [ds min-turnover]
  (let [liquid-set   (-> ds
                         (tc/group-by :asset)
                         (tc/aggregate {:avg-turnover #(dfn/mean (:turnover %))})
                         (tc/select-rows #(> (:avg-turnover %) min-turnover))
                         :$group-name
                         set)]
    (-> ds
        (tc/select-rows #(contains? liquid-set (:asset %)))
        (tc/order-by [:asset :date]))))



(def ds-liquid (filter-liquid-assets ds 1000000.0))

ds-liquid

;; 9000 stocks have less than 1 million usd average turnover.
;; 2500 stocks have more than 1 million usd average turnover.

(-> ds
    (tc/select-rows (fn [row]
                      (= (:asset row) "MSFT")))
    (add-trailing-decline 100))


(-> ds
    (tc/select-rows (fn [row]
                      (= (:asset row) "MSFT")))
    (add-trailing-decline-signal 100 -0.2 50)
    (tc/select-rows #(:signal %))
    ;(tc/select-rows #(:setup %))
    )

(defn select-signal [ds]
  (tc/select-rows ds #(:signal %)))


(defn compute-signals
  "Apply add-trailing-decline-signal to each asset group."
  [ds {:keys [window dd dd-n-min]}]
  (->> (tc/group-by ds :asset)
       (tc/groups->seq)
       ;(take 2)
       (pmap #(add-trailing-decline-signal (tc/as-regular-dataset %) window dd dd-n-min))
       (apply tc/concat)
       (select-signal)))


(-> ds-liquid
    (compute-signals {:window 300 
                      :dd -0.2
                      :dd-n-min 120})
    (tc/write! "signals.csv"))


(def bill-assets #{})



   