(ns ring.middleware.servlet-ext
  (:use ring.middleware.session.store)
  (:import [javax.servlet.http HttpServletRequest HttpSession]
           java.util.UUID
           java.security.Principal))

(declare ^:private ^:dynamic ^HttpSession *session*)

(defn- assert-session []
  (assert *session* "session is not bound. Please include wrap-with-session middleware."))

(deftype HttpSessionStore [session-key]
  SessionStore
  (read-session [store key]
                (assert-session)
                (or (.getAttribute *session* key) {}))
  (write-session [store key data]
                 (assert-session)
                 (let [key (or key (str (UUID/randomUUID)))]
                   (.setAttribute *session* key data)
                   key))
  (delete-session [store key]
                  (assert-session)
                  (.removeAttribute *session* key)
                  nil))

(defn httpsession-store
  ([] (httpsession-store "_http_session"))
  ([session-key] (HttpSessionStore. session-key)))


(defn- in-role? [^HttpServletRequest servlet-req allow-roles]
  (if (empty? allow-roles)
    true
    (loop [remaining-roles allow-roles]
      (if (empty? remaining-roles)
        false
        (or (.isUserInRole servlet-req (first remaining-roles))
            (recur (rest remaining-roles)))))))

(defn without-contextpath [handler]
  "Remove leading context path from URI"
  (fn [request]
    (if-let [^HttpServletRequest servlet-req (:servlet-request request)]
      (if-let [uri (.getRequestURI servlet-req)]
        (handler (assoc request :uri (.substring uri (.length (.getContextPath servlet-req)))))
        (handler request))
      (handler request))))

(defn wrap-userprincipal [handler & [ & {:keys [allow-roles]}]]
  "Wrap request with user principal.
  If a userprincipal is available, request is associated with
  a user (:username) and a predicate that given a role name returns true
  or false depending on wheter the user is in that role or not."
  (let [error-response {:status 403
                        :headers {"content-type" "text/plain"}
                        :body "Not authorized."}]

    (fn [request]
      (let [^HttpServletRequest servlet-req (:servlet-request request)
            ^Principal principal (if servlet-req
                                   (.getUserPrincipal servlet-req)
                                   nil)]
        (if principal
          (if (in-role? servlet-req allow-roles)
            (handler (assoc request :username (.getName principal)
                            :in-role? #(.isUserInRole servlet-req %)))
            error-response)
          (if allow-roles
            error-response
            (handler (assoc request :username nil
                            :in-role? (fn [role] false)))))))))

(defn wrap-with-session [handler]
  (fn [request]
    (if-let [^HttpServletRequest servlet-req (:servlet-request request)]
      (binding [*session* (.getSession servlet-req)]
        (handler request))
      (handler request))))
