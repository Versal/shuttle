(ns cljs.test
  (:require [clojure.browser.repl :as repl])
  (:use [jayq.core :only [xhr]]
        [jayq.util :only [log]]))

(repl/connect "http://localhost:9000/repl")

(defn ^:export submit []
  (xhr [:post "/api/submit"] {:submission 42}
       (fn [_] (log "Finished submission"))))
