(ns grhomework.core
  [:require
    [clojure.java.io  :as io]
    [clojure.set      :as cset]
    [clojure.string   :as s]
    [clojure.data.csv :as csv]])

(def headers
  ["LastName", "FirstName", "Email", "FavoriteColor", "DateOfBirth"])

(def colors ["red" "orange" "yellow" "green" "blue" "indigo" "violet"])

(defn slurp-csv [fname]
  (let [data (csv/read-csv (clojure.string/trim (slurp fname)))
        csv-headers (first data)]
    (map #(zipmap csv-headers %) (rest data))))

(defn sort-cols [data]
  "Turn all maps in data into sorted maps having  
   desired column order using `headers`." 
  (mapv
    #(into 
      (sorted-map-by
        (fn [a b]
          (compare (.indexOf headers a)
                   (.indexOf headers b))))
        %) data))

(defn add-colors [data]
  "Assign random color to each row in data"
  (map #(assoc % "FavoriteColor" (colors (rand-int 7))) data))

(defn write-delimited-data-file [data fpath sep]
  (let [header-row (s/join sep (keys (first data)))]
    (with-open [fout (io/writer fpath)]
      (.write fout (str header-row "\n"))
      (doseq [row data]
        (.write fout (str (s/join sep (vals row)) "\n"))))))

(defn generate-test-data-files [primer-fname]
  "Takes a primer csv (generated from dumbdata.com; see README),
  adds a `color` column, then splits into 3 distinct files,
  each with differing separators, each w/ 30 rows."
  (let [rawdata- (slurp-csv primer-fname)
        rawdata (map #(cset/rename-keys % {"namelast" "LastName"
                                           "namefirst" "FirstName"
                                           "email" "Email"
                                           "birthdate" "DateOfBirth"})
                                          rawdata-)
        rowcount (count rawdata)
        target-rowcount (java.lang.Math/round (float (/ rowcount 3)))
        data (add-colors rawdata)
        data-1 (sort-cols (take target-rowcount data))
        data-2 (sort-cols (take target-rowcount (drop target-rowcount data)))
        data-3 (sort-cols (take target-rowcount (drop (* 2 target-rowcount) data)))]
    (do
      (write-delimited-data-file data-1 "data-1.csv" " | ")
      (write-delimited-data-file data-2 "data-2.csv" ", ")
      (write-delimited-data-file data-3 "data-3.csv" " ")))
  (println "Generated data-1.csv, data-2.csv, and data-3.csv."))

