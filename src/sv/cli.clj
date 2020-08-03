(ns sv.cli
  (:require [clojure.pprint :refer [print-table]]
            [sv.file :as file]
            [sv.model :as model]
            [sv.parse :as parse]))

(defn check-filenames
  [filenames]
  (seq (file/filenames-with-errors filenames)))

(defn handle-filenames
  [filenames options]
  (let [[records errors] (parse/combine-parse-results (map parse/parse-filename filenames))
        sorter (model/sorter-for-key (:sort-by options))]
    (if (and (seq errors)
             (not (:ignore-errors options)))
      {:error "Some files had errors."}
      {:output (with-out-str
                 (print-table (->> records
                                   sorter
                                   (map model/record->display))))})))