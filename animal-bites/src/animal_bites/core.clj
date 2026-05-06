(ns animal-bites.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.data.csv :as csv]))
(import java.time.LocalDate)
(import java.time.format.DateTimeFormatter)
;; This code is written by Orville Anderson and Harley Hannahs.
;; To run this project, simply call lein repl and read the terminal output.
;; Our data file is located within the first level of this repository.

; this requires the data file to be stored at the project-level
(def animal-data 
  (with-open [reader (io/reader "Health_AnimalBites.csv")]
    (let [data (csv/read-csv reader)]
          (reduce conj [] data))))

(defn group-by-frequencies
  "Takes a vector and returns a map with the number of times that each element in the vector occurs (keys)
  and its corresponding elements (values).
  If the vector is empty, the function will return an empty map"
  [v]
  (group-by #(val %) (frequencies v)))
  ;; grouping the keys of mapped occurrences in v into a new map based on the different values of the first map

(defn get-most-common
  "Takes a vector of information and returns the most commonly found value.
  If more than one value appears most often, values will be returned in a vector.
  If the vector is empty, the function will return nil"
  [v]
  (if (empty? v) nil ; if vector is empty, will not calculate max frequency (max doesn't like empty vectors)
    (reduce into [] ; places all strings with the same number of occurrences into a single vector
            ((group-by-frequencies v) (apply max (keys (group-by-frequencies v))))))) ; finds strings in collection with max number of frequencies

(defn get-least-common
  "Takes a vector of information and returns the least commonly found value.
  If more than one value appears least often, values will be returned in a vector.
  If the vector is empty, the function will return nil"
  [v]
  (if (empty? v) nil ; if vector is empty, will not calculate max frequency (max doesn't like empty vectors)
    (reduce into [] ; places all strings with the same number of occurrences into a single vector
            ((group-by-frequencies v) (apply min (keys (group-by-frequencies v))))))) ; finds strings in collection with min number of frequencies

;; can use (take-nth 2 (get-most-common v)) to just get the elements without their number of occurrences
;; ^^ helpful for answering questions

(defn column
  "Takes one column header as a string, and one CSV formatted as nested vectors.
  Returns all information in that column.
  The first row in the CSV should be the headers of the data. 
  Missing values are not accepted." ; missing values should be set to "" or nil.
  [data header] ; we may be able to use destructuring here to split off the first row of data
    ;; this function is written in a way where you could technically have two columns with same header,
    ;; hence the `(first )`...
    (first (for [i (range (count (first data))) ; for i in the number of headings:
      :when (= header (nth (first data) i))] ; when the heading matches the heading passed to func
      (map #(nth % i) data)))) ; take nth item from each row of the data

(defn date-converter
  "Takes a date in a format which starts with `YYYY-MM-DD `.
  Returns the date as a Java Date object"
  [date]
  (let 
    [date-elements (map Integer/parseInt (subvec (str/split date #"[ -]") 0 3))] ; splits on dash and space
    ;; Use subvec here instead of a limit on str/split 
      ;; so the third element is not "DD 00:00:00" and do not get number format exception.
    (if (some nil? date-elements) ; if any values are nil, quit
      nil
      (LocalDate/of (nth date-elements 0) ; Year
                  (nth date-elements 1) ; Month
                  (nth date-elements 2))))) ; Day

;; Initial exploration
(println 
  "\n Columns in animal-data: "
  (first animal-data)
  "\n\n An example of an entry: "
  (second animal-data)
  "\n\n The types of animals that bit in this dataset are:"
  (distinct (rest (column animal-data "SpeciesIDDesc")))
  "\n\t Please note the odd space in the line above,
  thats from NA values which are stored as an empty string.")

;; Rabies Results
;; Get two columns, one with rabies results and one with animal types
;; make new variable that contains only the species in cases that were positive for rabies
(def rabies-results (map #(if (= % "") "UNKNOWN" %) (rest (column animal-data "ResultsIDDesc"))))
(def species (rest (column animal-data "SpeciesIDDesc")))
(def pos-results-species (filter some? 
  (for [x (range (count rabies-results))]
    (if (= (nth rabies-results x) "POSITIVE") (nth species x)))))

;; Type of animal most likely to be caught after a bite?
;; This is similar to the last question where we will filter down the species
;; to only ones that have a value in the DispositionIDDesc column
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
(def reduced-sorted-bite-dates 
                              (sort ; sort in ascending order
                              (filter 
                                      #(and ; filtering condition
                                          (<= 1985 (.getYear %)) ; on or after 1985
                                          (>= 2017 (.getYear %))) ; on or before 2017
                                      bite-dates)))

;; Answering Questions
(println 
  "\n Least common animal to be bitten by?"
  (get-least-common species) ; [SKUNK 1]
  "\n Reflection:"
  "\n This is interesting to think about as skunks are usually known for their spray being their main defense mechanism."
  "\n I suppose that doesn't mean that they aren't also able to utilize other defenses."

  "\n\n What is the species distribution of bites that resulted in positive rabies test results.
  What is the most common animal to result in a positive rabies result?" 
  ;; please note that we are NOT looking for the animal most LIKELY to result in a positive result
  "\n First let's check what positive results are marked as.
  Our initial frequencies (without removing missing values) are: "
  (frequencies (rest (column animal-data "ResultsIDDesc"))) ; {UNKNOWN 1240, NEGATIVE 299,  7460, POSITIVE 4}
  "\n After replacing all missing, unknown and negative values with false, our frequencies are: "
  (frequencies rabies-results) ; {UNKNOWN 8700, NEGATIVE 299, POSITIVE 4}
  "\n Actual results: "
  "\n\t Count: " (count pos-results-species) ; 4
  "\n\t Frequencies: " (frequencies pos-results-species)  ; {BAT 3, DOG 1}
  "\n\t Most common animal to result in a positive rabies result? " (get-most-common pos-results-species) ; [BAT 3]
  "\n Reflection:"
  "\n It's surprising that there were so few rabies tests that actually concluded in a significant result."
  "\n Were there so many results that came back as unknown because not all animals were tested?"
  "\n Or is getting an inconclusive result from a rabies test common?"
  "\n Or is it a mixture of both of these conclusions?"

  "\n\n Type of animal most likely to be caught after a bite?
  For this we need to decide if we want to use `DispositionIDDesc` as a metric of if an animal was caught or `head_sent_date`"
  "\n DispositionIDDesc"
  "\n\t Frequencies: " (frequencies disposition) ; {UNKNOWN 603, RELEASED 912, KILLED 16,  7468, DIED 4} 
  "\n\t Doing some quick math this would give us 932 animals who were caught (912+16+4)"

  "\n head_sent_date"
  "\n\t Count of distinct entries: " (count (distinct head_sent_date)) ; 326
  "\n\t Count of entries (not empty strings)" (count (filter #(not= "" %) head_sent_date)) ; 395
  "\n\t Using this metric, we would have 395 animals who were caught.
        Presumably every animal who had it's head sent in was dead, which does not line up with our findings above."

  "\n\n We chose to answer this question using the DispositionIDDesc variable.
  This is pretty easily modified to use a different condition."
  "\n Count of captured species: " (count captured-species) ; 932
  "\n Distinct captured species: " (distinct captured-species) ; (DOG CAT BAT OTHER)
  "\n Frequencies of captured species: " (frequencies captured-species) ; {DOG 789, CAT 137, BAT 4, OTHER 2}
  "\n Most common caught animal: " (get-most-common captured-species) ; [DOG 789]
  "\n Reflection:"
  "\n I feel like this is the most expected answer out of everything we've seen."
  "\n I wonder if this information is impacted by the area that the data was collected in."
  "\n Does being in an urban area increase the number of dogs that bite or are captured in general?"
  "\n You would need data from both urban and non-urban areas to figure this out."
  "\n Also, you can't simply assume that you can use the victim_zip data entries to answer this question,"
  "\n because people could be visiting from different zip codes and still get bit by local animals."

  "\n\n What date was the most recent bite? " 
  (.toString (first (reverse (sort bite-dates)))) ; 5013-07-15
  "\n What date was the earliest bite? "
  (.toString (first (sort bite-dates))) ; 1952-05-28
  "\n Both of these dates are odd because the dataset is supposed to be from the years 1985-2017!"
  "\n What is earliest date in reduced set? "
  (.toString (first reduced-sorted-bite-dates)) ; 1985-05-05
  "\n What is most recent date in reduced set? "
  (.toString (first (reverse reduced-sorted-bite-dates))) ; 2017-09-07
  "\n There is nothing especially interesting about these two dates
  except for the fact that to get them we needed to filter out some odd dates.")