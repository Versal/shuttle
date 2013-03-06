(ns shuttle.core-test
  (:require [cemerick.yonder :as yonder]
            [cemerick.piggieback]
            [ring.adapter.jetty :as jetty]
            [clojure.tools.nrepl :as nrepl]
            (clojure.tools.nrepl [server :as server]))
  (:use clojure.test
        shuttle.core))

(defn- with-webapp
  [f]
  (let [http (jetty/run-jetty site {:port 8080 :join? false})]
    (try
      (f)
      (finally (.stop http)))))

(use-fixtures :once #'with-webapp)

(deftest browser-test
         (yonder/with-session
           [session
            (yonder/open-session
              {:prepare (partial yonder/prepare-phantomjs
                                 {:url "http://localhost:8080/test.html"})
               :new-server
               {:handler (server/default-handler
                           #'cemerick.piggieback/wrap-cljs-repl)}})]
           (yonder/eval session (cljs.test/submit))
           (Thread/sleep 4000)
           (is (= "42" @submission))))
