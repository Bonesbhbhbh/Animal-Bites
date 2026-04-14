(ns animal-bites.core-test
  (:require [clojure.test :refer :all]
            [animal-bites.core :refer :all]))

(deftest a-test
  (testing "Testing the loading of the datafile"
    ;; file should exist
    (is animal-data) ; loaded data should be stored in a real objec
    (is (vector? animal-data)) ; clojure.lang.PersistentVector
    ;; data should have [#] of columns (ie [#] of items in first array/row)
    ;; data should have [#] rows
    ;; data type should be strings
    ))
