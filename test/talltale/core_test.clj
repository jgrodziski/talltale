(ns talltale.core-test
  (:require [clojure.test :refer :all]
            [clojure.spec.gen.alpha :as gen]
            [talltale.core :refer :all]))

(def company-full-name-pattern #"^[\w\s.]+$")

(def domain-pattern #"^[a-zA-Z0-9-]+\.[a-zA-Z]{2,}$")

(def identifier-pattern #"^[a-z.\d]+$")

(def email-pattern #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$")

(deftest address-test
  (testing "address full generator"
    (is (not (nil? (address))))))

(deftest company-test
  (testing "company full generator"
    (is (not (nil? (company))))))

(deftest person-test
  (testing "person full generator"
    (is (not (nil? (person))))))

(deftest company-gen-test
  (let [company (gen/generate (company-gen))]
    (is (map? company))
    (is (re-matches email-pattern (:email company)))
    (is (re-matches company-full-name-pattern (:full-name company)))
    (is (re-matches domain-pattern (:domain company)))
    (is (re-matches identifier-pattern (:updated-by company)))))
