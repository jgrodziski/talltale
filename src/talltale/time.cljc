(ns talltale.time
  #?(:clj
     (:require
       [clojure.spec.gen.alpha :as gen]
       [clojure.spec.alpha :as s]
       [tick.core :as t]
       [tick.alpha.interval :as interval]
       [tick.locale-en-us]
       [clojure.test.check.generators :as check-gen]

       [cljc.java-time.instant :as i :refer [to-epoch-milli]]
       [cljc.java-time.local-date-time :as ldt :refer [of-instant]]

       [cljc.java-time.zone-id :as zi :refer [get-available-zone-ids]]
       [java-time :refer [local-date-time local-date local-time zoned-date-time instant]]
       [talltale.rand :refer [rand-in]]
       ))
  #?(:cljs
     (:require
       [cljs.spec.alpha :as s]
       [tick.core :as t]
       [tick.alpha.interval :as interval]
       [cljc.java-time.local-date-time :as ldt :refer [of-instant]]
       ))
  #?(:clj (:import [java.time Instant ZonedDateTime OffsetDateTime LocalDateTime LocalDate LocalTime ZoneId ZoneOffset Duration Period Clock]
                   [java.time.format DateTimeFormatter])))


(def UTC ^java.time.ZoneOffset (t/zone "UTC"))

(def all-zone-ids
  (delay
    (get-available-zone-ids)))

(defn ^:dynamic *zone-ids*
      "Dynamically bind this to choose which time zones to use in generators."
      []
      (check-gen/one-of [(check-gen/return (t/zone "UTC"))
                         (check-gen/elements @all-zone-ids)]))

(defn- interop-local-date-time [y m d h mm ss]
       #?(:clj (local-date-time y m d h mm ss))
       #?(:cljs (ldt/of y m d h mm ss)))

(defn past-instant []
  (let [ms (rand-in (to-epoch-milli (t/instant (t/in (interop-local-date-time 2001 1 1 00 00 00) UTC)))
                    (to-epoch-milli (t/instant (t/in (t/date-time) UTC))))]
    (of-instant (t/instant ms) (t/zone))))

(defn future-instant []
  (let [ms (rand-in (to-epoch-milli (t/instant (t/in (t/date-time) UTC)))
                    (to-epoch-milli (t/instant (t/in (interop-local-date-time 2060 1 1 00 00 00) UTC))))]
    (of-instant (t/instant ms) (t/zone))))

(s/def ::past (s/int-in
                        ))

(s/def ::past-and-future (s/int-in (to-epoch-milli (t/instant (t/in (interop-local-date-time 2011 1 1 00 00 00) UTC)))
                                   (to-epoch-milli (t/instant (t/in (interop-local-date-time 2030 12 31 23 59 59) UTC)))))

(s/def ::future (s/int-in (to-epoch-milli (t/instant (t/in (interop-local-date-time 2031 1 1 00 00 00) UTC)))
                          (to-epoch-milli (t/instant (t/in (interop-local-date-time 2040 12 31 23 59 59) UTC)))))
