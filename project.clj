(defproject limbøtte "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                  [ring/ring-core "1.9.4"]
                  [ring/ring-jetty-adapter "1.9.4"]
                  [com.github.seancorfield/next.jdbc "1.2.737"]
                  [org.xerial/sqlite-jdbc "3.36.0.1"]
                  [compojure "1.6.2"]]
  :main ^:skip-aot limbøtte.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :plugins [[cider/cider-nrepl "0.24.0"]])
  
