(ns enlive-demo
  (:use [net.cgrand.enlive-html :as e])
  (:use [clojure.contrib.prxml :as p]))

(declare funny-html)

(def mynode {:tag "div" :attrs {:x 1} :content "testdiv"})

(e/defsnippet ex2 "pages/ex2.html" [:table]
  [ctx]
  [:td] (e/content "Aha"))

(e/deftemplate ex1 "pages/ex1.html" [ctx]
  [:a#linky]   (e/set-attr :href (:href ctx))
  [:p#divme]   (e/wrap :div {:class "foo"})
  [:p#undivme] (e/unwrap :div)
  [:p#message] (e/do->
		(e/content (:message ctx) mynode)
		(e/prepend "--#")
		(e/append "#--")
		)
  [:p#inner] (e/content (map ex2 ctx))
  [:p#html-message] (e/html-content (funny-html ctx)))

(defn funny-html [ctx]
  (with-out-str
    (p/prxml
     [:table
      [:tr
       [:td {:bgcolor "#cccccc"} (:message ctx)]
      ]
     ])))

(ex1 {:message "hi"})