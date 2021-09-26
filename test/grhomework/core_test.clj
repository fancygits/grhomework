(ns grhomework.core-test
  (:require [clojure.test :refer :all]
            [grhomework.core :refer :all]))

(deftest test-prints-table-ok
  (testing "Basic process of slurping delimited file and displaying."
    (let [test-data (slurp-csv "test-data-1.csv" \|)
          expected-output (slurp "resources/expected-output-test-prints-table-ok.txt")]
      (binding [*out* (java.io.StringWriter.)]
        (print-data-table test-data)
        (is (= expected-output (.toString *out*)))))))

