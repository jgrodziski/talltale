(ns talltale.person
  (:require
   [clojure.string :refer [lower-case]]
   [clj-time.core :as t]
   [talltale.address :as address :refer [address]]
   [talltale.core :refer [raw rand-data]])
  (:import [java.text DecimalFormat]))

(defn age []
  (rand-int 101))

(defn date-of-birth [age]
  (t/minus (t/today) (t/years age)))

(defn- identifier [first-name last-name]
  (let [lower-fn (lower-case first-name)
        lower-ln (lower-case last-name)
        generators {:initial-last-name (fn [] (str (subs lower-fn 0 1) lower-ln))
                    :first-dot-last (fn [] (str lower-fn "." lower-ln))
                    :first-number (fn [] (str lower-fn (rand-int 999)))
                    :last (fn [] lower-ln)
                    :first (fn [] lower-fn)}]
    (str ((get (vec (vals generators)) (rand-int (count generators)))))))

(defn username [first-name last-name]
  (identifier first-name last-name))

(defn email [locale first-name last-name]
  (str (identifier first-name last-name) "@" (rand-data locale [:person :personal-email])))

(defn first-name-male
  ([] (first-name-male :en))
  ([locale] (rand-data locale [:person :first-name :male])))

(defn first-name-female
  ([] (first-name-female :en))
  ([locale] (rand-data locale [:person :first-name :female])))

(defn last-name-male
  ([] (last-name-male :en))
  ([locale] (rand-data locale [:person :last-name :male])))

(defn last-name-female
  ([] (last-name-female :en))
  ([locale] (rand-data locale [:person :last-name :female])))

(defn- person-all [locale specific]
  (let [first-name (:first-name specific)
        last-name (:last-name-female specific)
        email (email locale first-name last-name)
        age (age)]
    {:first-name first-name
     :last-name last-name
     :username (username)
     :email email
     :date-of-birth (date-of-birth age)
     :sex :female
     :address (address locale)
     :age age}))

(defn person-male
  ([] (person-male :en))
  ([locale] (person-all locale
                        (first-name-male locale)
                        (last-name-male locale))))

(defn person-female
  ([] (person-female :en))
  ([locale]
   (person-all locale
               (first-name-female locale)
               (last-name-female locale))
   ))

(defn person
  ([] (person :en))
  ([locale] (if (= (rand-int 2) 0)
              (person-male locale)
              (person-female locale))))
