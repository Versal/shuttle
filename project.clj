(defproject shuttle "0.1.0-SNAPSHOT"
            :description "End-to-end testing"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [jayq "2.0.0"]
                           [compojure "1.1.5"]
                           [yayitswei/yonder "0.0.2-SNAPSHOT"]
                           [ring-middleware-format "0.2.4"]
                           [org.clojure/clojurescript "0.0-1576"]
                           [org.clojure/google-closure-library-third-party "0.0-2029"]]
            :plugins [[lein-ring "0.8.2"]]
            :ring {:handler shuttle.core/site})
