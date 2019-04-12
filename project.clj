(defproject talltale "0.3.0"
  :description "A CLJ/CLJS generator lib for fake data"
  :url "https://github.com/jgrodziski/talltale"
  :deploy-repositories [["releases" :clojars]
                         ["snapshots" :clojars]]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [clojure.java-time "0.3.2"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [org.clojure/test.check "0.10.0-alpha4"]])
