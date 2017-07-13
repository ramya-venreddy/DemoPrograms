Each of these tests expects a particular brand of database to be up and running, so they
should not be part of a regression test suite.  They should only be run if the database
is available.

Some of these test classes will attempt to import database specific classes.  So they shouldn't
be compiled unless those database class files are available.

  

