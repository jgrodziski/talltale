(ns talltale.core-test
  (:require [clojure.test :refer :all]
            [talltale.core :refer :all]))

(deftest address-test 
  (testing "address full generator"
    (is (not (nil? (address))))))

(deftest company-test 
  (testing "company full generator"
    (is (not (nil? (company))))))

(deftest person-test 
  (testing "person full generator"
    (is (not (nil? (person))))))
