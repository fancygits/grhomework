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
  (reset! datastore [])
  (testing "Ensure /records POST endpoint stores data succesfully"
    (let [expected-datastore [{"LastName" "Mcmurray", "FirstName" "Rolf", "Email" "exhortationsbabblestopss@trickierAudragyrations.edu", "FavoriteColor" "green", "DateOfBirth" "8/15/2007"}] 
          mock-request {:headers {:content-type "text/plain"}, :server-port 3000, :content-length 6, :form-params {}, :query-params {},  :character-encoding nil, :uri "/records", :body "resources/test-row-1.txt", :multipart-params {}, :scheme :http, :request-method :post}
          resp (handle-post-ingest-one-entry mock-request) ]
        (and
          (is (= (:status resp) 200))
          (is (= @datastore expected-datastore))))))

(deftest test-get-endpoints
  (reset! datastore [])
  ;; load some data first
  (let [mock-post-1 {:headers {:content-type "text/plain"}, :server-port 3000, :content-length 6, :form-params {}, :query-params {},  :character-encoding nil, :uri "/records", :body "resources/test-row-1.txt", :multipart-params {}, :scheme :http, :request-method :post}
        mock-post-2 {:headers {:content-type "text/plain"}, :server-port 3000, :content-length 6, :form-params {}, :query-params {},  :character-encoding nil, :uri "/records", :body "resources/test-row-2.txt", :multipart-params {}, :scheme :http, :request-method :post}
        mock-post-3 {:headers {:content-type "text/plain"}, :server-port 3000, :content-length 6, :form-params {}, :query-params {},  :character-encoding nil, :uri "/records", :body "resources/test-row-3.txt", :multipart-params {}, :scheme :http, :request-method :post}
          _ (route mock-post-1)
          _ (route mock-post-2)
          _ (route mock-post-3) ]

    (testing "Test GET records ordered by color"
      (let [expected-pyld "[{\"LastName\":\"Mcmurray\",\"FirstName\":\"Rolf\",\"Email\":\"exhortationsbabblestopss@trickierAudragyrations.edu\",\"FavoriteColor\":\"green\",\"DateOfBirth\":\"8\\/15\\/2007\"},{\"LastName\":\"Burrier\",\"FirstName\":\"Efrain\",\"Email\":\"overpowernailbrushs@franchisedgateposts.com\",\"FavoriteColor\":\"red\",\"DateOfBirth\":\"10\\/20\\/2008\"},{\"LastName\":\"Edwardsen\",\"FirstName\":\"Kieth\",\"Email\":\"greasedscapegoating@brokeKatmai.edu\",\"FavoriteColor\":\"yellow\",\"DateOfBirth\":\"6\\/5\\/1998\"}]"
            mock-req {:server-port 3000 :uri "/records/color" :scheme :http :request-method :get}
            resp (route mock-req)]
        (and
          (is (= 200 (:status resp)))
          (is (= expected-pyld (:body resp))))))

    (testing "Test GET records ordered by birthdate"
      (let [expected-pyld "[{\"LastName\":\"Edwardsen\",\"FirstName\":\"Kieth\",\"Email\":\"greasedscapegoating@brokeKatmai.edu\",\"FavoriteColor\":\"yellow\",\"DateOfBirth\":\"6\\/5\\/1998\"},{\"LastName\":\"Mcmurray\",\"FirstName\":\"Rolf\",\"Email\":\"exhortationsbabblestopss@trickierAudragyrations.edu\",\"FavoriteColor\":\"green\",\"DateOfBirth\":\"8\\/15\\/2007\"},{\"LastName\":\"Burrier\",\"FirstName\":\"Efrain\",\"Email\":\"overpowernailbrushs@franchisedgateposts.com\",\"FavoriteColor\":\"red\",\"DateOfBirth\":\"10\\/20\\/2008\"}]"
            mock-req {:server-port 3000 :uri "/records/birthdate" :scheme :http :request-method :get}
            resp (route mock-req)]
        (and
          (is (= 200 (:status resp)))
          (is (= expected-pyld (:body resp))))))

    (testing "Test GET records ordered by last name"
      (let [expected-pyld "[{\"LastName\":\"Burrier\",\"FirstName\":\"Efrain\",\"Email\":\"overpowernailbrushs@franchisedgateposts.com\",\"FavoriteColor\":\"red\",\"DateOfBirth\":\"10\\/20\\/2008\"},{\"LastName\":\"Edwardsen\",\"FirstName\":\"Kieth\",\"Email\":\"greasedscapegoating@brokeKatmai.edu\",\"FavoriteColor\":\"yellow\",\"DateOfBirth\":\"6\\/5\\/1998\"},{\"LastName\":\"Mcmurray\",\"FirstName\":\"Rolf\",\"Email\":\"exhortationsbabblestopss@trickierAudragyrations.edu\",\"FavoriteColor\":\"green\",\"DateOfBirth\":\"8\\/15\\/2007\"}]"
            mock-req {:server-port 3000 :uri "/records/name" :scheme :http :request-method :get}
            resp (route mock-req)]
        (and
          (is (= 200 (:status resp)))
          (is (= expected-pyld (:body resp))))))
))

