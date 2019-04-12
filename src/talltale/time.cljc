(ns talltale.time
  "This namespace requires Clojure 1.9 or later. It defines a set of predicates plus a set of spec defs with associated generators."
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [java-time :refer [local-date-time local-date local-time zoned-date-time instant]])
  (:import [java.time ZonedDateTime OffsetDateTime LocalDateTime LocalDate LocalTime ZoneId ZoneOffset]))

(defn UTC-zoned-date-time? [x]
  (and (instance? ZonedDateTime x)
       (.equals (ZoneId/of "UTC") (.getZone x))))

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

(def UTC (ZoneId/of "UTC"))
(def OffsetUTC (ZoneOffset/UTC))

(def all-zone-ids
  (delay
    (set
     (keep #(try (ZoneId/of ^String %)
                 (catch Throwable t nil))
            (ZoneId/getAvailableZoneIds)))))

(defn ^:dynamic *zone-ids*
  "Dynamically bind this to choose which time zones to use in generators."
  []
  (gen/one-of [(gen/return (ZoneId/of "UTC"))
               (s/gen @all-zone-ids)]))

(s/def ::past (s/int-in (.toEpochMilli (.toInstant (local-date-time 2001 1 1 00 00 00) OffsetUTC))
                        (.toEpochMilli (.toInstant (local-date-time 2010 12 31 00 00 00) OffsetUTC))))

(s/def ::past-and-future (s/int-in (.toEpochMilli (.toInstant (local-date-time 2011 1 1 00 00 00) OffsetUTC))
                                   (.toEpochMilli (.toInstant (local-date-time 2030 12 31 23 59 59) OffsetUTC))))

(s/def ::future (s/int-in (.toEpochMilli (.toInstant (local-date-time 2031 1 1 0 00 00) OffsetUTC))
                          (.toEpochMilli (.toInstant (local-date-time 2040 12 31 23 59 59) OffsetUTC))))

(defn ^:dynamic *period*
  "Dynamically bind this to choose the range of your generated dates."
  []
  (s/gen ::past-and-future))

(s/def ::zone-id (s/with-gen zone-id? *zone-ids*))

(s/def ::UTC-zoned-date-time (s/with-gen zoned-date-time?
                           #(gen/fmap (fn [ms] (ZonedDateTime/ofInstant (instant ms) UTC))
                                      (*period*))))

(s/def ::UTC-local-date-time (s/with-gen local-date-time?
                               #(gen/fmap (fn [ms] (LocalDateTime/ofInstant (instant ms) UTC))
                                          (*period*))))

(s/def ::local-date (s/with-gen local-date?
                      #(gen/fmap (fn [local-date-time] (.toLocalDate local-date-time))
                                 (s/gen ::local-date-time))))

