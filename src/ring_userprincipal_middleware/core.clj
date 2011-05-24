(ns ring-userprincipal-middleware.core
  (:import javax.servlet.http.HttpServletRequest
           java.security.Principal))

(defn wrap-userprincipal [app]
  "Wrap request with user principal.
  If a userprincipal is available, request is associated with
  a user (:username) and a predicate that given a role name returns true
  or false depending on wheter the user is in that role or not."
  (fn [req]
    (let [^HttpServletRequest servlet-req (:servlet-request req)
          ^Principal principal (if servlet-req 
                      (.getUserPrincipal servlet-req)
                      nil)]
      (if principal 
        (app (assoc req :username (.getName principal)
                    :is-in-role #(.isUserInRole servlet-req %)))
        (app (assoc req :username nil
                    :is-in-role (fn [role] false)))))))

  
