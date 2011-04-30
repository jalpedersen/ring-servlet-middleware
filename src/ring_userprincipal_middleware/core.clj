(ns ring-userprincipal-middleware.core
  (:import javax.servlet.http.HttpServletRequest))

(defn wrap-userprincipal [handler]
  (fn [req]
    (let [^HttpServletRequest servlet-req (:servlet-request req)]
      (if servlet-req
        (.getName (.getUserPrincipal servlet-req))
        nil))))

  
