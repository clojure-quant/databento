(ns longus.bento-loader
  (:require
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

(def ds2018
  (-> "csv/eod-market-2018.csv"
      (load-dataset)))

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

(def ds-liquid-2018 (filter-liquid-assets ds2018 1000000.0))

;(tc/write! ds-liquid "liquid-stocks-bars.csv")

ds-liquid

;; 9000 stocks have less than 1 million usd average turnover.
;; 2500 stocks have more than 1 million usd average turnover.
