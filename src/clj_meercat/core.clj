;; (c) 2010 Patrick Hochstenbach <patrick.hochstenbach@gmail.com>
(ns clj-meercat.core
  (:require [clj-meercat.solr :as solr])
  (:use ring.adapter.jetty)
  (:use [clj-meercat.util :as util])
  (:use [clj-meercat.templates :as tpl])
  (:use [clojure.contrib.logging :as log])
  (:use (ring.middleware reload stacktrace file file-info params)))

;; DATABASE
(def engine (solr/client "http://localhost:8983/solr"))

;; CONTROLLERS
(defn do-query [req]
  (let [query (util/parse-query req)
	results (solr/query engine (:q query) (util/parse-integer (:start query 0)))]
   {:status  200
    :headers {"Content-Type" "text/html"}
    :body    (tpl/pages-query query results)}))

(defn do-default [req]
   {:status  200
    :headers {"Content-Type" "text/html"}
    :body    (tpl/pages-default)})

;; DISPATCHER
(defn- handler [req]
 ; (log/info (:uri req))
  (cond
   (= (:uri req) "/query")
     (do-query req)
   :else
     (do-default req)))

;; RING APPLICATION
(def app
  (-> #'handler
    (wrap-file "public")
    (wrap-file-info)
    (wrap-params)
    (wrap-reload '(clj-meercat.core))
    (wrap-stacktrace)))

(defn boot []
  (run-jetty #'app {:port 8080}))
