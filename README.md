grhomework
==========

generating test data
--------------------

First, to generate a "primer" file, Visit dumbdata.com. Choose these four
colums:

* Last name
* First name
* Email
* Birth date

Select `CSV`, choose a rowcount (e.g., 90), arrange the column order as
per above, and generate a file.

Then, to create the three target test data files, each with a distinct
delimiter, and with a `FavoriteColor` column, run `lein repl`, then run:

    grhomework.core=> (generate-test-data-files "primer-data.csv")

Running tests
-------------

Run tests like so:

    lein test

Step 1
------

First, check that you have the three necessary data files:

* `data-1.csv` (delimiter: " | ")
* `data-2.csv` (delimiter: ", ")
* `data-3.csv` (delimiter: " ")

Run and display the combined, sorted data like so -- please note that the
delimiter you pass as an arg should consist of a single character:

    lein run data-1.csv "|" data-2.csv "," data-3.csv " "


