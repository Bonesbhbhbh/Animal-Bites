(ns animal-bites.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.data.csv :as csv]))
;; This code is written by Orville Anderson (and later Harley Hannahs)

(println "In core.clj of animal_bites") ; quick check to see if running in correct file.
(def animal-data 
  (with-open [reader (io/reader "Health_AnimalBites.csv")] ; this requires the data file to be stored at the project-level
    (let [data (csv/read-csv reader)]
      (reduce conj [] data))))

(defn column
  "Takes a csv formatted as a list of nested vectors and one string associated with a column header (value in first vector).
  Returns all information in that column."
  [data header] ; we may be able to use destructuring here to split off the first row of data
    ;; should really check that the data is not empty and that the header is valid
    ;; this function is written in a way where you could technically have two columns with same header, hence the `(first )`...
    (first (for [i (range (count (first data))) ; for i in the number of headings:
      :when (= header (nth (first data) i))] ; when the heading matches the heading passed to func
      (map #(nth % i) data)))) ; take nth item from data
      
