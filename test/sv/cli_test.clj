(ns sv.cli-test
  (:require [clojure.test :refer [deftest is]]
            [sv.cli :as cli]
            [sv.testutil :as testutil]))

(deftest tabulate-results
  (is (= (str "\n| :last-name | :first-name |   :gender | :favorite-color | :date-of-birth |\n"
              "|------------+-------------+-----------+-----------------+----------------|\n"
              "|     Ashton |        Neil |      male |          indigo |     08/16/1984 |\n"
              "|    Bashton |        Beil |    female |          indigo |     08/15/1984 |\n"
              "|    Cashton |        Ceil | nonbinary |          indigo |     08/14/1984 |\n"
              "|    Dashton |        Deil |      male |          indigo |     08/13/1984 |\n")
         (cli/tabulate-results testutil/example-records))))

(def filenames
  ["resources/test.csv"
   "resources/test.psv"
   "resources/test.ssv"])
(deftest handle-filenames
  (is (= {:error "Some files had errors."}
         (cli/handle-filenames filenames {:sort-by :name})))
  (is (= {:output (str "\n"
                       "| :last-name | :first-name |   :gender | :favorite-color | :date-of-birth |\n"
                       "|------------+-------------+-----------+-----------------+----------------|\n"
                       "|  mBrashton |        Leil |      male |         crimson |     08/20/1984 |\n"
                       "|   Smashton |        Ceil | nonbinary |          indigo |     08/19/1984 |\n"
                       "|   Drashton |        Peil |    female |         crimson |     08/18/1984 |\n"
                       "|   Blashton |        Zeil |      male |         crimson |     08/16/1984 |\n"
                       "|   Blashton |        Meil |    female |          indigo |     08/21/1984 |\n"
                       "|     Ashton |        Neil | nonbinary |          indigo |     08/17/1984 |\n")}
         (cli/handle-filenames filenames {:ignore-errors true
                                          :sort-by :name}))))