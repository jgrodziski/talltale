# Talltale

Talltale is a Clojure(Script) library that generates fake data, useful for prototyping or load testing. Can be used with [clojure.spec](https://clojure.org/guides/spec) for instance.

[![Clojars Project](https://img.shields.io/clojars/v/talltale.svg)](https://clojars.org/talltale)

Talltale provides traditional Clojure's functions that generates values but also [test.check](https://github.com/clojure/test.check)/[clojure.spec](https://clojure.org/guides/spec) generators (with `-gen` suffix). The date objects used are Java Time objects (version 0.3.0) instead of Joda Time.

I was inspired by the [JFairy lib](https://github.com/Codearte/jfairy) (I actually copied the nice data used by their generators).

1. [Installation](#installation)
2. [Usage](#usage)
3. [All Available Generators](#all-available-generators)
4. [ClojureScript](#clojurescript)

## Installation

Declare the lib dependency in Leiningen:

```clojure
[talltale "0.4.3"]
```

## Usage

Require the core namespace, every generators is merged into the core namespace for convenience.

```clojure
(use '[talltale.core])

(company :fr) 
;; => {:address {:city "Le Perreux-sur-Marne", :postal-code "10000",
;;               :street "Grande Rue", :street-number 1},
;;     :domain "buapel.com", :email "ventes@buapel.com", :full-name "Buapel Ltd",
;;     :identification-number "00000001",
;;     :logo-url "http://via.placeholder.com/350x150?text=Buapel", :name "Buapel",
;;     :org-id "BUAPEL", :phone-number "0100000000", :tld "com", :type "Ltd",
;;     :url "https://www.buapel.com"}

(company);; default is always :en locale 
;; => {:address {:city "Miami", :postal-code "10076", :street "Ford Street",
;;               :street-number 233},
;;     :domain "adapt.com", :email "contact@adapt.com",
;;     :full-name "Adapt Associates", :identification-number "12-0000016",
;;     :logo-url "http://via.placeholder.com/350x150?text=Adapt", :name "Adapt",
;;     :org-id "ADAPT", :phone-number "124-124-0124", :tld "com",
;;     :type "Associates", :url "https://www.adapt.com"}

(person)
;; => {:address {:city "San Francisco", :postal-code "10000",
;;            :street "Summer Place", :street-number 1},
;;  :age 37,
;;  :date-of-birth #object[org.joda.time.LocalDate 0x7d24c344 "1981-01-08"],
;;  :email "shannon@yahoo.com", :first-name "Khloe", :last-name "Shannon",
;;  :phone-number "122-122-122",
;;  :picture-url "https://randomuser.me/api/portraits/women/4.jpg", :sex :female,
;;  :username "khloe"}
(username (first-name) (last-name))
(age);; => 56
(-> age date-of-birth);; => return a LocalDate object on JVM or cljs-time on JS
(first-name :fr);;random considering the gender (frequency 0f 0.5 for both)
(first-name-male :en);;=> "Logan"
(email (first-name) (last-name) );; => 
(picture-url :female)

;;you got the test.check generator version
(gen/sample (person-gen))

(address);; => {:city "San Francisco", :postal-code "10000",
         ;;     :street "Summer Place", :street-number 1}
(address-gen);; => return a Generator
(phone-number);;=> "124-124-0124"
(company-gen);; => return a Generator
(gen/sample (company-gen))
(lorem-ipsum)
(text)

```

Notes: I use test.check generators for numbers, so don't be surprised by the value as they respect the ["Growth and Shrink" manner of test.check](https://github.com/clojure/test.check/blob/master/doc/growth-and-shrinking.md).

### All available generators

```clojure
(keys (ns-publics 'talltale.core))
;; Common
(text)
(lorem-ipsum) 

;; Address
(street-number) 
(street) 
(postal-code) 
(city) 
(phone-number) 
(address) 

;; Company
(org-id) 
(company-name) 
(company-type)
(function)
(email) 
(full-name) 
(company-email) 
(identification-number)
(tld) 
(domain) 
(url) 
(logo-url) 
(company) 

;; Person
(sex) 
(username) 
(first-name) 
(first-name-male) 
(first-name-female) 
(last-name) 
(last-name-male) 
(last-name-female) 
(age) 
(date-of-birth) 
(position)
(picture-url) 
(person-male) 
(person-female) 
(person) 

;;various generator

(quality)
;; => "robust" "unreal" etc.
(shape)
;; => "circular" "oval"
(color)
;; => "blue" "rose"
(animal)
;; => "horse" "koala" 
(landform)
;; => "moulin" "cave" 

```

Remember that you can just add the `-gen` suffix to get a test.check/clojure.spec generator and then use it with `gen/sample`.

Also each function/generator accept a locale as first argument with `:en` and `:fr` as the first ones provided.

## ClojureScript

If you want to try Talltale with [Planck](http://planck-repl.org/):
```bash
planck -D talltale:0.2.11,com.andrewmcveigh/cljs-time:0.5.2,org.clojure/test.check:0.10.0-alpha2
```
then
```clojure
(require 'talltale.core)
(in-ns 'talltale.core)
```

## License

Copyright © 2019 Jeremie Grodziski 

Distributed under the Eclipse Public License either version 1.0.
