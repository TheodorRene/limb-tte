(ns limbøtte.core
  (:require [ring.adapter.jetty :as j]
            [ring.middleware.params :as p])
  )

(def plaintext {"Content-Type" "text/plain"})

(defn retrieve-paste-id [params]
  (get params "id"))

;; Here actually do the database query
(defn get-paste [stuff] (str stuff))

(def request {:query-params {"id" "hei"}})


(defn get-paste-handler [req]
  {:status 200
   :header plaintext
   :body (get-paste
           (retrieve-paste-id 
             (:query-params req)))})

(:body 
  (get-paste-handler request))


;; Learn threading, most likely need to add more middleware
(def app
  (p/wrap-params get-paste-handler))

(defn -main [& _]
  (j/run-jetty app {:port 3000, :join? false}))
