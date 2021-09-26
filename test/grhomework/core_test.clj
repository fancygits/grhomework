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

(deftest test-sorting
  (testing "Sorting by color then by last name"
    (let [data (slurp-mult-files
                ["test-data-1.csv" "|"
                 "test-data-2.csv" ","])
          sorted (sort-by-color-then-last-name data) 
          expected-output (clojure.edn/read-string
            (slurp "resources/expected-output-sort-by-color-then-last-name.txt"))]
      (is (= expected-output sorted))))
  (testing "Sorting by dob"
    (let [data (slurp-mult-files
                ["test-data-1.csv" "|"
                 "test-data-2.csv" ","])
          sorted (sort-by-dob data) 
          expected-output (clojure.edn/read-string
            (slurp "resources/expected-output-sort-by-dob.txt"))]
      (is (= expected-output sorted))))
  (testing "Sorting by last name descending"
    (let [data (slurp-mult-files
                ["test-data-1.csv" "|"
                 "test-data-2.csv" ","])
          sorted (sort-by-last-name-descend data) 
          expected-output (clojure.edn/read-string
            (slurp "resources/expected-output-sort-by-last-name-descend.txt"))]
      (is (= expected-output sorted))))

(deftest test-slurp-sort-display
  (testing "Step 1 process of slurp, sort, and printing table works ok"
    (let [inputs ["test-data-1.csv" "|" "test-data-2.csv" ","]
         expected-output (slurp "resources/expected-output-sort-slurp-display.txt")]
      (binding [*out* (java.io.StringWriter.)]
        (slurp-sort-display inputs)
        (is (= expected-output (.toString *out*)))))))

)
