# NasneMonitorApp

このアプリを動かすには Oracle Stream Explorer と SOA 対応版の JDeveloper が必要です。

下記のリンクからダウンロードできます。

http://www.oracle.com/technetwork/jp/middleware/complex-event-processing/downloads/index.html

pom.xml はありますが、Stream Explorer に含まれているライブラリが足りないため maven を使ってビルドはできません。
下記のコマンドで依存関係のあるサードパーティライブラリを lib ディレクトリへコピーされるため、そのライブラリをビルドパスに追加します。
```
mvn clean
```
