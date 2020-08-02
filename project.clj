(defproject sv "0.1.0-SNAPSHOT"
  :description "A simple pair of apps for working with structured text files"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :main sv.cmd
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.cli "1.0.194"]
                 [org.clojure/test.check "1.1.0"]
                 [ring/ring-core "1.8.1"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [cheshire "5.10.0"]
                 [compojure "1.6.1"]
                 [clojure.java-time "0.3.2"]]
  :profiles {:cmd {:main sv.cmd}
             :api {:main sv.api}}
  :aliases {"cmd" ["with-profile" "cmd" "run"]
            "api" ["with-profile" "api" "run"]}
  :plugins [[lein-cloverage "1.1.2"]]
  :repl-options {:init-ns sv.core})
