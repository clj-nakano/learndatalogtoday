# Datalogを今日学ぼう！

対話形式のDatalogチュートリアルです。[jonase/learndatalogtoday](https://github.com/jonase/learndatalogtoday)からフォークし、i18n/l10n対応を加えて日本語訳をパッケージしたものです。

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

## License

Copyright © 2013 Jonas Enlund

Distributed under the Eclipse Public License, the same as Clojure.
