# klarna-dojo-quil

Klarna Coding Dojo - Quil in ClojureScript

## Setup

You'll need the following tools to participate in this dojo:
* Git
* Java 8 JDK
* Leiningen
* A text editor. [Nightcode](https://sekao.net/nightcode/) is a nice batteries-included Clojure IDE. IntelliJ users may enjoy [Cursive](https://cursive-ide.com/).

ClojureBridge has a nice [Getting Set Up](https://github.com/ClojureBridge/curriculum/blob/gh-pages/outline/setup.md) page to guide you through installing the above.

Once you have the tools, clone this Git repo:

    git clone https://github.com/jmglov/klarna-dojo-quil.git

To get an interactive development environment:

    cd klarna-dojo-quil
    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/). This will auto compile and send all changes to the browser without the need to reload. After the compilation process is complete, you will get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

## License

Copyright © 2016 Josh Glover <jmglov@gmail.com>

Distributed under the MIT License.
