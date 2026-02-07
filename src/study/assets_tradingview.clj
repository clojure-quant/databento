(ns study.assets-tradingview
  (:require
   [tablecloth.api :as tc]
   [tech.v3.dataset :as ds]
   [tech.v3.datatype.functional :as dfn]
   [tech.v3.datatype.rolling :as rolling]))

(def ds-nyse
  (->  "./csv/nyse-listed.csv"
       (tc/dataset)
       (tc/rename-columns {"ACT Symbol" :asset
                           "Category" :category
                           "Company Name" :name})
         ;(tc/select-columns [:Symbol :Name :Category])
       ))

(tc/info ds-nyse)
; 2883 stocks

(def ds-signals
  (->  "./signals2018.csv"
       (tc/dataset {:key-fn keyword})
       #_(tc/rename-columns {"ACT Symbol" :asset
                             "Category" :category
                             "Company Name" :name})
         ;(tc/select-columns [:Symbol :Name :Category])
       ))

(tc/info ds-signals)

(def signal-asset-set
  (->> (:asset ds-signals)
       (into #{})))


(def nyse-with-signal
  (-> ds-nyse
      (tc/select-rows (fn [row]
                        (contains? signal-asset-set (:asset row))))))
(spit "tv-assets.edn"
      (pr-str {:assets (->> nyse-with-signal
                            (tc/rows)
                            (map (fn [[asset name]]
                                   {:asset asset
                                    :name name
                                    :category "stock"
                                    :exchange "us"}))
                            (into []))}))

