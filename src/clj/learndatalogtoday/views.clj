(ns learndatalogtoday.views
  (:require [clojure.java.io :as io]
            [datomic-query-helpers.core :refer [pretty-query-string]]
            [environ.core :refer [env]]
            [fipp.edn :as fipp]
            [hiccup.element :refer [javascript-tag]]
            [hiccup.page :refer [html5 include-js include-css]]
            [learndatalogtoday.i18n :as i18n]
            [markdown.core :as md]
            [taoensso.tempura :as tempura]))

(def tr (partial tempura/tr {:dict i18n/tempura-dictionary} [(keyword (env :pagelang)) :en]))

(def google-analytics-string
  "(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-40247950-2', 'learndatalogtoday.org');
  ga('send', 'pageview');")

(defn footer []
  [:footer.text-center {:style "border-top: 1px solid lightgrey; margin-top: 40px;padding:10px;"}
   [:small
    [:p "英語版 " [:a {:href "http://www.learndatalogtoday.org"} "www.learndatalogtoday.org"]
     "&copy; 2013 - 2016 Jonas Enlund"]
    [:p "日本語版 " [:a {:href "https://learn-datalog-today-ja.herokuapp.com/"} "learn-datalog-today-ja.herokuapp.com"]
     "Kenji Nakamura"]
    [:p "英語版 | "
     [:a {:href "https://github.com/jonase/learndatalogtoday"} "github"] " | "
     [:a {:href "http://lispinsummerprojects.org/"} "lispinsummerprojects.org"]]
    [:p "日本語版 | "
     [:a {:href "https://github.com/clj-nakano/learndatalogtoday"} "github"]]]])

(defn row [& content]
  [:div.row
   [:div.offset2.span8
    content]])

(defn base [chapter text exercises ecount]
  (list
   [:head
    (include-css "/third-party/bootstrap/css/bootstrap.css")
    (include-css "/third-party/codemirror-3.15/lib/codemirror.css")
    (include-css "/style.css")
    [:title (tr [:learn-datomic-today "Learn Datalog Today!"])]
    [:script google-analytics-string]]
   [:body
    [:div.container
     (row [:div.textcontent text])
     (row (when (> chapter 0)
            [:a {:href (str "/chapter/" (dec chapter))}
             (tr [:previous-chapter "<< Previous chapter"])])
          (when (< chapter 8)
            [:a.pull-right {:href (str "/chapter/" (inc chapter))}
             (tr [:next-chapter "Next chapter >>"])]))
     (row [:div.exercises {:style "margin-top: 14px"} exercises])
     (row (footer))]
    (include-js "/third-party/jquery/jquery-1.10.1.min.js")
    (include-js "/third-party/codemirror-3.15/lib/codemirror.js")
    (include-js "/third-party/codemirror-3.15/mode/clojure/clojure.js")
    (include-js "/third-party/bootstrap/js/bootstrap.js")
    (include-js "/app.js")
    (javascript-tag (format "learndatalogtoday.core.init(%s, %s);" chapter ecount))]))

(defn build-input [tab-n input-n input]
  (let [label (condp = (:type input)
                :query (tr [:query "Query:"])
                :rule (tr [:rules  "Rules:"])
                :value (str "Input #" input-n ":"))
        input-str (condp = (:type input)
                    :query (pretty-query-string (:value input))
                    :rule (with-out-str (fipp/pprint (:value input)))
                    :value (with-out-str (fipp/pprint (:value input))))]
    [:div.span8
     [:div.row
      [:div.span8 [:p [:small [:strong label]
                       (when (= :query (:type input))
                         [:span.pull-right "[ " [:a {:href "#" :class (str "show-ans-" tab-n)} (tr [:give-up "I give up!"])] " ]"])]]]]
     [:div.row
      [:div.span8 [:textarea {:class (str "input-" tab-n)} input-str]]]]))

(defn build-inputs [tab-n inputs]
  (map-indexed (partial build-input tab-n) inputs))

(defn build-exercise [tab-n exercise]
  (list [:div {:class (if (zero? tab-n) "tab-pane active" "tab-pane")
               :id (str "tab" tab-n)}

         (md/md-to-html-string (:question exercise))
         [:div.row.inputs
          (build-inputs tab-n (:inputs exercise))]
         [:div.row
          [:div.span8
           [:button.btn.btn-block {:id (str "run-query-" tab-n)
                                   :data-tab tab-n}
            (tr [:run-query "Run Query"])]]]
         [:div.row
          [:div.span8
           [:div.alerts]
           [:table.table.table-striped.resultset
            [:thead]
            [:tbody]]]]]))

(defn build-exercises [exercises]
  (list [:div.tabbable
         [:ul.nav.nav-tabs
          (for [n (range (count exercises))]
            [:li (when (zero? n) {:class "active"})
             [:a {:href (str "#tab" n)
                  :data-toggle "tab"}
              [:span.label n]]])]
         [:div.tab-content
          (map-indexed build-exercise exercises)]]))

(defn chapter-response [chapter-data]
  (let [text (-> chapter-data :text-file slurp md/md-to-html-string)
        exercises (build-exercises (:exercises chapter-data))
        ecount (count (:exercises chapter-data))
        chapter (:chapter chapter-data)]
    (html5 (base chapter text exercises ecount))))

(defn localized-toc []
  (let [l10n-toc (format "resources/toc_%s.md" (env :pagelang))]
    (if (-> l10n-toc
            io/file
            .exists)
      l10n-toc
      "resources/toc.md")))

(defn toc []
  (html5
   [:head
    (include-css "/third-party/bootstrap/css/bootstrap.css")
    (include-css "/style.css")
    [:title "Learn Datalog Today!"]
    [:script google-analytics-string]]
   [:body
    [:div.container
     (row [:div.textcontent
           (-> (localized-toc) slurp md/md-to-html-string)])
     (row (footer))]
    (include-js "/third-party/jquery/jquery-1.10.1.min.js")
    (include-js "/third-party/bootstrap/js/bootstrap.js")]))
