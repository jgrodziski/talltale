(ns talltale.credit-card)

;; implement the luhn algorithm with Clojure

(defn- checksums
  "returns a vector of two value: the sum of odd and then even digits"
  [ccn-seq]
  (let [odds (filter odd? ccn-seq)
        evens (filter even? ccn-seq)
        odds-sum (reduce + odds)
        evens-sum (reduce + evens)]
    [odds-sum evens-sum]))

(defn check-digit
  "returns the check digit given a sequence of digits representing the credit card number (without the check digit)"
  [ccn-seq]
  (let [[odds-sum evens-sum] (checksums ccn-seq)]
    (mod (* 9 (+ odds-sum evens-sum)) 10)))

(defn valid?
  "takes a seq of digits (string included) and returns whether a credit card number is valid (must include the check-digit number)"
  [ccn-seq]
  (let [[odds-sum evens-sum] (checksums (butlast ccn-seq))]
    (= (mod (+ odds-sum evens-sum) 10) 0)))

(defn credit-card-number
  "generates a new credit card number as a Long value (including its check digit)"
  ([] (credit-card-number 10))
  ([n]
   (let [ccn-seq (map (fn [digit] (rand-int 10)) (range n))
         check-digit (check-digit ccn-seq)]
     (Long/valueOf (apply str (conj ccn-seq check-digit))))))
