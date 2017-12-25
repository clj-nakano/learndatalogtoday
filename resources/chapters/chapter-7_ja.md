# 集計

`sum`、`max`などの集計関数はDatomicのDatalog実装内で既に利用可能になっています。これらの関数は、`:find`句内で使うことができます。

    [:find (max ?date)
     :where
     ...]

集計関数は複数のdatomからの値を集計し、下記の値を返します。

* 単一の値: `min`, `max`, `sum`, `avg`, など。
* 値のコレクション: `(min n ?d)` `(max n ?d)` `(sample n ?e)` など。 `n`はコレクションのサイズを指定する整数。
