# Code Smell Checker（仮）

このプラグインはコードスメルを探し、コードスメルのあるJavaソースコードをリファクタリングを支援します。

## プラグインの使い方

このプラグインは現在（2020/05/23）[JetBrains Plugin Repository](https://plugins.jetbrains.com/)で公開していません（余裕があれば公開予定）。
そのため、使いづらいとは思いますが、もし使用したい場合はcloneしてお使いください。
cloneした後は、以下の手順で使用できると思います。

1. IntelliJ IDEAでcloneしたリポジトリを開く
2. `` View > Tool Windows > Gradle``を選択する
    1. ウィンドウに何も表示されていない場合、左上の更新ボタンを押す
3. `` code_smell_checker_plugin > Tasks > intellij > runIde ``を選択する

以上の操作で、恐らく、現在開いているIDEとは別に、新しくIDEが立ち上がると思います。
このプラグインは、新しく立ち上がったIDEで使用できます。

## 動作確認済み

※あくまでプラグインを使用するためのIDEを立ち上げる環境のことです。立ち上がるIDEはIntelliJ IDEA 2019.3.3固定です。
- IntelliJ IDEA
   - version : 2019.1.4
   - Ultimate、Communityどちらも可
- Gradle
   - version : 5.5.1
- Java
   - version : 11.0.4
   - OpenJDK Runtime Environment AdoptOpenJDK (build 11.0.4+11)
   - OpenJDK 64-Bit Server VM AdoptOpenJDK (build 11.0.4+11, mixed mode)
