javac -cp ./ -d ./ mjw324/DatabaseApplication.java
jar cfmv DatabaseApplication.jar Manifest.txt DatabaseApplication.class Customer.class Employee.class Manager.class
rm DatabaseApplication.class
rm Customer.class
rm Employee.class
rm Manager.class
java -jar DatabaseApplication.jar
