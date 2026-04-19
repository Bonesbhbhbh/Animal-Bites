(ns animal-bites.core-test
  (:require [clojure.test :refer :all]
            [animal-bites.core :refer :all]))

(deftest loading-test
  (testing "Testing the loading of the datafile"
    ;; file should exist
    (is animal-data) ; loaded data should be stored in a real object
    (is (vector? animal-data)) ; clojure.lang.PersistentVector
    (is (= 15 (count (first animal-data)))) ; first row(headers) should have 15 items
    (is (= 9004 (count animal-data))) ; data should have [#] rows
    (type (first (first animal-data))) ; first entry should be a string
    ))

(deftest column-test
  (testing "Testing the column function"
    (let [data1 [
      ["col1" "col2" "col3"]
      ["1,1" "1,2" "1,3"]
      ["2,1" "2,2" "2,3"]]]
      (is (= ["col1" "1,1" "2,1"] (column data1 "col1")))
      (is (= nil (column data1 "not a header")))
    )
    
    (is (= nil (column [[][][]] "col1")))
    (is (= nil (column [[1 2][][]] "col1")))
    (is (= [1] (column [[1 2]] 1)))
    (is (= [1 nil nil] (column [[1 2][nil nil][nil nil]] 1)))
    
    (let [data2 [
      ["normal" "sparse" "col3"]
      ["n1" 1 "1,3"]
      ["n2" nil "2,3"]
      ["n3" 0 "3,3"]]]
      (is (= ["normal" "n1" "n2" "n3"] (column data2 "normal")))
      (is (= ["sparse" 1 nil 0] (column data2 "sparse")))
      (is (= ["col3" "1,3" "2,3" "3,3"] (column data2 "col3")))
    )
  )
)
