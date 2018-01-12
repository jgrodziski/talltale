(in-ns 'talltale.core)

(generator-from-coll :en [:person :first-name-male])
(generator-from-coll :en [:person :first-name-female])
(generator-from-coll :en [:person :last-name-male])
(generator-from-coll :en [:person :last-name-female])

(defn age []
  (rand-int 110))
(defn age-gen []
  (check-gen/large-integer* {:min 18 :max 110}))

(defn date-of-birth [age]
  (time/minus (time/today) (time/years age)))
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

(defn picture-url
  ([sex] (picture-url sex (rand-int 100)))
  ([sex r]
   (let [s (case sex :male "men" :female "women" "men")]
     (str "https://randomuser.me/api/portraits/" s "/" r ".jpg"))))

(defn picture-url-gen [sex]
 (gen/fmap (partial picture-url sex) (check-gen/large-integer* {:min 0 :max 99}) ))

(defn- person-all [locale {:keys [first-name last-name sex] :as specific}]
  (let [age (age)]
    (merge specific {:username (username first-name last-name)
                     :email (email locale first-name last-name)
                     :phone-number (phone-number locale)
                     :age age
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
                            date-of-birth (date-of-birth-gen age)
                            picture-url (picture-url-gen sex)
                            address (address-gen locale)]
              (create-map first-name last-name sex username email phone-number age date-of-birth picture-url address))))

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
                            date-of-birth (date-of-birth-gen age)
                            picture-url (picture-url-gen sex)
                            address (address-gen locale)]
              (create-map first-name last-name sex username email phone-number age date-of-birth picture-url address))))

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
