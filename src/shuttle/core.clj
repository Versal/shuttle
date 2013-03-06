(ns shuttle.core
  (:use [compojure.core]
        [ring.middleware.format-params :only [wrap-restful-params]])
  (:require [clojure.tools.nrepl.server :as nrepl-server]
            [cemerick.piggieback]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [cljs.closure :as closure]
            [cemerick.yonder :as yonder]))

;; Some state on the server
(def submission (atom nil))

(defonce browser-repl
  (constantly
    (closure/build "src/cljs"
                   {:optimizations :whitespace :pretty-print true})))

(defroutes app-routes
           (route/resources "/" {:root "/private/html"})
           (route/resources "/js" {:root "/private/js"})
           (GET "/test.js" [] (browser-repl))
           (POST "/api/submit"
                 {{s :submission} :params}
                 ;; Modify server state
                 (reset! submission s)))

(def site (-> (handler/api app-routes) (wrap-restful-params)))
