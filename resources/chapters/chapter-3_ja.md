# パラメータ化されたクエリ

下記のクエリを見てください。

    [:find ?title
     :where
     [?p :person/name "Sylvester Stallone"]
     [?m :movie/cast ?p]
     [?m :movie/title ?title]]

もしこのクエリを、シルベスター・スタローンだけでなく、どの俳優にでも再利用できれば便利ですよね。
プログラム言語で引数を関数やメソッドに渡すのと同じように、クエリに入力パラメータを提供する`:in`句を使うことでこれが可能になります。


俳優に入力パラメータを使用したクエリはこのようになります。

    [:find ?title
     :in $ ?name
     :where
     [?p :person/name ?name]
     [?m :movie/cast ?p]
     [?m :movie/title ?title]]

このクエリは、２つの引数をとっています。`$`はデータベースそのもので、`:in`句が省略された場合には黙示的に指定されます。
もうひとつは俳優の名前となる`$name`です。

上記のクエリは`(q query db "Sylvester Stallone")`のように実行することができます。
`query`は上でみたクエリで、`db`はデータベースの値となります。
クエリにはいくつでも入力値を与えることができます。

上記のクエリでは、入力パターン変数`?name`はスカラ値、この場合は文字列型に束縛されています。
入力値にはスカラ、タプル、コレクションとリレーションの４種類があります。

## 余談

ちょっと待ってください。`$`はどこで使われているのでしょうか？
クエリの中のデータパターンは、実は下記のフォーマットをとる**5要素のタプル**なのです。

    [<database> <entity-id> <attribute> <value> <transaction-id>]

`in`句の第一引数と同様、`database`の部分は黙示的です。下記のクエリは機能的には上記のクエリと同一です。

    [:find ?title
     :in $ ?name
     :where
     [$ ?p :person/name ?name]
     [$ ?m :movie/cast ?p]
     [$ ?m :movie/title ?title]]

## タプル

タプルは例えば`[?name ?age]`のように記述され、入力値を分配束縛したい場合に用いることができます。
例えば、`["James Cameron" "Arnold Schwarzenegger"]`というベクタがあるとして、これをこの２人が
一緒に仕事をした映画を探すための入力値として、下記のように使うことができます。

    [:find ?title
     :in $ [?director ?actor]
     :where
     [?d :person/name ?director]
     [?a :person/name ?actor]
     [?m :movie/director ?d]
     [?m :movie/cast ?a]
     [?m :movie/title ?title]]

この場合は、タプルではなく、２つの独立した入力値を使うこともできるでしょう。

    :in $ ?director ?actor

## コレクション

クエリ内で**論理和**を実装するためにコレクションの分配束縛を利用することができます。
例えばJames CameronかRidley Scottの監督作品を全て検索したいとすると、

    [:find ?title
     :in $ [?director ...]
     :where
     [?p :person/name ?director]
     [?m :movie/director ?p]
     [?m :movie/title ?title]]

この場合、`?director`パターン変数には”James Cameron"と"Ridley Scott"の両方が束縛されます。`?director`の後ろにある"..."はコードの省略形ではなく、定数であることに注意してください。

## リレーション

タプルのセットで構成されるリレーションは、最も興味深く、パワフルな入力値タイプです。
なぜなら、これにより外部のリレーションをデータベース内のdatomとジョインすることができるからです。

簡単な例として、`[タイトル 興行収入]`のタプルを使ったリレーションを考えてみましょう。

    [
     ...
     ["Die Hard" 140700000]
     ["Alien" 104931801]
     ["Lethal Weapon" 120207127]
     ["Commando" 57491000]
     ...
    ]

このデータとデータベース内のデータを使って、特定の監督の興行収入を検索します。

    [:find ?title ?box-office
     :in $ ?director [[?title ?box-office]]
     :where
     [?p :person/name ?director]
     [?m :movie/director ?p]
     [?m :movie/title ?title]]

`?box-office`パターン変数は`:where`句内のどこにも現れていないことに注目してください。
