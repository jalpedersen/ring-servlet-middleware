(defproject org.signaut/ring-userprincipal-middleware "0.1"
  :description "Exposes the current user principal from the surrounding servlet environment."
  :url "http://github.com/jalpedersen/ring-userprincipal-middleware"
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [ring/ring-servlet "0.3.8"]]
  :dev-dependencies [[lein-clojars "0.6.0"]]
  :warn-on-reflection true)
