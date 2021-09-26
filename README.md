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

Then, to create the three target test data files with a `FavoriteColor`
column, run `lein repl`, then run:

    grhomework.core=> (generate-test-data-files "primer-data.csv")


