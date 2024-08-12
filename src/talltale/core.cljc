(ns talltale.core
  (:require
   [clojure.string :as str :refer [lower-case upper-case]]
   [clojure.pprint :refer [cl-format]]
   [clojure.test.check.generators :as check-gen]
   [clojure.spec.gen.alpha :as gen]
   #?(:cljs [cljs-time.core :as time])
   #?(:cljs [cljs-time.coerce :as time-coerce])
   #?(:cljs [talltale.macros :refer [raw rand-data rand-data-from-seed rand-excluding] :refer-macros [create-map generator-from-coll]])
   #?(:clj [talltale.macros :refer [create-map generator-from-coll raw rand-data rand-data-from-seed rand-excluding]]))
  (:import [java.time LocalDate Instant]))

(defn lorem-ipsum []
  (rand-data :en [:lorem-ipsum]))
(defn lorem-ipsum-gen ([] (lorem-ipsum-gen :en))
  ([locale] (check-gen/return (raw locale [:lorem-ipsum]))))

(defn text
  ([] (text :en))
  ([locale] (rand-data locale [:text])))
(defn text-gen
  ([] (text-gen :en))
  ([locale] (check-gen/return (rand-data locale [:text]))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                              ;;
;;                 ADDRESS STUFF                                ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn street-number []
  (rand-int 1000))

(defn street-number-gen []
  (check-gen/large-integer* {:min 1 :max 1000}))

(generator-from-coll :en [:address :street])

(defn postal-code
  ([] (postal-code :en))
  ([locale]
   (postal-code locale (rand-int 99999)))
  ([locale rand]
   (let [control-string (rand-data locale [:address :postal-code])]
     (cl-format nil control-string rand))))

(defn postal-code-gen
  ([] (postal-code-gen :en))
  ([locale] (gen/fmap (partial postal-code locale) (check-gen/large-integer* {:min 10000 :max 99999}))))

(generator-from-coll :en [:address :city])

(defn phone-number
  ([] (phone-number :en))
  ([locale]
   (case locale
     :en (phone-number locale (rand-int 999) (rand-int 999) (rand-int 999))
     :fr (phone-number locale (rand-int 999999999) nil nil)
     (phone-number locale (rand-int 999999999) nil nil)))
  ([locale & [r1 r2 r3]]
   (let [control-string (rand-data locale [:phone-number-format])]
     (case locale
       :en (cl-format nil control-string r1 r2 r3)
       :fr (cl-format nil control-string r1)
       (cl-format nil "~10,'0d" r1)))))

;;declare Var to avoid Warning in CLJS
(def r1) (def r2) (def r3)
(defn phone-number-gen
  ([] (phone-number-gen :en))
  ([locale] (case locale
              :en
              (check-gen/let [r1 (check-gen/large-integer* {:min 100 :max 999})
                              r2 (check-gen/large-integer* {:min 100 :max 999})
                              r3 (check-gen/large-integer* {:min 100 :max 999})]
                (phone-number locale r1 r2 r3))
              :fr (check-gen/fmap (partial phone-number locale) (check-gen/large-integer* {:min 100000000 :max 999999999}))
              (check-gen/fmap (partial phone-number locale) (check-gen/large-integer* {:min 100000000 :max 999999999})))))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                              ;;
;;                 PERSON STUFF                                 ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(generator-from-coll :en [:person :first-name-male])
(generator-from-coll :en [:person :first-name-female])
(generator-from-coll :en [:person :last-name-male])
(generator-from-coll :en [:person :last-name-female])
(generator-from-coll :en [:person :position])

(defn random-letter []
  (-> (rand-int 26) (+ 97) char))

(defn random-digit []
  (rand-int 10))

(defn random-alphanum []
  (if (= (rand-int 3) 0)
    (first (str  (random-digit)))
    (random-letter)))

(defn random-password
  "Return a random password of n length, if missing arg a random password of random length between 6 and 20 is generated"
  ([] (random-password (+ (rand-int 14) 8)))
  ([n] (apply str (repeatedly n random-alphanum))))

(defn random-password-gen [] (gen/return (random-password)))

(defn random-password-from-spec
  "take an input like \"aa1aaaa1\" returns something like \"nz8iswp3\" if no arg then completely random password"
  [spec]
  (apply str (map (fn [c]
                    (cond (#{\0 \1 \2 \3 \4 \5 \6 \7 \8 \9} c) (random-digit)
                          :else (random-letter))) spec)))

(defn first-name
  ([] (first-name :en))
  ([locale] (if (= (rand-int 2) 0)
              (first-name-male locale)
              (first-name-female locale))))
(defn first-name-gen
  ([] (first-name-gen :en))
  ([locale] (if (= (rand-int 2) 0)
              (first-name-male-gen locale)
              (first-name-female-gen locale))))
(defn last-name
  ([] (last-name :en))
  ([locale] (if (= (rand-int 2) 0)
              (last-name-male locale)
              (last-name-female locale))))
(defn last-name-gen
  ([] (last-name-gen :en))
  ([locale] (if (= (rand-int 2) 0)
              (last-name-male-gen locale)
              (last-name-female-gen locale))))

(defn age []
  (rand-int 110))
(defn age-gen []
  (check-gen/large-integer* {:min 18 :max 110}))

(defn date-of-birth
  ([] (date-of-birth (age)))
  ([age]
   #?(:clj (.minusYears (LocalDate/now) age))
   #?(:cljs (time-coerce/to-date (time/minus (time/today) (time/years age))))))

(defn date-of-birth-gen [age]
  (gen/return (date-of-birth age)))

(defn- identifier [first-name last-name]
  (let [lower-fn (str/replace (str/lower-case first-name) " " "")
        lower-ln (str/replace (str/lower-case last-name) " " "")
        generators {:initial-last-name (fn [] (str (subs lower-fn 0 1) lower-ln))
                    :first-dot-last (fn [] (str lower-fn "." lower-ln))
                    :first-number (fn [] (str lower-fn (rand-int 999)))
                    :last (fn [] lower-ln)
                    :first (fn [] lower-fn)}]
    (str ((get (vec (vals generators)) (rand-int (count generators)))))))

(defn username
  ([] (username (first-name) (last-name)))
  ([locale] (username (first-name locale) (last-name locale)))
  ([first-name last-name]
   (identifier first-name last-name)))

(defn username-gen
  ([] (username-gen (first-name) (last-name)))
  ([locale] (username-gen (first-name locale) (last-name locale)))
  ([first-name last-name]
   (gen/return (username first-name last-name))))

(defn email-host
  ([] (email-host :en))
  ([locale] (rand-data locale [:person :personal-email])))

(defn email
  ([] (email (first-name) (last-name)))
  ([identifier] (str identifier "@" (email-host)))
  ([first-name last-name] (email :en first-name last-name))
  ([locale first-name last-name]
   (str (identifier first-name last-name) "@" (email-host locale))))

(defn fixed-email
  "return email address with constant host deduced from the username hash"
  [identifier]
  (str identifier "@" (rand-data-from-seed (long (hash identifier)) :en [:person :personal-email])))

(defn email-gen [locale first-name last-name]
  (gen/return (email locale first-name last-name)))

(defn sex []
  (case (rand-int 2) 0 :male 1 :female))

(defn picture-url
  ([] (picture-url (sex)))
  ([sex] (picture-url sex (rand-int 100)))
  ([sex r]
   (let [s (case sex :male "men" :female "women" "men")]
     (str "https://randomuser.me/api/portraits/" s "/" r ".jpg"))))

(defn picture-url-gen [sex]
  (gen/fmap (partial picture-url sex) (check-gen/large-integer* {:min 0 :max 99})))

(defn- person-all [locale {:keys [first-name last-name sex] :as specific}]
  (let [age (age)]
    (merge specific {:username (username first-name last-name)
                     :email (email locale first-name last-name)
                     :position (position locale)
                     :phone-number (phone-number locale)
                     :age age
                     :password (random-password)
                     :date-of-birth (date-of-birth age)
                     :picture-url (picture-url sex)
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
                            phone-number (phone-number-gen locale)
                            age (age-gen)
                            password (random-password-gen)
                            date-of-birth (date-of-birth-gen age)
                            position (position-gen locale)
                            picture-url (picture-url-gen sex)
                            address (address-gen locale)]
              (create-map first-name last-name sex username email phone-number age password date-of-birth picture-url address))))

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
                            phone-number (phone-number-gen locale)
                            age (age-gen)
                            password (random-password-gen)
                            date-of-birth (date-of-birth-gen age)
                            position (position-gen locale)
                            picture-url (picture-url-gen sex)
                            address (address-gen locale)]
              (create-map first-name last-name sex username email phone-number age password date-of-birth picture-url address))))

(defn person
  ([] (person :en))
  ([locale] (if (= (rand-int 2) 0)
              (person-male locale)
              (person-female locale))))

(defn person-gen
  ([] (person-gen :en))
  ([locale] (if (= (rand-int 2) 0)
              (person-male-gen locale)
              (person-female-gen locale))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                              ;;
;;                 COMPANY STUFF                                ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(generator-from-coll :en [:company :company-name])
(generator-from-coll :en [:company :company-type])
(generator-from-coll :en [:company :tld])
(generator-from-coll :en [:company :function])

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

(defn company-email
  ([domain] (company-email :en domain))
  ([locale domain] (str (rand-data locale [:company :email]) "@" domain)))
(defn company-email-gen
  ([domain] (company-email-gen :en))
  ([locale domain] (gen/return (company-email locale domain))))

(defn in-en
  ([] (in-en (rand-int 9999)))
  ([rand]
   (let [serial (cl-format nil "~7,'0d" rand)
         area-excluding-numbers #{7, 8, 9, 17, 18, 19, 28, 29, 41, 47, 49, 69, 70, 79, 89, 96, 97}
         area (cl-format nil "~2,'0d" (rand-excluding 99 area-excluding-numbers))]
     (str area "-" serial))))

(defn in-fr
  ([] (in-fr (rand-int 99999999)))
  ([rand]
   (cl-format nil "~8,'0d" rand)))

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

(defn company-id [name]
  (str/replace name #" " ""))
(defn company-id-gen [name]
  (gen/return (company-id name)))

(generator-from-coll :en [:company :department])

(defn company
  ([] (company :en))
  ([locale] (company locale (company-name locale)))
  ([locale name]
   (let [type (company-type locale)
         full-name (full-name name type)
         domain (domain name (tld locale))
         url (url domain)
         email (company-email locale domain)]
     {:company-id (company-id name)
      :company-name name
      :full-name full-name
      :identification-number (identification-number locale)
      :domain domain
      :url url
      :logo-url (logo-url name)
      :company-type type
      :email email
      :phone-number (phone-number locale)
      :address (address locale)
      :updated-by (username locale)
      :updated-at (Instant/now)})))

(defn company-with-name [name]
  (company :en name))

;;declare Var to avoid Warning in CLJS because of test.check let macro
(def first-name) (def last-name) (def email) (def sex) (def company-name) (def company-type)

(defn company-gen
  ([] (company-gen :en))
  ([locale]
   (check-gen/let [company-name (company-name-gen)
                   company-id (company-id-gen company-name)
                   company-type (company-type-gen)
                   identification-number (identification-number-gen locale)
                   full-name (full-name-gen company-name company-type)
                   tld (tld-gen)
                   domain (domain-gen company-name tld)
                   url (url-gen domain)
                   logo-url (logo-url-gen company-name)
                   email (company-email-gen locale domain)
                   phone-number (phone-number-gen locale)
                   address (address-gen locale)
                   updated-by (username-gen)]
     (create-map company-name company-id company-type identification-number full-name tld domain url logo-url email phone-number address updated-by))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                              ;;
;;                 THING STUFF                                  ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(generator-from-coll :en [:quality])
(generator-from-coll :en [:shape])
(generator-from-coll :en [:color])
(generator-from-coll :en [:animal])
(generator-from-coll :en [:landform])

(defn quality-color-animal
  []
  (str/join " " [(quality) (color) (animal)]))

