(ns talltale.core
  (:require 
   [clojure.string :as str :refer [lower-case upper-case]]
   [clojure.pprint :refer [cl-format]]
   [clj-time.core :as t]
   [clojure.test.check.generators :as check-gen]
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]
   [clojure.java.io :refer [resource]]
   [talltale.utils :refer [create-map]]
   [talltale.samples :as samples])
  )

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

(defn rand-excluding [n excluding]
  (loop [r (rand-int n)]
    (if (excluding r)
      (recur (rand-int n))
      r)))


(defn lorem-ipsum []
  (rand-data :en [:lorem-ipsum]))
(defn lorem-ipsum-gen ([] (lorem-ipsum-gen :en))
  ([locale] (check-gen/return (raw locale [:lorem-ipsum]))))

(defn text
  ([] (text :en))
  ([locale] (rand-data locale [:text])))
(defn text-gen
  ([](text-gen :en))
  ([locale] (check-gen/return (rand-data locale [:text]))))

(load "address")
(load "company")
(load "person")
