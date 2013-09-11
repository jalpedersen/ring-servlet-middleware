(defproject org.signaut/ring.middleware.servlet-ext "1.0"
            :description "Exposes information from the underlying servlet environment."
            :url "http://github.com/jalpedersen/ring-userprincipal-middleware"
            :dependencies [[org.clojure/clojure "1.3.0"]
                           [ring/ring-core "1.2.0"]]
            :plugins [[lein-clojars "0.9.1"]]
            :warn-on-reflection true
            :profiles
            {:provided {:dependencies [[javax.servlet/servlet-api "2.5"]]}
             :dev {:dependencies [[javax.servlet/servlet-api "2.5"]]}
             :1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}
             :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}})
