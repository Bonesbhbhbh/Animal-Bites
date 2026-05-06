# Animal-Bites

Final Project for CSci 2601
By Harley Hannahs and Orville Anderson.

The function `date-converter` is set to only take dates as strings formatted as YYYY-MM-DD with a space or dash following.
To modify this function for better handling of illegal input values change it to be:

```{Clojure}
(defn date-converter
  "Takes a date in a format which starts with `YYYY-MM-DD `.
  Returns the date as a Java Date object"
  [date]
  (let 
    [date-elements (str/split date #"[ -]") ; split on space or dash
    formatted-date (if (>= (count date-elements) 3)
                        (map Integer/parseInt (subvec date-elements 0 3)) ;; Use subvec here instead of a limit on str/split so the third element is not "DD 00:00:00" and do not get number format exception.
                        nil)] ; this will make it so empty and partial strings can return nil
    (if (nil? formatted-date) ; check for illegal arguments
      nil
      (LocalDate/of (nth formatted-date 0) ; Year
                  (nth formatted-date 1) ; Month
                  (nth formatted-date 2))))) ; Day
```

With this adjustments the following statements will return true:

```{Clojure}
(nil? (date-converter ""))
(nil? (date-converter "3000-1"))
(nil? (date-converter "300-1-"))
```

This code is not implemented because it serves us better to universally return an error with an illegal input instead of sometimes and error and sometimes nil. To fully implement restrictions on the input it would be best to also check that the first three values in date-elements are numbers. That would be a significantly more complex implementation than our one-line fix of removing empty strings when we define bite-dates.
