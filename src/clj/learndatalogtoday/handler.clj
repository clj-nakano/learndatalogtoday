(ns learndatalogtoday.handler
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [compojure.core :refer [routes GET POST]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [datomic-query-helpers.core :refer [check-query
                                                normalize
                                                pretty-query-string]]
            [datomic.api :as d]
            [environ.core :refer [env]]
            [fipp.edn :as fipp]
            [hiccup.page :refer [html5]]
            [learndatalogtoday.views :as views]
            [ring.adapter.jetty :as jetty]
            [taoensso.timbre :as log]
            [tutorial.fns])
  (:import [java.util Date]))

(def dev? (boolean (env :devmode)))

(defn edn-response [edn-data]
  {:status 200
   :headers {"Content-Type" "application/edn"}
   :body (pr-str edn-data)})

(defn localized-edn [v]
  (let [local (-> v
                  (subs 0 (- (count v) (count ".edn")))
                  (str (format "_%s.edn" (env :pagelang))))]
    (if (.exists (io/file local))
      local
      v)))

(defn read-file
  [s]
  (-> (localized-edn s)
      slurp
      read-string))

(defn read-chapter-data [chapter]
  (->> chapter
       (format "resources/chapters/chapter-%s.edn")
       read-file))

(defn read-chapter
  "Returns a html string"
  [chapter]
  (let [chapter-data (read-chapter-data chapter)]
    (assoc chapter-data
           :html (views/chapter-response (assoc chapter-data
                                                :chapter chapter)))))

(def whitelist '#{< > <= >= not= = tutorial.fns/age .getDate .getMonth
                  movie-year sequels friends avg min max sum count})

(defn validate [[query & args]]
  (let [syms (check-query query args whitelist)]
    (if (empty? syms)
      (cons (normalize query) args)
      (throw (ex-info (str "Non-whitelist symbol used in query/args: " syms
                           ". The symbol whitelist is " whitelist)
                      {:syms syms})))))

(def toc (if dev? views/toc (memoize views/toc)))
(def read-chapter-data (if dev? read-chapter-data (memoize read-chapter-data)))
(def read-chapter (if dev? read-chapter (memoize read-chapter)))

(defn app-routes [db]
  (routes
   (GET "/"
     []
     (toc))

   (GET ["/chapter/:n" :n #"[0-9]+"]
     [n]
     (:html (read-chapter (Integer/parseInt n))))

   (GET ["/query/:chapter/:exercise" :chapter #"[0-9]+" :exercise #"[0-9]+"]
     {{:keys [chapter exercise data] :as params} :params}
     (try
       (let [chapter (Integer/parseInt chapter)
             exercise (Integer/parseInt exercise)
             usr-input (edn/read-string data)
             ans-input (get-in (read-chapter-data chapter) [:exercises exercise :inputs])
             [ans-query & ans-args] (validate (map #(or (:correct-value %1) %2) ans-input usr-input))
             [usr-query & usr-args] (validate (edn/read-string data))
             usr-result (apply d/q usr-query db usr-args)
             ans-result (apply d/q ans-query db ans-args)]
         (if (= usr-result ans-result)
           (do (log/info (format "Success query [%s,%s]: %s" chapter exercise (pr-str usr-input)))

               (edn-response {:status :success
                              :result usr-result}))
           (do (log/info (format "Fail query [%s,%s]: %s" chapter exercise (pr-str usr-input)))
               (edn-response {:status :fail
                              :result usr-result
                              :correct-result ans-result}))))
       (catch Exception e
         (let [msg (.getMessage e)]
           (log/info (format "Error query [%s,%s]. Data: %s Message: %s" chapter exercise data msg))
           (edn-response {:status :error
                          :message (.getMessage e)})))))

   (GET ["/answer/:chapter/:exercise" :chapter #"[0-9]+" :exercise #"[0-9]+"]
     [chapter exercise]
     (try
       (let [chapter (Integer/parseInt chapter)
             exercise (Integer/parseInt exercise)
             ans-input (get-in (read-chapter-data chapter) [:exercises exercise :inputs])
             value #(or (:correct-value %) (:value %))
             answer (map (fn [input]
                           (condp = (:type input)
                             :query (pretty-query-string (value input))
                             :rule (with-out-str
                                     (fipp/pprint (value input)))
                             :value (with-out-str
                                      (fipp/pprint (value input)))))
                         ans-input)]
         (do (log/info (format "Answer request [%s,%s]: %s" chapter exercise (seq answer)))
             (edn-response answer)))))

   (route/resources "/")
   (route/not-found "Not Found")))

(defn init-db [name schema seed-data]
  (let [uri (str "datomic:mem://" name)
        conn (do (d/delete-database uri)
                 (d/create-database uri)
                 (d/connect uri))]
    @(d/transact conn schema)
    @(d/transact conn seed-data)
    (d/db conn)))

(def app
  (let [schema (read-file "resources/db/schema.edn")
        seed-data (read-file "resources/db/data.edn")
        db (init-db "movies" schema seed-data)]
    (handler/site (-> (app-routes db)))))

(defn -main []
  (let [port (Integer/parseInt (or (env :port) "8080"))]
    (jetty/run-jetty app {:port port :join? false})))
