(ns clj-meercat.templates
  (:use [clj-meercat.util :as util])
  (:use [clojure.contrib.str-utils :as str])
  (:use [net.cgrand.enlive-html :as e])
  )

(defn- lookup-keyword-by-class
  [node prefix col]
  (-> node :attrs :class (subs (inc (count prefix))) keyword col str))

(defn check-by-class
  "Checks a radio/checkbox element if its class attribute contains
  a record key that can be found in the result set 'col'. Use the
  prefix to trim off the class attribute name.
  Return the orignal HTML element if no match is found."
  [prefix col]
  #(let [value (-> % :attrs :value)
	 txt (lookup-keyword-by-class % prefix col)]
     (if (= value txt)
       ((e/set-attr :checked "checked") %)
       %)))
     
(defn select-by-class
  "Selects a select element if its class attribute contains a record
  key that can be found in the result set 'col'. Use the prefix to
  trim off the class attribute name.
  Return the original HTML select element if nothing is found."
  [prefix col]
  #(if-let [txt (lookup-keyword-by-class % prefix col)]
     (e/at % [[:option (e/attr= :value txt)]] (e/set-attr :selected "selected"))
     %))

(defn attribute-by-class
  "Set the value of an HTML element to the content of the class
  attribute looked up in the result set 'col'. Use the prefix to
  trim off the class attribute name.
  Return the original HTML element if nothing is found." 
  [prefix attribute col]
  #(if-let [txt (lookup-keyword-by-class % prefix col)]
     ((e/set-attr attribute txt) %)
     %))

(defn content-by-class
  "Set the content of an HTML element to the content of the class
  attribute looked up in the result set 'col'. Use the prefix to
  trim off the class attribute name.
  Return the orginal HTML element if nothing is found."
  [prefix col]
  #(if-let [txt (lookup-keyword-by-class % prefix col)]
     ((e/content txt) %)
     %))

;; Start page
(e/deftemplate pages-default "pages/default.html" [])

;; Query result loop
(e/defsnippet pages-results "pages/query.html" [:.query.results]
  [result]
  [:.result] (content-by-class "result" result))

;; Result page
(e/deftemplate pages-query "pages/query.html" [query results]
  [[:input.param (e/attr= :type "radio")]] (check-by-class "param" query)
  [[:input.param (e/attr= :type "checkbox")]] (check-by-class "param" query)
  [[:input.param (e/attr= :type "text")]] (attribute-by-class "param" :value query)
  [[:input.param (e/attr= :type "hidden")]] (attribute-by-class "param" :value query)
  [:textarea.param] (content-by-class "param" query)
  [:select.param] (select-by-class "param" query)
  [:span.param] (content-by-class "param" query)
  [:span.query] (content-by-class "query" results)
  [:.query.results] (e/substitute (map pages-results (:results results)))    
)
