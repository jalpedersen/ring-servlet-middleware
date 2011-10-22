(ns ring.middleware.servlet-ext.dev
    (:import javax.servlet.http.HttpServletRequest
             javax.servlet.http.HttpSession
             java.security.Principal))
(def ^:dynamic *session-store* (atom {}))

(defn wrap-with-fake-user [app fake-username fake-roles]
  "Wrap request with fake user and fake roles. Useful while developing."
  (let [dummy-session (proxy [HttpSession] []
                        (getAttribute [key] (@*session-store* key))
                        (setAttribute [key, val] (swap! *session-store* assoc key val)))
	fake-user (proxy [Principal] []
                (getName [] fake-username))
	fake-request (proxy [HttpServletRequest] []
                (getSession [& create] dummy-session)
	        (getUserPrincipal [] fake-user)
                (isUserInRole [role] (contains? fake-roles role)))]
    (fn [request]
      (app (assoc request :servlet-request fake-request)))))
