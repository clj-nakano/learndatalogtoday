# データパターン

前章で、`:where`句に続くベクタとして記述される**データパターン**について学びました。
例：`[?e :movie/title "Commando"]`

`:where`句は様々なデータパターンをとることができます。

    [:find ?title
     :where
     [?e :movie/year 1987]
     [?e :movie/title ?title]]

ここで重要なのは、パターン変数`?e`が両方のデータパターンで使われている点です。
パターン変数が複数の場所で使われている場合、クエリエンジンはそれぞれの場所で同じ値を束縛するよう要求します。
したがって、上記のクエリは1987年に公開された映画のタイトルだけを検索します。

データパターンの順序は、クエリの速さを考慮しなければ、結果には影響を与えないので、下記のように記述することもできます。

    [:find ?title
     :where
     [?e :movie/title ?title]
     [?e :movie/year 1987]]

どちらの場合も全く同じ結果を返します。

例えば、映画"Lethal Weapon"の出演者を調べたいとします。
そのためには3つのデータパターンが必要になります。
最初は"Lethal Weapon"をタイトルに持つ映画のエンティティIDを見つけます。

    [?m :movie/title "Lethal Weapon"]

`?m`に束縛された同じエンティティIDを使って、出演者を見つけることができます。

    [?m :movie/cast ?p] 

このパターンでは、`?p`がpersonエンティティのIDになるので、そこから名前を取得することができます。

    [?p :person/name ?name] 

よって、クエリは下記のようになります。

    [:find ?name
     :where
     [?m :movie/title "Lethal Weapon"]
     [?m :movie/cast ?p]
     [?p :person/name ?name]]
