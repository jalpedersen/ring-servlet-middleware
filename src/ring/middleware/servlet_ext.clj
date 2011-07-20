(ns ring.middleware.servlet-ext
  (:import javax.servlet.http.HttpServletRequest
           java.security.Principal))

(defn- in-role? [^HttpServletRequest servlet-req allow-roles]
  (if (empty? allow-roles)
    true
    (loop [remaining-roles allow-roles]
      (if (empty? remaining-roles)
        false
        (if (.isUserInRole servlet-req (first remaining-roles))
          true
          (recur (rest remaining-roles)))))))

(defn wrap-userprincipal [app & [ & {:keys [allow-roles]}]]
  "Wrap request with user principal.
  If a userprincipal is available, request is associated with
  a user (:username) and a predicate that given a role name returns true
  or false depending on wheter the user is in that role or not."
  (fn [req]
    (let [^HttpServletRequest servlet-req (:servlet-request req)
          ^Principal principal (if servlet-req
                                 (.getUserPrincipal servlet-req)
                                 nil)
          error-response {:status 403
                          :headers {"content-type" "text/plain"}
                          :body "Not authorized."}]
      (if principal
        (if (in-role? servlet-req allow-roles)
          (app (assoc req :username (.getName principal)
                      :in-role? #(.isUserInRole servlet-req %)))
          error-response)
        (if allow-roles
          error-response
          (app (assoc req :username nil
                      :in-role? (fn [role] false))))))))


