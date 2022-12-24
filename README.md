# Micah Worth CSE241 Final Project README
## To run:
Unzip the file, open terminal in the main directory and enter `sh launch.sh` or `java -jar mjw324.jar`

## Interfaces
There are 3 interfaces: Manager (1), Employee (2), and Customer (3).
The application will ask you to enter numbers or values, each according to the respective prompt.
For the easiest way through, start by creating a customer then navigating the customer interface.
After this you can explore the Employee interface. Lastly, use the Manager interface to view the altercations made inside the database.


## Directory
RecordPopulation is the mvn project used to populate the records.
Inside this directory `mvn package; mvn exec:java` can be run to use RecordPopulation.
launch.sh is the shell script used to recompile and run the project.
Manifest.txt is used to specify the main class and jdbc for compilation.
mjw324 is the directory containing DatabaseApplication.java, the source code of the project.
JDBC-Practice is the directory containing Main.java, source code utilizing JDBC functionality (independent from the project)