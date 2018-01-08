# talltale

Talltale is a Clojure library that generates fake data, useful for prototyping and performs things like load testing used with clojure.spec for instance.
Talltale provides traditional Clojure's functions that generates a new value but also test.check/clojure.spec generators (with -gen suffix).

## Installation

Declare the lib dependency in Leiningen
[talltale "0.1.0"]

## Usage

Require the core namespace, every generators is merged into the core namespace for convenience.

```clojure
(use '[talltale.core])

(person :fr) 
;; => {:address {:city "Le Perreux-sur-Marne", :postal-code "10000",
;;            :street "Grande Rue", :street-number 1},
;;  :domain "buapel.com", :email "ventes@buapel.com", :full-name "Buapel Ltd",
;;  :identification-number "00000001",
;;  :logo-url "http://via.placeholder.com/350x150?text=Buapel", :name "Buapel",
;;  :org-id "BUAPEL", :phone-number "0100000000", :tld "com", :type "Ltd",
;;  :url "https://www.buapel.com"}

(person);; default is always :en locale 
;; => {:address {:city "Miami", :postal-code "10076", :street "Ford Street",
;;            :street-number 233},
;;  :domain "adapt.com", :email "contact@adapt.com",
;;  :full-name "Adapt Associates", :identification-number "12-0000016",
;;  :logo-url "http://via.placeholder.com/350x150?text=Adapt", :name "Adapt",
;;  :org-id "ADAPT", :phone-number "124-124-0124", :tld "com",
;;  :type "Associates", :url "https://www.adapt.com"}

;;you got the test.check generator version
(gen/sample (person-gen))

(address)
(address-gen)
(phone-number);;=> "124-124-0124"
(company)
(company-gen)
(lorem-ipsum)
(text)

```

## BDD usage
Talltale can be used along (Scenari.io)[https://scenari.io] to generates data in BDD scenarios.

## License

Copyright © 2018 Jeremie Grodziski 

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
