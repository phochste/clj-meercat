;; (c) 2010 Patrick Hochstenbach <patrick.hochstenbach@gmail.com>
(ns clj-meercat.solr
  (:use [clojure.contrib.logging :as log])
  (:import [org.apache.solr.client.solrj
	    SolrServer
	    SolrServerException]
	   [org.apache.solr.client.solrj.impl
	    CommonsHttpSolrServer]
	   [org.apache.solr.client.solrj.response
	    QueryResponse]
	   [org.apache.solr.common.params
	    ModifiableSolrParams]
	   ))

(defn client
  [adr]
  (new CommonsHttpSolrServer adr))

(defn close
  [client]
  nil)

(defn- query-results
  [response]
  (let [res (.getResults response)]
    (for [doc res]
      (reduce #(assoc %1 (keyword %2) (get doc %2)) {} (keys doc)))))

(defn- facet-results
  [response]
  (let [ff   (.getFacetFields response)]
   (for [field ff]
    (reduce #(assoc %1 (.getName %2) (.getCount %2)) {} (.getValues field)))))
    
(defn- hits
  [response]
  (.getNumFound (.getResults response)))

(defn- results
  [response]
  (if (nil? (.getFacetFields response))
    (query-results response)
    (facet-results response)))

(defn query
  ([client q] (query client q 0 10 nil nil))
  ([client q start] (query client q start 10 nil nil))
  ([client q start num] (query client q start num nil nil))
  ([client q start num sort filter]
     (let [params (doto (new ModifiableSolrParams)
		 (.set "q" (into-array String [q]))
		 (.set "start" start)
		 (.set "rows" num)
		 (.set "sort" (into-array String [sort]))
		 (.set "fq" (into-array String filter)))
	   response (.query client params)]
       {:query q
	:facet nil
	:start start
	:num num
	:fitler filter
	:sort sort
	:hits (hits response)
	:results (results response)})))

(defn facet
  ([client q field] (facet client q field nil))
  ([client q field filter]
   (let [params (doto (new ModifiableSolrParams)
		 (.set "q" (into-array String [q]))
		 (.set "start" 0)
		 (.set "rows" 0)
		 (.set "facet" true)
		 (.set "facet.field" (into-array String [field]))
		 (.set "facet.sort" true)
		 (.set "facet.limit" 10)
		 (.set "facet.mincount" 0)
		 (.set "facet.missing" false)
		 (.set "fq" (into-array String filter)))
	 response (.query client params)]
     {:query q
      :facet facet
      :start 0
      :num 0
      :filter filter
      :sort nil
      :hits (hits response)
      :results (results response)})))
 
