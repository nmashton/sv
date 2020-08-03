(ns sv.cli
  (:require [clojure.pprint :refer [print-table]]
            [sv.file :as file]
            [sv.model :as model]
            [sv.parse :as parse]))

(defn check-filenames
  [filenames]
  (seq (file/filenames-with-errors filenames)))

(defn tabulate-results
  "Generate a tabular view of a collection of records
   in their display form."
  [records]
  (with-out-str
    (->> records
         (map model/record->display)
         print-table)))

(defn handle-filenames
  "Given the list of filenames and map of options provided
   by the CLI -main function, generate either an output
   table or an error message."
  [filenames options]
  (let [[records errors] (parse/combine-parse-results (map parse/parse-filename filenames))
        sorter (model/sorter-for-key (:sort-by options))]
    (if (and (seq errors)
             (not (:ignore-errors options)))
      {:error "Some files had errors."}
      {:output (-> records
                   sorter
                   tabulate-results)})))