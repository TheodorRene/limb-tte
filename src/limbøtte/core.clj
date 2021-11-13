(ns limbøtte.core
  (:require [ring.adapter.jetty :as j]
            [ring.middleware.params :as p]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [compojure.core :refer [defroutes GET POST]]
            [ring.middleware.reload :refer [wrap-reload]]
            [clojure.string :as s]
            )
  )

(def plaintext {"Content-Type" "text/plain; charset=utf-8"})


;; DB stuff
(defn db [name] {:dbtype "sqlite" :dbname name})

(def ds
  (jdbc/get-datasource (db "database.sqlite")))

(def db-settings {:return-keys true
                  :build-fn rs/as-unqualified-lower-maps})

(defn do-query 
  ([ds query]  (jdbc/execute! ds [query ]))
  ([ds query param]  (jdbc/execute-one! ds [query param] db-settings))
  )

;; QUERIES
(def drop-table "DROP TABLE paste")

(def create-table "
  CREATE TABLE IF NOT EXISTS paste (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      text TEXT)
  ")

(def get-paste-q 
  "SELECT id, text FROM paste WHERE id=?")

(def insert-paste-q "
  INSERT INTO paste (text)
  values
  (?)
  RETURNING 
  id, text
  ")
;; INIT DB
;;(do-query ds drop-table)
(do-query ds create-table)
(do-query ds insert-paste-q "gurba")
;; END QUERIES

(defn get-paste-db [ds id]
 (:paste/text 
   (do-query ds get-paste-q id)))

(defn insert-paste-db [ds text]
  (do-query ds insert-paste-q text))

(defn getHTML [text]
  (str "<p>" text "</p>")
  )




(defn nl2br [text]
  (interpose "<br>" (s/split text #"\n"))
  )

(nl2br "hello\n")

(defn getHandler [id]
  (getHTML (get-paste-db ds id)))
   




(defn post-form [_]
   "<div>
      <h1>Limbøtte?</h1>
      <p> Gi meg ditt lim </p>
      <form method=\"post\" action=\"\">
       <input type=\"text\" name=\"name\" />
       <input type=\"submit\" value\"submit\" />
      </form>
    </div>")


(defn parseBody [req]
  (let [{:keys [form-params]} req
       param-p (get form-params "name")
       ] param-p)
  )

(defn returnHTML [res]
  (str "<div>
    <h1> Limbøtte </h1>
    <p> Her er ditt lim </p>
    <a href=/" (str(:paste/id res)) "> Kopier meg </a>
    <p>" res "</p>
  </div>")
)

(defn post-return [req]
  (let [ return-body (insert-paste-db ds (parseBody req)) ]
    (returnHTML return-body)
  ))

(defn postHandler [req]
  {:status 200
   :headers plaintext
   :body (str
             (insert-paste-db ds (parseBody req)))
   })

(defroutes routes
  (GET "/" [req] (post-form req))
  (POST "/" req (post-return req))
  (GET "/:id" [id] (getHandler id))
  )


(def app
  (-> routes
      p/wrap-params
      wrap-reload
      ))

(defn -main [& _]
  (j/run-jetty app {:port 3000}))
