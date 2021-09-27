(ns grhomework.core-test
  (:require [clojure.test :refer :all]
            [grhomework.core :refer :all]))

(deftest test-prints-table-ok
  (testing "Basic process of slurping delimited file and displaying."
    (let [test-data (slurp-and-process-csv "resources/test-data-1.csv" \|)
          expected-output (slurp "resources/expected-output-test-prints-table-ok.txt")]
      (binding [*out* (java.io.StringWriter.)]
        (print-data-table test-data)
        (is (= expected-output (.toString *out*)))))))

(deftest test-sorting
  (testing "Sorting by color then by last name"
    (let [data (slurp-mult-files
                ["resources/test-data-1.csv" "|"
                 "resources/test-data-2.csv" ","])
          sorted (sort-by-color-then-last-name data) 
          expected-output (clojure.edn/read-string
            (slurp "resources/expected-output-sort-by-color-then-last-name.txt"))]
      (is (= expected-output sorted))))
  (testing "Sorting by dob"
    (let [data (slurp-mult-files
                ["resources/test-data-1.csv" "|"
                 "resources/test-data-2.csv" ","])
          sorted (sort-by-dob data) 
          expected-output (clojure.edn/read-string
            (slurp "resources/expected-output-sort-by-dob.txt"))]
      (is (= expected-output sorted))))
  (testing "Sorting by last name descending"
    (let [data (slurp-mult-files
                ["resources/test-data-1.csv" "|"
                 "resources/test-data-2.csv" ","])
          sorted (sort-by-last-name-descend data) 
          expected-output (clojure.edn/read-string
            (slurp "resources/expected-output-sort-by-last-name-descend.txt"))]
      (is (= expected-output sorted))))
)

(deftest test-slurp-sort-display
  (testing "Step 1 process of slurp, sort, and printing table works ok"
    (let [inputs ["resources/test-data-1.csv" "|" "resources/test-data-2.csv" ","]
         expected-output (slurp "resources/expected-output-sort-slurp-display.txt")]
      (binding [*out* (java.io.StringWriter.)]
        (slurp-sort-display inputs)
        (is (= expected-output (.toString *out*)))))))

(deftest test-post-endpoint
  (testing "Ensure /records POST endpoint stores data succesfully"
    (let [expected-datastore [{"LastName" "Mcmurray", "FirstName" "Rolf", "Email" "exhortationsbabblestopss@trickierAudragyrations.edu", "FavoriteColor" "green", "DateOfBirth" "8/15/2007"}] 
          mock-request {:headers {:content-type "text/plain"}, :server-port 3000, :content-length 6, :form-params {}, :query-params {},  :character-encoding nil, :uri "/records", :body "resources/test-row-1.txt", :multipart-params {}, :scheme :http, :request-method :post}
          resp (handle-post-ingest-one-entry mock-request) ]
        (and
          (is (= (:status resp) 200))
          (is (= @datastore expected-datastore))))))

