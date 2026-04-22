# Function Exercises

## Frequencies

### Explanation

Frequencies takes a collection and returns a map containing the unique items in the collection as keys and the times they appeared as the associated values.

### 3+ Examples

```{Clojure}
    (let ex-vec ["frog" "dog" "cat" "cat" "dog" "dog" "bat"]
    
        ;; example one - basic
        (frequencies ex-vec)
        ;; will return: {"frog" 1, "dog" 3, "cat" 2, "bat" 1}
    
        ;; example two - combined with sorting
        (sort-by val > (frequencies ex-vec))
        ;; will return: (["dog" 3] ["cat" 2] ["frog" 1] ["bat" 1])
    
        ;; example three - edge case
        (frequencies [nil nil 1])
        ;; returns: {nil 2, 1 1}
    )
```

### 1 Exercise (plus 2 tests and a solution)

Given a nested vector and an index, use `frequencies` to get the frequencies of the item at the index in each entry,

Test cases:

```{Clojure}
(let ex-nest-vec [
    ["col1" "col2" "col3"]
    [19 "Brown" false]
    [0.5 "Black" true]
    [20 "Brown" true]]

    (defn fn
        ""
        [nest-vec index]
        ;; write function here
    )

    (is (= {"col2" 1, "Brown" 2, "Black" 1} (fn ex-nest-vec 2)))
    (is (= {"col3" 1, false 1, true 2} (fn ex-nest-vec 3)))
)
```

solution:

```{Clojure}

(defn fn
    ""
    [nest-vec index]
    (frequencies (map #(nth % index) nest-vec))
)

```

## Group-by

### Explaination

Group-by takes a function and a collection and returns a map relating the results of the function (keys) to the elements that provided that outcome (values). If there's more than one related element, they are stored in a vector.

### 3+ Examples

```{Clojure}

    ;; general example
    (group-by pos? [-12 2 4 -13 0 40])
    ;; returns: {false [-12 -13 0], true [2 4 40]}

    ;; complex example involving map
    (group-by val {:a 7, :b 9, :c 7, :d 9 :e 7})
    ;; returns: {7 [[:a 7] [:c 7] [:e 7]], 9 [[:b 9] [:d 9]]}

    ;; edge case
    (group-by count nil)
    ;; returns: {}
```

### 1 Exercise (plus 2 tests and a solution)

Given a map, use `group-by` to group the key value pairs by their values and return a map of the groups in ascending order.

Test cases:

```{Clojure}
(def map1 {"oranges" 27, "lemons" 11, "apples" 42, "peaches" 11})
(def map2 {"parrots" 14, "turtles" 14, "dogs" 14, "hamsters" 84})

    (defn fn
        ""
        [m]
        ;; write function here
    )

    (is (= {11 [["lemons" 11] ["peaches" 11]], 27 [["oranges" 27]], 42 [["apples" 42]]} (fn map1)))
    (is (= {14 [["parrots" 14] ["turtles" 14] ["dogs" 14]], 84 [["hamsters" 84]]} (fn map2)))

```

solution:

```{Clojure}

(defn answer
    ""
    [m]
    (into {} (sort (group-by #(val %) m)))
)

```