(ns clj-meercat.util
  (:use [clojure.contrib.str-utils :only [str-join]]
	[clojure.contrib.java-utils :only [as-str]])
  (:import (java.net URLEncoder)))

(defn parse-integer
  [str]
  (if (integer? str)
    str
   (try
    (Integer/parseInt str)
    (catch NumberFormatException nfe 0))))

(defn parse-query
  "Return map of keyword->param values geven a HTTP request."
  [req]
  (let [params (:query-params req)]
    (zipmap (map keyword (keys params)) (vals params))))

(defn url-encode
  "Wrapper around java.net.URLEncoder returning a (UTF-8) URL encoded
   representation of argument, either a string or map."
  [arg]
  (if (map? arg)
    (str-join \& (map #(str-join \= (map url-encode %)) arg))
    (URLEncoder/encode (as-str arg) "UTF-8")))