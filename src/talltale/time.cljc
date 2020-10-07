(ns talltale.time
  #?(:clj
     (:require
       [clojure.spec.gen.alpha :as gen]
       [clojure.spec.alpha :as s]
       [tick.alpha.api :as t]
       [cljc.java-time.instant :as i :refer [to-epoch-milli]]
       [cljc.java-time.local-date-time :as ldt :refer [of-instant]]
       [cljc.java-time.local-date :as ld]
       [cljc.java-time.local-time :as lt]
       [cljc.java-time.zoned-date-time :as zdt]
       [cljc.java-time.zone-id :as zi :refer [get-available-zone-ids]]
       ;[java-time :refer [local-date-time local-date local-time zoned-date-time instant]]
       ))
  #?(:cljs
     (:require
       [cljs.spec.gen.alpha :as gen]
       [cljs.spec.alpha :as s]
       [tick.alpha.api :as t]
       [cljc.java-time.instant :as i :refer [to-epoch-milli]]
       [cljc.java-time.local-date-time :as ldt :refer [of-instant]]
       [cljc.java-time.local-date :as ld]
       [cljc.java-time.local-time :as lt]
       [cljc.java-time.zoned-date-time :as zdt]
       [cljc.java-time.zone-id :as zi :refer [get-available-zone-ids]]
       [java.time :refer [Instant ZonedDateTime OffsetDateTime LocalDateTime LocalDate LocalTime ZoneId ZoneOffset]]))
  #?(:clj (:import [java.time Instant ZonedDateTime OffsetDateTime LocalDateTime LocalDate LocalTime ZoneId ZoneOffset])))

(defn UTC-zoned-date-time? [x]
  (and (instance? ZonedDateTime x)
       (.equals (t/zone "UTC") (t/zone x))))

(defn zoned-date-time? [x]
  (and (instance? ZonedDateTime x)))

(defn offset-date-time? [x]
  (instance? OffsetDateTime x))

(defn local-date-time? [x]
  (instance? LocalDateTime x))

(defn local-date? [x]
  (instance? LocalDate x))

(defn local-time? [x]
  (instance? LocalTime x))

(defn zone-id? [x]
  (instance? ZoneId x))

(defn instant? [x]
  (instance? Instant x))

(def UTC (t/zone "UTC"))
;(def OffsetUTC (ZoneOffset/UTC))

(def all-zone-ids
  (delay
    (get-available-zone-ids)))

(defn ^:dynamic *zone-ids*
  "Dynamically bind this to choose which time zones to use in generators."
  []
  (gen/one-of [(gen/return (t/zone "UTC"))
               (s/gen @all-zone-ids)]))

(defn- interop-local-date-time [y m d h mm ss]
  #?(:clj (ldt/of y m d h mm ss))
  #?(:cljs (ldt/of y m d h mm ss)))

(s/def ::past (s/int-in (to-epoch-milli (t/instant (t/in (interop-local-date-time 2001 1 1 00 00 00) UTC)))
                        (to-epoch-milli (t/instant (t/in (interop-local-date-time 2010 12 31 00 00 00) UTC)))))

(s/def ::past-and-future (s/int-in (to-epoch-milli (t/instant (t/in (interop-local-date-time 2011 1 1 00 00 00) UTC)))
                                   (to-epoch-milli (t/instant (t/in (interop-local-date-time 2030 12 31 23 59 59) UTC)))))

(s/def ::future (s/int-in (to-epoch-milli (t/instant (t/in (interop-local-date-time 2031 1 1 00 00 00) UTC)))
                          (to-epoch-milli (t/instant (t/in (interop-local-date-time 2040 12 31 23 59 59) UTC)))))

(defn ^:dynamic *period*
  "Dynamically bind this to choose the range of your generated dates."
  []
  (s/gen ::past-and-future))

(s/def ::zone-id (s/with-gen zone-id? *zone-ids*))

(s/def ::UTC-zoned-date-time (s/with-gen zoned-date-time?
                               #(gen/fmap (fn [ms]
                                            (t/in (t/instant ms) UTC))
                                          (*period*))))

(s/def ::UTC-local-date-time (s/with-gen local-date-time?
                               #(gen/fmap (fn [ms]
                                            (of-instant (t/instant ms) UTC))
                                          (*period*))))

(s/def ::instant (s/with-gen instant?
                   #(gen/fmap (fn [ms] (t/instant ms)) (*period*))))

(s/def ::local-date (s/with-gen local-date?
                      #(gen/fmap (fn [local-date-time]
                                   (t/date local-date-time))
                                 (s/gen ::UTC-local-date-time))))



