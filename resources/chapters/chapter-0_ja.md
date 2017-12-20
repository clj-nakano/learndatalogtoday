# Extensible Data Notation

Datomicでは、Datalogクエリは[extensible data notation (edn, 発音はˈidən)](http://edn-format.org)で記述されています。
EDNはJSONと同様のデータ表現形式ですが、

* ユーザ定義の型で拡張可能
* より多くの基本型が提供されている
* [Clojure](http://clojure.org)データのサブセット
という特徴を備えています。

EDNは下記の要素で構成されています。

* 数値型: `42`, `3.14159`
* 文字列型: `"This is a string"`
* キーワード: `:kw`, `:namespaced/keyword`, `:foo.bar/baz`
* シンボル: `max`, `+`, `?title`
* ベクタ: `[1 2 3]` `[:find ?foo ...]`
* リスト: `(3.14 :foo [:bar :baz])`, `(+ 1 2 3 4)`
* タイムスタンプ: `#inst "2013-02-26"`

他にもいくつかありますが、本チュートリアルでは不要なので割愛します。

サンプルデータベースに登録されている映画の全タイトルを取得するクエリの例

    [:find ?title
     :where 
     [_ :movie/title ?title]]

クエリは４つの要素からなるベクタであることに注目してください。

* キーワード `:find`
* シンボル `?title`
* キーワード `:where`
* ベクタ `[_ :movie/title ?title]`

クエリのひとつひとつについては後にふれますが、とりあえず
上記のクエリをそのまま下のテキストボックスに入力し、
**クエリを実行** ボタンを押し、次の章に進んでください。
