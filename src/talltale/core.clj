(ns talltale.core
  (:require 
            [clojure.java.io :refer [resource]]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [talltale.samples :as samples])
  (:import [java.text DecimalFormat]))


(defn raw
  "returns the raw data for the generator, locale is keyword (eg. :en or :fr) and ks is a vector of keys like with get-in"
  [locale ks]
  (get-in samples/data (cons locale ks)))

(defn rand-data [locale ks]
  (let [data (raw locale ks)]
    (if (coll? data)
      (get data (rand-int (count data)))
      data)))

(defmacro generator-from-coll [default-locale ks]
  (let [name# (symbol (subs (str (last ks)) 1))
        namegen# (symbol (str name# "-gen"))]
    `(do 
      (defn ~name#
         ([] (~name# ~default-locale))
         ([locale#] (rand-data locale# ~ks)))
      (defn ~namegen#
         ([] (~namegen# ~default-locale))
         ([locale#] (gen/elements (raw locale# ~ks)))))))

(do
  (defn fn1
    ([] (prn "fn1"))
    ([x] (prn "fn1" x)))
  (defn fn2 [] (prn "fn2")))


(defn rand-excluding [n excluding]
  (loop [r (rand-int n)]
    (if (excluding r)
      (recur (rand-int n))
      r)))

(defn phone-number
  ([] (phone-number :en))
  ([locale]
   (let [df (rand-data locale [:phone-number-format])]
     (case locale
       :en (format df (rand-int 999) (rand-int 999) (rand-int 999))
       :fr (format df (rand-int 999999999))
       (format "%010d" (rand-int 9999999999))))))

(defn lorem-ipsum []
  (rand-data :en [:lorem-ipsum]))

(defn text
  ([] (text :en))
  ([locale] (rand-data locale [:text])))
