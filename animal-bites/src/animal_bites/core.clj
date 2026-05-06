(ns animal-bites.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.data.csv :as csv]))
(import java.time.LocalDate)
(import java.time.format.DateTimeFormatter)
;; This code is written by Orville Anderson and Harley Hannahs.

(def animal-data 
  (with-open [reader (io/reader "Health_AnimalBites.csv")] ; this requires the data file to be stored at the project-level
    (let [data (csv/read-csv reader)]
          (reduce conj [] data))))

(defn group-by-frequencies
  "Takes a vector and returns a map with the number of times that each element in the vector occurs (keys) and its corresponding elements (values). If the vector is empty, the function will return an empty map"
  [v]
  (group-by #(val %) (frequencies v)))
  ;; grouping the keys of mapped occurrences in v into a new map based on the different values of the first map

(defn get-most-common
  "Takes a vector of information and returns the most commonly found value.
  If more than one value appears most often, values will be returned in a vector.
  If the vector is empty, the function will return nil"
  [v]
  ; sort by values, then grab first item
  ;; (first (sort-by val > (frequencies v)))  ; original function
  (if (empty? v) nil ; if vector is empty, will not calculate max frequency (max doesn't like empty vectors)
    (reduce into [] ; places all strings with the same number of occurrences into a single vector
            ((group-by-frequencies v) (apply max (keys (group-by-frequencies v)))))))  ; finds strings in collection with max number of frequencies

(defn get-least-common
  "Takes a vector of information and returns the least commonly found value. If more than one value appears least often, values will be returned in a vector. If the vector is empty, the function will return nil"
  [v]
  (if (empty? v) nil ; if vector is empty, will not calculate max frequency (max doesn't like empty vectors)
    (reduce into [] ; places all strings with the same number of occurrences into a single vector
            ((group-by-frequencies v) (apply min (keys (group-by-frequencies v))))))) ; finds strings in collection with min number of frequencies

;; can use (take-nth 2 (get-most-common v)) to just get the elements without their number of occurrences
;; ^^ helpful for answering questions

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

(defn date-converter
  "Takes a date in a format which starts with `YYYY-MM-DD `.
  Returns the date as a Java Date object"
  [date]
  (let 
    [date-elements (map Integer/parseInt (subvec (str/split date #"[ -]") 0 3))] ; splits on dash and space
    ;; Use subvec here instead of a limit on str/split so the third element is not "DD 00:00:00" and do not get number format exception.
    (if (some nil? date-elements) ; if any values are nil, quit
      nil
      (LocalDate/of (nth date-elements 0) ; Year
                  (nth date-elements 1) ; Month
                  (nth date-elements 2))))) ; Day

;; initial exploration
(println 
  "\n Columns in animal-data: "
  (first animal-data)
  "\n\n An example of an entry: "
  (second animal-data)
  "\n\n The types of animals that bit in this dataset are:"
  (distinct (rest (column animal-data "SpeciesIDDesc")))
  "\n\t Please note the odd space in the line above, thats from NA values which are stored as an empty string.")

;; get two columns, one with rabies results and one with animal types
;; make new variable that contains only the species in cases that were positive for rabies
(def rabies-results (map #(if (= % "") "UNKNOWN" %) (rest (column animal-data "ResultsIDDesc"))))
(def species (rest (column animal-data "SpeciesIDDesc")))
(def pos-results-species (filter some? 
  (for [x (range (count rabies-results))]
    (if (= (nth rabies-results x) "POSITIVE") (nth species x)))))

;; Type of animal most likely to be caught after a bite?
;; This is similar to the last question where we will filter down the species to only ones that have a value in the -- column
(def disposition (rest (column animal-data "DispositionIDDesc")))
(def head_sent_date (rest (column animal-data "head_sent_date")))
(def captured-species (filter some?
  (for [x (range (count disposition))]
      (if (contains? #{"RELEASED" "KILLED" "DIED"} (nth disposition x)) (nth species x)))))

;; Most Recent Bite
(def bite-dates
              (->> (column animal-data "bite_date")
                (rest) ; remove column header
                (filter #(not= "" %) ) ; remove empty strings
                (map date-converter ))) ; convert to dates

;; Answering Questions
(println 
  "\n Least common animal to be bitten by?"
  ;; (take-nth 2 (get-least-common species))
  (get-least-common species)) ; [SKUNK 1]

(println 
  "\n What is the species distribution of bites that resulted in positive rabies test results.
  What is the most common animal to result in a positive rabies result?" 
  ;; please note that we are not looking for the animal most LIKELY to result in a positive result
  "\n First let's check what positive results are marked as.
  Our initial frequencies (without removing missing values) are: "
  (frequencies (rest (column animal-data "ResultsIDDesc")))
  "\n After replacing all missing, unknown and negative values with false, our frequencies are: "
  (frequencies rabies-results)
  "\n Actual results: "
  "\n\t Count: " (count pos-results-species) ; {UNKNOWN 603, RELEASED 912, KILLED 16, "" 7468, DIED 4}
  "\n\t Frequencies: " (frequencies pos-results-species)
  "\n\t Most common animal to result in a positive rabies result? " (get-most-common pos-results-species))

(println 
  "\n Type of animal most likely to be caught after a bite?
  We need to decide if we want to use `DispositionIDDesc` as a metric of if an animal was caught or `head_sent_date`"
  "\n DispositionIDDesc"
  "\n\t Frequencies: " (frequencies disposition)
  "\n\t Doing some quick math this would give us 932 animals who were caught (912+16+4)"

  "\n head_sent_date"
  "\n\t Count of distinct entries: " (count (distinct head_sent_date))
  "\n\t Count of entries (not empty strings)" (count (filter #(not= "" %) head_sent_date))
  "\n\t Using this metric, we would have 395 animals who were caught.
        Presumably every animal who had it's head sent in was dead, which does not line up with our findings above."

  "\n\n I am choosing to answer this question using the DispositionIDDesc variable.
  This is pretty easily modified to use a different condition."
  "\n Count of captured species: " (count captured-species)
  "\n Distinct captured species: " (distinct captured-species)
  "\n Frequencies of captured species: " (frequencies captured-species)
  "\n Most common caught animal: " (get-most-common captured-species))

(println 
  "\n What date was the most recent bite? "
  (.toString (first (reverse (sort bite-dates))))
  "\n What date was the earliest bite? "
  (.toString (first (sort bite-dates)))
  "\n Both of these dates are odd because the set is supposed to be from the years 1985-2017!")
  (def reduced-sorted-bite-dates (sort (filter #(and (<= 1985 (.getYear %)) (>= 2017 (.getYear %))) bite-dates))) ; sorted in ascending order
(println 
  "\n What is earliest date in reduced set? " (.toString (first reduced-sorted-bite-dates))
  "\n What is most recent date in reduced set? " (.toString (first (reverse reduced-sorted-bite-dates))))