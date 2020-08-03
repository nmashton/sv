(defproject sv "0.1.0-SNAPSHOT"
  :description "A simple pair of apps for working with structured text files"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :main sv.cmd
  :dependencies [[cheshire "5.10.0"]
                 [compojure "1.6.1"]
                 [clojure.java-time "0.3.2"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/test.check "1.1.0"]
                 [org.clojure/tools.cli "1.0.194"]
                 [ring/ring-core "1.8.1"]
                 [ring/ring-jetty-adapter "1.6.3"]]
  :profiles {:api {:main sv.cmd.api}
             :cli {:main sv.cmd.cli}}
  :aliases {"api" ["with-profile" "api" "run"]
            "cli" ["with-profile" "cli" "run"]}
  :plugins [[lein-cloverage "1.1.2"]]
  :repl-options {:init-ns sv.core})
