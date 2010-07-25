(defproject clj-meercat "0.1.0-SNAPSHOT"
  :description "a library catalog front-end based on Ghent University Meercat "
  :dev-dependencies [[swank-clojure "1.2.1"]
		     [ring/ring-devel "0.2.0"]]
  :dependencies [[org.clojure/clojure "1.2.0-master-SNAPSHOT"]
                 [org.clojure/clojure-contrib "1.2.0-SNAPSHOT"]
		 [org.clojars.kjw/solrj "1.4.0"]
		 [ring/ring-core "0.2.0"]
		 [ring/ring-jetty-adapter "0.2.0"]
		 [enlive/enlive "1.0.0-SNAPSHOT"]
		 [log4j "1.2.15" :exclusions [javax.mail/mail
	                                      javax.jms/jms
	                                      com.sun.jdmk/jmxtools
	                                      com.sun.jmx/jmxri]]])
