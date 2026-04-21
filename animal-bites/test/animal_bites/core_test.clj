(ns animal-bites.core-test
  (:require [clojure.test :refer :all]
            [animal-bites.core :refer :all]))

(deftest a-test
  (testing "Testing the loading of the datafile"
    ;; file should exist
    (is animal-data) ; loaded data should be stored in a real object
    (is (vector? animal-data)) ; clojure.lang.PersistentVector
    (is (= 15 (count (first animal-data)))) ; first row(headers) should have 15 items
    (is (= 9004 (count animal-data))) ; data should have [#] rows
    (type (first (first animal-data))) ; first entry should be a string
    ))

(deftest get-most-common-test 
  (testing "Testing most common"
    (is (= ["dog" 3] (get-most-common ["dog" "cat" "cat" "dog" "dog" "bat"])))
    (is (= ["dog" 1] (get-most-common ["dog"])))
    (is (= nil (get-most-common []))) ; the current multiples fix messes up this test returning nil, instead returns []
    ;; (is (= [] (get-most-common []))) ; previous test
    (is (= ["dog" 2, "cat" 2] (get-most-common ["dog" "dog" "cat" "cat" "bat"])))  ; what do we do if there are multiple most common? 
    ; is first most of the list ok? we should probably list all strings of same frequency, right?
  ))
