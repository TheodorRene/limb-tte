(ns limbøtte.core
  (:require [ring.adapter.jetty :as j]
            [ring.middleware.params :as p]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [compojure.core :refer [defroutes GET POST]]
            [ring.middleware.reload :refer [wrap-reload]]
            [clojure.string :as s]
            [selmer.parser :as selm]))

;; DB stuff
(defn db [name] {:dbtype "sqlite" :dbname name})

(def ds
  (jdbc/get-datasource (db "database.sqlite")))

(def db-settings {:return-keys true
                  :build-fn rs/as-unqualified-lower-maps})

(defn do-query
  ([ds query]  (jdbc/execute! ds [query]))
  ([ds query param]  (jdbc/execute-one! ds [query param] db-settings)))

;; QUERIES
;;(def drop-table "DROP TABLE paste")

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
;;(do-query ds insert-paste-q "gurba")
;; END QUERIES

(defn get-paste-db [ds id]
  (:paste/text
   (do-query ds get-paste-q id)))

(defn insert-paste-db [ds text]
  (do-query ds insert-paste-q text))

(defn getHTML [text]
  (selm/render "<p> {{text}} </p>" {:text text}))

(defn nl2br [text]
  (apply str (interpose "<br>" (s/split text #"\n"))))

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
        param-p (get form-params "name")] param-p))

(defn returnHTML [res]
  (nl2br (selm/render "<div>
    <h1> Limbøtte </h1>
    <p> Her er ditt lim </p>
    <a href=/{{url}}> Kopier meg </a>
    <p> {{res}} </p>
  </div>" {:url (str (:paste/id res))
           :res res})))

(returnHTML #:paste{:id "<h1> hei </h1>" :test "gureba"})

(defn post-return [req]
  (let [return-body (insert-paste-db ds (parseBody req))]
    (returnHTML return-body)))

(defroutes routes
  (GET "/" [req] (post-form req))
  (POST "/" req (post-return req))
  (GET "/:id" [id] (getHandler id)))

(def app
  (-> routes
      p/wrap-params
      wrap-reload))

(defn -main [& _]
  (j/run-jetty app {:port 80}))
