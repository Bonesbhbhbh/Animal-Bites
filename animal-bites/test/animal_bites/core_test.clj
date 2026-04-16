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
