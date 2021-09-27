grhomework
==========

Requirements
------------

See [gr-homework.pdf](doc/gr-homework.pdf).

Generating test data
--------------------

(Note that some ready-to-go test data already exists in `resources/`.)

First, generate a "primer" file. Visit dumbdata.com; choose these four
colums:

* Last name
* First name
* Email
* Birth date

Select `CSV`, choose a rowcount (e.g., 90), arrange the column order as
per above, and generate a file.

Then, to create the three target test data files, each with a distinct
delimiter, and with a `FavoriteColor` column, run `lein repl`, then run:

    (generate-test-data-files "primer-data.csv")

Running tests
-------------

Run tests like so:

    lein test

Step 1
------

First, check that you have the three necessary data files:

* `resources/data-1.csv` (delimiter: " | ")
* `resources/data-2.csv` (delimiter: ", ")
* `resources/data-3.csv` (delimiter: " ")

(There are also some smaller versions of these files with just
five lines each, prefixed with `test-`.)

Run and display the combined, sorted data like so -- please note that the
delimiter you pass as an arg should consist of a single character (if the
file has spaces around the delimier, that will be handled):

    lein run resources/data-1.csv "|" resources/data-2.csv "," resources/data-3.csv " "

Step 2
------

In Step 2 some HTTP endpoints have been set up for simple reading
and writing of data (stored in-memory).

Start the server (using the repl):

    (def svr (start-svr))

Stop the server:

    (.stop svr)

POSTing a record:

    curl -XPOST  -H"Content-Type:text/plain" -d "Mcmurray | Rolf | exhortationsbabblestopss@trickierAudragyratns.edu | green | 2007-08-15"  http://localhost:3000/records
    curl -XPOST  -H"Content-Type:text/plain" -d "Burrier, Efrain, overpowernailbrushs@franchisedgateposts.com, red, 2008-10-20"  http://localhost:3000/records
    curl -XPOST  -H"Content-Type:text/plain" -d "Edwardsen Kieth greasedscapegoating@brokeKatmai.edu yellow 1998-06-05"  http://localhost:3000/records

The above calls are also stored in `load-some-rows.sh`.

GET all records, sorted by color:

    curl http://localhost:3000/records/color

GET all records, sorted by birthdate:

    curl http://localhost:3000/records/birthdate

GET all records, sorted by last name:

    curl http://localhost:3000/records/name


