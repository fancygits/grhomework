(ns grhomework.core
  [:require
    [clojure.java.io  :as io]
    [clojure.pprint   :as pprint]
    [clojure.set      :as cset]
    [clojure.string   :as s]
    [clojure.data.csv :as csv]])

(def headers
  ["LastName", "FirstName", "Email", "FavoriteColor", "DateOfBirth"])

(def colors ["red" "orange" "yellow" "green" "blue" "indigo" "violet"])

(def out-sdf (java.text.SimpleDateFormat. "M/d/yyyy"))

(defn fmt-dt [dt-str]
  "Takes a date str and formats it in desired format"
  (let [in-sdf (java.text.SimpleDateFormat. "yyyy-MM-dd")
        dt (.parse in-sdf dt-str)]
    (.format out-sdf dt)))

(defn slurp-csv
  ([fname]
    (slurp-csv fname \,))
  ([fname sep]
    (let [rawdata- (csv/read-csv (s/trim (slurp fname)) :separator sep)
          ; clean spaces around values (necessary b/c we can
          ; only pass in a char, not a multi-char str, for separator)
          rawdata (map (fn [items] (map #(s/trim %) items)) rawdata-)
          csv-headers (first rawdata)
          data* (map #(zipmap csv-headers %) (rest rawdata))
          ; Reformat date per requirement 
          data (map #(update % "DateOfBirth" fmt-dt) data*)
        ]
      data)))

(defn slurp-mult-files [args]
  "`args` should be filename1, sep1, filename2, sep2 ... etc."
  (let [fnames (take-nth 2 args)
        seps (map first (take-nth 2 (rest args)))
        data (flatten (mapv slurp-csv fnames seps))]
    data))

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
  each with differing separators."
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

(defn sort-by-color-then-last-name [data]
  (sort-by
    (juxt
      #(get % "FavoriteColor")
      #(s/lower-case (get % "LastName")))
    data))

(defn sort-by-dob [data]
  (sort-by 
    #(.parse out-sdf (get % "DateOfBirth")) data))

(defn sort-by-last-name-descend [data]
  (sort-by 
    #(s/lower-case (get % "LastName")) #(compare %2 %1) data))

(defn banner [sz]
  (s/join "" (take sz (repeatedly (fn [] "=")))))

(defn print-data-table [data]
  (pprint/print-table data))

(defn slurp-sort-display [args]
  "Main driver function for Step 1."
  (let [data (slurp-mult-files args)
        sorted1 (sort-by-color-then-last-name data)
        sorted2 (sort-by-dob data)
        sorted3 (sort-by-last-name-descend data)]

    (println (str "\n" (banner 80)))
    (println "Output 1 – sorted by favorite color then by last name"
             "ascending.")
    (println (banner 80))
    (print-data-table sorted1)

    (println (str "\n" (banner 80)))
    (println "Output 2 – sorted by birth date, ascending.")
    (println (banner 80))
    (print-data-table sorted2)

    (println (str "\n" (banner 80)))
    (println "Output 3 – sorted by last name, descending.")
    (println (banner 80))
    (print-data-table sorted3)
  ))

(defn -main [& args]
  (println "\n...::: Step 1 :::...")
  (slurp-sort-display args)
)

