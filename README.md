> **GETTING STARTED:** You must start from some combination of the CSV Sprint code that you and your partner ended up with. Please move your code directly into this repository so that the `pom.xml`, `/src` folder, etc, are all at this base directory.

> **IMPORTANT NOTE**: In order to run the edu.brown.cs.student.main.server, run `mvn package` in your terminal then `./run` (using Git Bash for Windows users). This will be the same as the first Sprint. Take notice when transferring this run sprint to your Sprint 2 implementation that the path of your Server class matches the path specified in the run script. Currently, it is set to execute Server at `edu/brown/cs/student/main/edu.brown.cs.student.main.server/Server`. Running through terminal will save a lot of computer resources (IntelliJ is pretty intensive!) in future sprints.

# Project Details
Here is my github link for server: https://github.com/cs0320-s24/csv-AshtonGlover.git. For this project, I had to handle four
different endpoints for a web server including loadcsv, searchcsv, viewcsv, and broadband. These four endpoints had different
query paramaters that will be further specified below. 

# Design Choices
I started my copying over most of CSV files, except for the main class, as that contained the code for the REPL structure of
CSV that I no longer needed. I then basically divided the functionality into two packages, one for CSV and one for Server. 
The server package actually handled the endpoints and data collection, while the CSV package just contained the old files. I
chose to create different classes that implemented the route interface for each interface, all of which defined what should
happen upon a query to that specific endpoint. Furthermore, I created a class that basically handled the problem of ensuring
that a CSV had actually been loaded prior to searching or viewing, and it allowed subsequent calls to loadcsv to change the 
stored CSV. I also made a class to handle the data retrieving of state and county information from the ACS API to avoid having
to do this every time the endpoint was used. 

# Errors/Bugs
There are no bugs or errors that I am aware of. 

# Tests
I split my tests into two files, one for testing the broadband part (ACSTest) and one for CSV related endpoints (CSVTest). 
I tried to handle a variety of edge cases for both, and I utilized both mocked data and real data from the API for testing 
the broadband endpoint. To run these tests, simply type mvn package in the terminal. 

# How to
As stated above, all you have to do to run the tests is type mvn package. After that, you can either run ./run in the terminal,
or press the green play button with Server.java as the selected file to run the server. Upon running the server, a link will
be displayed in the terminal that guides you to the server. This server handles four endpoints, loadcsv, searchcsv, viewcsv,
and broadband. loadcsv has a query paramter of filepath, so you must specify the filepath when using this to load a csv. If
successful, a success message will be displayed. Only after loading can searchcsv and viewcsv be used. For viewcsv, simply
type viewcsv as an endpoint with no query paramter to display the loaded csv. For searchcsv, the required query parameters are
hasheaders (which should be true or false), numcolumns (which should be an integer), and value (which is the value to search
for). Furthermore, you can specify if you want to search a specific column or header using the index query paramter. This is
optional, however. This will search the loadedcsv for an instance of value, and display the row that the value was found or 
a message saying the value was not located. The last endpoint is broadband. Broadband has two query paramters, state and
county, both of which must be specified for use. These are fairly self explanatory: for state, just type the state you want to
search and then also specify the county in the format "county, state". Doing this will either yield some sort of error due
to the location not being found, or a message displaying the time of the search and the estimated broadband access in that
location. 
