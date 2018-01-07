(ns talltale.company
  (:require [clj-time.core :as t]
            [clojure.string :as str :refer [lower-case upper-case]]
            [clojure.test.check.generators :as check-gen]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [talltale.address :refer [address address-gen]]
            [talltale.utils :refer [create-map]]
            [talltale.core :refer [rand-data rand-excluding raw generator-from-coll]])
  (:import [clojure.test.check.generators Generator]))

(generator-from-coll :en [:company :name])
(generator-from-coll :en [:company :type])
(generator-from-coll :en [:company :tld])

(defn type
  ([] (type :en))
  ([locale] (rand-data locale [:company :type])))

(defn full-name
  [name type] (str name " " type))
(defn full-name-gen [name type] (gen/return (full-name name type)))

(defn domain
  [name tld] (str (lower-case name) "." tld))
(defn domain-gen [name tld] (gen/return (domain name tld)))

(defn url
  [domain] (str "https://www." domain))
(defn url-gen [domain] (gen/return (url domain)))

(defn logo-url
  [name]
  (str "http://via.placeholder.com/350x150?text=" name))
(defn logo-url-gen [name] (gen/return (logo-url name)))

(defn email
  ([domain] (email :en domain))
  ([locale domain] (str (rand-data locale [:company :email]) "@" domain )))
(defn email-gen
  ([domain] (email-gen :en))
  ([locale domain] (gen/return (email locale domain))))

(defn in-en
  ([](in-en (rand-int 9999)))
  ([rand]
   (let [serial (format "%07d" rand)
         area-excluding-numbers #{7, 8, 9, 17, 18, 19, 28, 29, 41, 47, 49, 69, 70, 79, 89, 96, 97}
         area (format "%02d" (rand-excluding 99 area-excluding-numbers))]
     (str area "-" serial))))

(defn in-fr
  ([] (in-fr (rand-int 99999999)))
  ([rand]
   (format "%08d" rand)))

(defn identification-number
  ([] (in-en))
  ([locale] (case locale
              :en (in-en)
              :fr (in-fr)
              (in-en)))
  ([locale rand] (case locale
                   :en (in-en rand)
                   :fr (in-fr rand)
                   (in-en rand))))
(defn identification-number-gen
  ([] (identification-number-gen :en))
  ([locale] (case locale
              :en (gen/fmap in-en (check-gen/large-integer* {:min 1 :max 9999}))
              :fr (gen/fmap in-fr (check-gen/large-integer* {:min 1 :max 99999999})))))

(defn org-id [name]
  (upper-case (str/replace name #" " "")))
(defn org-id-gen [name]
  (gen/return (org-id name)))

(defn company
  ([] (company :en))
  ([locale]
   (let [name (name locale)
         type (type locale)
         full-name (full-name locale name type)
         domain (domain name (tld locale))
         url (url locale domain)
         email (email locale domain)]
     {:org-id (org-id name)
      :name name
      :full-name full-name
      :identification-number (in locale)
      :domain domain
      :url url
      :logo-url (logo-url url)
      :type type
      :email email
      :address (address locale)})))

(defn company-gen
  ([] (company-gen :en))
  ([locale]
   (check-gen/let [name (name-gen)
                   org-id (org-id-gen name)
                   type (type-gen)
                   identification-number (identification-number-gen locale)
                   full-name (full-name-gen name type)
                   tld (tld-gen)
                   domain (domain-gen name tld)
                   url (url-gen domain)
                   logo-url (logo-url-gen name)
                   email (email-gen locale domain)
                   address (address-gen locale)]
     (create-map name org-id type identification-number full-name tld domain url logo-url email address))))
