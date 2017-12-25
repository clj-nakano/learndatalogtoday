# Datalogを今日学ぼう！

対話形式のDatalogチュートリアルです。[jonase/learndatalogtoday](https://github.com/jonase/learndatalogtoday)からフォークし、i18n/l10n対応を加えてJJonas Enlund氏から許諾を受けた上で日本語訳をパッケージしたものです。

英語版の内容は全て翻訳されていますが、将来的には独自に改変、追加を行う予定です。また改変の英語版へのフィードバック、あるいは英語版の更新の反映も保証するものではありませんので、将来的には内容が派生し、日本語版独自のものになりうることをご了承ください。

## Webサイト

* 日本語版は[learn-datalog-today-ja](https://learn-datalog-today-ja.herokuapp.com)からアクセス可能です。
* 英語版は[learn-datalog-today](http://www.learndatalogtoday.org/)からアクセスできます。

## Prerequisites

You will need [Leiningen](https://github.com/technomancy/leiningen) installed.

## Bootstrapping

    $ git clone git@github.com:jonase/learndatalogtoday.git
    $ cd learndatalogtoday
    $ ./fetch-js-deps.sh
    $ lein clean && lein cljsbuild once

## Running the webapp

    $ DEVMODE=true lein ring server

A browser window should open, otherwise visit http://localhost:3000.

## Feedback welcome

You can open issues on the github issue tracker or you can email me your suggestions/bugs/typos/feedback/etc. Pull requests welcome!

日本語版に関しては、Issue, Pull Requestを[clj-nakano/learndatalogtoday](https://learn-datalog-today-ja.herokuapp.com)までお送りください。

## License

Copyright © 2013 Jonas Enlund

Distributed under the Eclipse Public License, the same as Clojure.

Kenji Nakamura was granted to translate and publish in Japanese kindly by Jonas Enlund.
