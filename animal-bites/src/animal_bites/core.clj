(ns animal-bites.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.data.csv :as csv]))
;; This code is written by Orville Anderson and Harley Hannahs.

(println "In core.clj of animal_bites") ; quick check to see if running in correct file.
(def animal-data 
  (with-open [reader (io/reader "Health_AnimalBites.csv")] ; this requires the data file to be stored at the project-level
    (let [data (csv/read-csv reader)]
      (reduce conj [] data))))

(defn get-most-common
  "Takes a vector of information and returns the most commonly found value. (if none is found...)"
  [v]
  ; sort by values, then grab first item
  (first (sort-by val > (frequencies v)))
)

(println 
  (second animal-data)
  ;; "The types of animals that bit in this dataset are:"
  ;; (frequencies )

)

;; Notes to self: items are stored as strings:
;; (nth (second animal-data) 4)
;; "LIG. BROWN"