(ns ring-userprincipal-middleware.dev
    (:import javax.servlet.http.HttpServletRequest
             java.security.Principal))


(defn- wrap-with-fake-user [handler fake-username fake-roles]
  "Wrap request with fake user and fake roles. Useful while developing."
  (let [fake-user (proxy [java.security.Principal][]
		    (getName [] fake-username))
	fake-request (proxy [javax.servlet.http.HttpServletRequest] []
		       (getUserPrincipal [] fake-user)
		       (isUserInRole [role] (contains? fake-roles role)))]
    (fn [request]
      (handler (assoc request :servlet-request fake-request)))))
