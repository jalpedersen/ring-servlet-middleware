(ns ring.middleware.servlet-ext.test.core
  (:use ring.middleware.servlet-ext
        ring.middleware.servlet-ext.dev
        clojure.test))

(defn- testapp [request]
  {:status  200
   :headers {"Content-Type" "text/plain"}
   :custom  {:valid-role ((:in-role? request) "valid")
             :invalid-role ((:in-role? request) "invalid")}
   :body    (:username request)})

(def wrapped-no-auth
  (-> #'testapp
    (wrap-userprincipal)))

(def wrapped-no-auth-no-principal
  (-> #'testapp
    (wrap-userprincipal :required-roles [])))

(def wrapped-no-roles
  (-> #'testapp
    (wrap-userprincipal :required-roles [])
    (wrap-with-fake-user "monty" #{"valid"})))

(def wrapped-with-valid-roles
  (-> #'testapp
    (wrap-userprincipal :required-roles ["valid"])
    (wrap-with-fake-user "monty" #{"valid"})))

(def wrapped-with-invalid-roles
  (-> #'testapp
    (wrap-userprincipal :required-roles ["not-valid"])
    (wrap-with-fake-user "monty" #{"valid"})))

(deftest test1
         (is (= 200 (:status (wrapped-no-auth {})))))

(deftest test2
         (is (= 403 (:status (wrapped-no-auth-no-principal {})))))

(deftest test3
         (let [response (wrapped-no-roles {})]
           (is (= 200 (:status response)))
           (is (= true ((:custom response) :valid-role)))
           (is (= false ((:custom response) :invalid-role)))
           (is (= "monty" (:body response)))))

(deftest test4
         (is (= 200 (:status (wrapped-with-valid-roles {})))))

(deftest test5
         (is (= 403 (:status (wrapped-with-invalid-roles {})))))

