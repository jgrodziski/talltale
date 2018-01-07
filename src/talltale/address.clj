(ns talltale.address
  (:require
   [clojure.string :refer [lower-case]]
   [clojure.test.check.generators :as check-gen]
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]
   [clj-time.core :as t]
   [talltale.utils :refer [create-map]]
   [talltale.core :refer [raw rand-data generator-from-coll]])
  (:import [java.text DecimalFormat]))

(defn street-number []
  (rand-int 1000))

(defn street-number-gen []
  (check-gen/large-integer* {:min 1 :max 1000}))

(generator-from-coll :en [:address :street])

(defn postal-code
  ([] (postal-code :en))
  ([locale]
   (postal-code locale (rand-int 99999)) )
  ([locale rand]
   (let [df (DecimalFormat.(rand-data locale [:address :postal-code]))]
     (.format df rand))))

(defn postal-code-gen
  ([] (postal-code-gen :en))
  ([locale] (gen/fmap (partial postal-code locale) (check-gen/large-integer* {:min 10000 :max 99999}))))

(generator-from-coll :en [:address :city])

(defn address
  ([] (address :en))
  ([locale]
   {:street (street locale)
    :street-number (street-number) 
    :postal-code (postal-code locale)
    :city (city locale)}))

(defn address-gen
  ([] (address-gen :en))
  ([locale] (check-gen/let [street (street-gen locale)
                            postal-code (postal-code-gen locale)
                            city (city-gen locale)
                            street-number (street-number-gen)]
              (create-map street street-number postal-code city))))
