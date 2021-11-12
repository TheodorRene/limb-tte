(ns my-stuff.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn ls [] "im a function")
(ls)

(defn hello [name]
  (str "Hello " name))
(hello :Maria)

(def henlo #(str "henlo " %1))
(henlo "mariA")

(defn count-args [& args]
  (str "you passed " (count args) " args: " args))

(count-args 1 2 3 4)

(def x {:a 1})
(x :a)

(def keymap {:a 1, :b 2})
(def keymap2 {:a 1 :b 2})
(keymap :b)
(keymap2 :b)

(set [1 1 1 1])

(require 'clojure.string)
(clojure.string/blank? "")
(clojure.string/blank? "e")

(require '[clojure.repl :refer :all])
(doc +)

(let [x 1
      y 2]
  (+ x y))

(defn plusone [x y]
  (+ x y))

(def gurba (partial plusone 5))

(x 1)

(defn always-thing [& args] 100)

(defn make-thingy [x]
  (defn always [& args] x))

(let [n (rand-int Integer/MAX_VALUE)
      f (make-thingy n)]
  (assert (= n (f)))
  (assert (= n (f 123)))
  (assert (= n (apply f 123 (range)))))

(defn triplicate [f]
  (f) (f) (f))
(triplicate #(println "hello"))

(Math/sqrt 25)
(defn http-get [url]
  (slurp
   (.openStream
    (java.net.URL. url))))

(http-get "https://theodorc.no")
(slurp "https://theodorc.no")

((comp x x) 1)

(def person
  {:name "Theodor"
   :age 24})

(source defrecord)
(:name person)

(let [x 4] 
  (str x " is " (if (even? x) "even" "odd")))

(let [x 4] 
  (cond
        (< x 2) "x i less than 2"
        (< x 10) "x is less than 10"))

