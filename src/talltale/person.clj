(ns talltale.person
  (:require
   [clojure.string :refer [lower-case]]
   [clojure.test.check.generators :as check-gen]
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]
   [clj-time.core :as t]
   [talltale.utils :refer [create-map]]
   [talltale.address :as address :refer [address address-gen]]
   [talltale.core :refer [raw rand-data generator-from-coll]])
  (:import [java.text DecimalFormat]))

(generator-from-coll :en [:person :first-name-male])
(generator-from-coll :en [:person :first-name-female])
(generator-from-coll :en [:person :last-name-male])
(generator-from-coll :en [:person :last-name-female])

(defn age []
  (rand-int 110))
(defn age-gen []
  (check-gen/large-integer* {:min 18 :max 110}))

(defn date-of-birth [age]
  (t/minus (t/today) (t/years age)))
(defn date-of-birth-gen [age]
  (gen/return (date-of-birth age)))

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
(defn username-gen [first-name last-name]
  (gen/return (username first-name last-name)))

(defn email [locale first-name last-name]
  (str (identifier first-name last-name) "@" (rand-data locale [:person :personal-email])))
(defn email-gen [locale first-name last-name]
  (gen/return (email locale first-name last-name)))

(defn- person-all [locale {:keys [first-name last-name sex] :as specific}]
  (let [age (age)]
    (merge specific {:username (username first-name last-name)
                     :email (email locale first-name last-name)
                     :age age
                     :date-of-birth (date-of-birth age)
                     :address (address locale)})))

(defn person-male
  ([] (person-male :en))
  ([locale] (person-all locale
                        {:first-name (first-name-male locale)
                         :last-name (last-name-male locale)
                         :sex :male})))

(defn person-male-gen
  ([] (person-male-gen :en))
  ([locale] (check-gen/let [first-name (first-name-male-gen locale)
                            last-name (last-name-male-gen locale)
                            sex (gen/return :male)
                            username (username-gen first-name last-name)
                            email (email-gen locale first-name last-name)
                            age (age-gen)
                            date-of-birth (date-of-birth-gen age)
                            address (address-gen locale)]
              (create-map first-name last-name sex username email age date-of-birth address))))

(defn person-female
  ([] (person-female :en))
  ([locale]
   (person-all locale
               {:first-name (first-name-female locale)
                :last-name (last-name-female locale)
                :sex :female})))

(defn person-female-gen
  ([] (person-female-gen :en))
  ([locale] (check-gen/let [first-name (first-name-female-gen locale)
                            last-name (last-name-female-gen locale)
                            sex (gen/return :female)
                            username (username-gen first-name last-name)
                            email (email-gen locale first-name last-name)
                            age (age-gen)
                            date-of-birth (date-of-birth-gen age)
                            address (address-gen locale)]
              (create-map first-name last-name sex username email age date-of-birth address))))

(defn person
  ([] (person :en))
  ([locale] (if (= (rand-int 2) 0)
              (person-male locale)
              (person-female locale))))
(defn person-gen
  ([] (person-gen :en))
  ( [locale] (if (= (rand-int 2) 0)
               (person-male-gen locale)
               (person-female-gen locale))))
