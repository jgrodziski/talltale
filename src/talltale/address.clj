
(in-ns 'talltale.core)

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

(defn phone-number
  ([] (phone-number :en))
  ([locale]
   (case locale
     :en (phone-number locale (rand-int 999))
     :fr (phone-number locale (rand-int 999999999))
     (phone-number locale (rand-int 999999999))))
  ([locale r]
   (let [df (rand-data locale [:phone-number-format])]
     (case locale
       :en (format df r r r)
       :fr (format df r)
       (format "%010d" r)))))

(defn phone-number-gen
  ([] (phone-number-gen :en))
  ([locale] (case locale
              :en (check-gen/fmap (partial phone-number locale) (check-gen/large-integer* {:min 100 :max 999}))
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
