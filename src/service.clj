(ns service
  (:require [datomic.api :as d]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]))

(defn is-datomic-up? [{{:keys [db-uri]} :query-params}]
  (try
    (d/create-database db-uri)
    (d/connect db-uri)
    {:status 200 :body "Yes!"}
    (catch Exception _
      {:status 503 :body "Not yet!"})))

(def routes
  (route/expand-routes
   #{["/check-datomic" :get is-datomic-up?
      :route-name :check-datomic]}))

(defn create-server []
  (http/create-server {::http/routes routes
                       ::http/type   :jetty
                       ::http/port   8090}))

(defn start! [& _]
  (http/start (create-server)))