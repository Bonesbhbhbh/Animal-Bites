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
  "Takes a vector of information and returns the most commonly found value.
  If more than one value appears most often, values will be returned in a vector.
  If the vector is empty, the function will return nil"
  [v]
  (if (empty? v) 
    nil ; if vector is empty, will not calculate max (max doesn't like empty vectors)
    (reduce 
      into 
      []
      ;; This next bit is a odd sequence of the most common word and the count of time it occurs.
      ;; It is formatted this way to allow for cases where there are ties for most common item.
      ((group-by #(val %) (frequencies v))
        (apply max (keys (group-by #(val %) (frequencies v)))))))) ; checks the collection for strings with the max number of occurrences
  ; places all strings with the same number of occurrences into a single vector

(println 
  (second animal-data)
  ;; "The types of animals that bit in this dataset are:"
  ;; (frequencies )

)

;; Notes to self: items are stored as strings:
;; (nth (second animal-data) 4)
;; "LIG. BROWN"
(defn column
  "Takes one column header as a string, and one CSV formatted as nested vectors.
  Returns all information in that column.
  The first row in the CSV should be the headers of the data. 
  Missing values are not accepted." ; missing values should be set to "" or nil.
  [data header] ; we may be able to use destructuring here to split off the first row of data
    ;; this function is written in a way where you could technically have two columns with same header, hence the `(first )`...
    (first (for [i (range (count (first data))) ; for i in the number of headings:
      :when (= header (nth (first data) i))] ; when the heading matches the heading passed to func
      (map #(nth % i) data)))) ; take nth item from each row of the data
      
;; initial exploration
(println 
  "Columns in animal-data: "
  (first animal-data)
  "\nAn example of an entry"
  (second animal-data)
  "\nThe types of animals that bit in this dataset are:"
  (distinct (rest (column animal-data "SpeciesIDDesc")))
  "\nPlease note the odd space in the line above, thats from NA values, its actually an empty string.")

;; Answering Questions
(println 
  "\nLeast common animal to be bitten by?"
  ;; Harley is working on this code in another branch
  "\nMost common animal to result in a positive rabies result?"
  ;; get two columns, one with rabies results and one with animal types
  "\n First let's check what positive results are marked as:"
  (distinct (rest (column animal-data "ResultsIDDesc")))
  "\n Actual q2-results: " ; (frequencies q2-results)
  (def rabies-results (map #(if (= % "POSITIVE") % false) (rest (column animal-data "ResultsIDDesc"))))
  (def species (rest (column animal-data "SpeciesIDDesc")))
  (for [x (range (count rabies-results))]
    (if (nth rabies-results x) (nth species x))) ; returns nil a whole bunch of times

  "\nType of animal most likely to be caught after a bite?")