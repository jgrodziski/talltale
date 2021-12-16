(ns talltale.macros
  (:require
   [clojure.spec.gen.alpha :as gen]
   [talltale.samples :as samples])
  (:import [java.util Random]))

(defn raw
  "returns the raw data for the generator, locale is keyword (eg. :en or :fr) and ks is a vector of keys like with get-in"
  [locale ks]
  (get-in samples/data (cons locale ks)))

(defn rand-from-seed [seed]
  (Random. seed))

(defn rand-int-from-seed
  ([seed]
   (rand-int-from-seed seed nil))
  ([seed n]
   (let [rnd (rand-from-seed seed)]
     (if n
       (.nextInt rnd n)
       (.nextInt rnd)))))

(defn rand-data
  ([locale ks]
   (rand-data (Random.) locale ks))
  ([rnd locale ks]
   (let [data (raw locale ks)]
     (if (coll? data)
       (get data (.nextInt rnd (count data)))
       data))))

(defn rand-data-from-seed [seed locale ks]
  (rand-data (rand-from-seed seed) locale ks))

(defn rand-excluding [n excluding]
  (loop [r (rand-int n)]
    (if (excluding r)
      (recur (rand-int n))
      r)))

(defmacro create-map
  [& syms]
  (zipmap (map keyword syms) syms))

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
