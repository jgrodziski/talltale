(ns talltale.time
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]
   #?(:clj [clj-time.core :as time])
   #?(:clj [clj-time.spec :as time-spec]))
  #?(:clj (:import [org.joda.time DateTimeZone LocalDate LocalDateTime]
                   [org.joda.time.base BaseDateTime])) )

#?(:clj
   (defn date-time? [x]
     (and (instance? BaseDateTime x)
          (= (.getZone ^BaseDateTime x) DateTimeZone/UTC)))

   (defn local-date-time? [x]
     (instance? LocalDateTime x))

   (defn local-date? [x]
     (instance? LocalDate x))

   (defn time-zone? [x]
     (instance? DateTimeZone x))

   (defn date-time? [x]
     (and (instance? BaseDateTime x)
          (= (.getZone ^BaseDateTime x) DateTimeZone/UTC)))

   (s/def ::date-time
     (s/with-gen date-time?
       #(gen/fmap (fn [ms] (DateTime. ms DateTimeZone/UTC))
                  (*period*))))


   (s/def ::past (s/int-in (to-long (date-time 2001 1 1 00 00 00))
                                 (to-long (date-time 2010 12 31 00 00 00))))

   (s/def ::past-and-future (s/int-in (to-long (date-time 2011 1 1 00 00 00))
                                            (to-long (date-time 2030 12 31 23 59 59))))

   (s/def ::future (s/int-in (to-long (date-time 2031 1 1 0 00 00))
                                   (to-long (date-time 2040 12 31 23 59 59))))
   (defn ^:dynamic *period*
     "Dynamically bind this to choose the range of your generated dates."
     []
     (s/gen ::past-and-future))

   (s/def ::time-zone
     (s/with-gen time-zone? *time-zones*))

   (s/def ::date-time
     (s/with-gen date-time?
       #(gen/fmap (fn [ms] (DateTime. ms DateTimeZone/UTC))
                  (*period*))))

   (s/def ::local-date
     (s/with-gen local-date?
       #(gen/fmap (fn [ms] (LocalDate. ms))
                  (*period*))))

   (s/def ::local-date-time
     (s/with-gen local-date-time?
       #(gen/fmap (fn [ms] (LocalDateTime. ms))
                  (*period*))))
   )


#?(:cljs


   )
