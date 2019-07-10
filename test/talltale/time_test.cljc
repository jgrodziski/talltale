(ns talltale.time-test
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]
   [clojure.test.check.generators];require required to load the spec generators
   [cljc.java-time.instant :as instant]
   [cljc.java-time.local-date :as local-date]
   [cljc.java-time.local-date-time :as local-date-time]
   [cljc.java-time.zoned-date-time :as zoned-date-time]
   [tick.alpha.api :as t]
   #?(:cljs [cljs.test :as test :include-macros true :refer [deftest testing is]])
   #?(:clj [clojure.test :as test :refer [deftest testing is]])
   [talltale.time :as time]))

(deftest time-instance-test
  (testing "times instances"
    (let [i (instant/now)
          ld (local-date/now)
          ldt (local-date-time/now)
          zdt (zoned-date-time/now)]
      (is (time/instant? i))
      (is (time/local-date? ld))
      (is (time/local-date-time? ldt))
      (is (time/zoned-date-time? zdt)))
    (testing "times instances generated with spec"
      (let [i (gen/generate (s/gen ::time/instant))
            ld (gen/generate (s/gen ::time/local-date))
            utc-zdt (gen/generate (s/gen ::time/UTC-zoned-date-time))
            utc-ldt (gen/generate (s/gen ::time/UTC-local-date-time))]
        (is (time/instant? i))
        (is (time/local-date? ld))
        (is (time/zoned-date-time? utc-zdt))
        (is (t/zone "UTC") (t/zone utc-zdt))
        (is (time/local-date-time? utc-ldt))))))

