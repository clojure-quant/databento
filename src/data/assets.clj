(ns data.assets
  (:require
   [tablecloth.api :as tc]
   [tech.v3.dataset :as ds]
   [tech.v3.datatype.functional :as dfn]
   [tech.v3.datatype.rolling :as rolling]))


(def ds
  (->  "./csv/us_equities_with_class_sector.csv"
       (tc/dataset {:key-fn keyword})
       (tc/select-columns [:Symbol :Name :Category])))


(def dict 
 (zipmap (:Symbol ds) (:Category ds)) 
  )


(defn get-category [symbol]
  (get dict symbol))

(defn add-category [ds]
  (tc/add-column ds :category 
                 (map get-category (:asset ds))))

;(tc/map-rows ds (fn [row] [(:Symbol row) (:Category row)]))

 

(comment
  ds


  (-> ds
      (tc/group-by :Category)
      (tc/aggregate {:c #(count (:Symbol %))}))
;| :$group-name |   :c |
;|--------------|-----:|
;|        Stock | 6504 |
;|          ETF | 1178 |
;|      Warrant |  494 |
  
 (get-category "QQQ")
  (get-category "MSFT")

  ) 