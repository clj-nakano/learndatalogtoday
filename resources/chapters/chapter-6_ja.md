# 変換関数

**変換関数**はクエリ内で値を変換してその結果をパターン変数に束縛するのに使用できる副作用のない純粋関数またメソッドです。
たとえば、`:db.type/instant`型の`:person/born`という属性があるとします。与えられた誕生日を使って、大体の年齢を計算するのは簡単です。

    (defn age [birthday today]
      (quot (- (.getTime today)
               (.getTime birthday))
            (* 1000 60 60 24 365)))

この関数を使って、**クエリの中で**人物の年齢を計算することができます。

    [:find ?age
     :in $ ?name ?today
     :where
     [?p :person/name ?name]
     [?p :person/born ?born]
     [(tutorial.fns/age ?born ?today) ?age]]

変換関数句は`[(<fn> <arg1> <arg2> ...) <result-binding>]`の形をとります。`<result-binding>`は[3章](/chapter/3)でみたのと同じ束縛形式をとることができます。

* スカラ: `?age`
* タプル: `[?foo ?bar ?baz]`
* コレクション: `[?name ...]`
* リレーション: `[[?title ?rating]]`

変換関数は入れ子にできないことに注意。下記のように書くことはできません。

    [(f (g ?x)) ?a]

代わりに、中間結果を一時的なパターン変数に束縛する必要があります。

    [(g ?x) ?t]
    [(f ?t) ?a]
