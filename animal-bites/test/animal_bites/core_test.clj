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

(deftest group-by-frequencies-test
  (testing "Testing the frequencies helper function"
    (is (= {3 [["dog" 3]], 2 [["cat" 2]], 1 [["bat" 1]]} (group-by-frequencies ["dog" "cat" "cat" "dog" "dog" "bat"])))
    (is (= {1 [["dog" 1]]} (group-by-frequencies ["dog"])))
    (is (= {} (group-by-frequencies [])))
    (is (= {2 [["dog" 2] ["cat" 2] ["bat" 2]]} (group-by-frequencies ["dog" "dog" "cat" "cat" "bat" "bat"])))
    (is (= {1 [[1 1] [-1 1] [4 1] [5 1] [30 1]], 2 [[20 2] [18 2]]} (group-by-frequencies [1 20 -1 4 5 18 18 20 30])))
    (is (= {1 [[1 1]]} (group-by-frequencies [1])))
  ))

(deftest get-most-common-test 
  (testing "Testing most common"
    (is (= ["dog" 3] (get-most-common ["dog" "cat" "cat" "dog" "dog" "bat"])))
    (is (= ["dog" 1] (get-most-common ["dog"])))
    (is (= nil (get-most-common [])))
    ;; (is (= [] (get-most-common []))) ; previous test
    (is (= ["dog" 2, "cat" 2] (get-most-common ["dog" "dog" "cat" "cat" "bat"])))
    (is (= [20 2, 18 2] (get-most-common [1 20 -1 4 5 18 18 20 30 21])))
    (is (= [20 1] (get-most-common [20])))
  ))

(deftest get-least-common-test
  (testing "Testing least common"
    (is (= ["bat" 1] (get-least-common ["dog" "cat" "cat" "dog" "dog" "bat"])))
    (is (= ["dog" 1] (get-least-common ["dog"])))
    (is (= nil (get-least-common [])))
    (is (= ["dog" 2, "cat" 2] (get-least-common ["dog" "bat" "dog" "bat" "cat" "cat" "bat"])))
    (is (= [-1 1, 4 1] (get-least-common [20 -1 4 18 18 20])))
    (is (= [20 1] (get-least-common [20])))
    ))

(deftest column-test
  (testing "Testing the column function"
    ;; testing on "normal" data, with one duplicate column
    (let [data1 [
      ["col1" "col2" "col2"]
      ["1,1" "1,2" "1,3"]
      ["2,1" "2,2" "2,3"]]]
      (is (= ["col1" "1,1" "2,1"] (column data1 "col1")))
      (is (= ["col2" "1,2" "2,2"] (column data1 "col2")))
      (is (= nil (column data1 "not a header"))) ; test for invalid header
    )
    ;; testing on invalid inputs
    (is (= nil (column [] "col1"))) ; test for invalid data (still a vector)
    (is (= nil (column [[][][]] "col1")))
    (is (= nil (column [[1 2][][]] "col1")))
    (is (= [1] (column [[1 2]] 1)))
    (is (= [1 nil nil] (column [[1 2][nil nil][nil nil]] 1)))
    ;; Testing on data with nil value
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
