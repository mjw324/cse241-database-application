import java.util.Scanner;
import java.io.*;
import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

// Main class for the Final Project Interface
class DatabaseApplication {
  public static void main (String[] arg) {
    boolean retry = true; // When user inputs incorrect/invalid entries this is set to false
    String user_name = "";
    char [] pwd = {};
    Connection con = null;
    Statement s = null;
    Scanner input = new Scanner(System.in);
    do { // do while + try catch block for login attempt(s) to Edgar1
      try {
        System.out.println("Please input your Oracle username on Edgar1:");
        user_name = input.nextLine();
        System.out.println("Please input your Oracle password on Edgar1:");
        //designed for inputting password without displaying the password:
        Console console = System.console();
        pwd = console.readPassword();
        con = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241","mjw324", "KSI2800!!!");
        s=con.createStatement();
        retry = false;
      } catch(Exception ex) {
        System.out.println("Invalid username/password. Try again.");
      }
    } while (retry);


    // Reset retry boolean for next step: user input for main menu
    retry = true;


    while (retry){
      PrintMainMenu();
      String menuOption = InputParser(input, "[1-4]", "Input a number corresponding with an action. Try again.");
      // No need for try-catch block here, each method handles errors independently
      switch(menuOption) {
        case "1":
          Manager.MenuAction(input, s);
          break;
        case "2":
          Employee.MenuAction(input, s, con);
          break;
        case "3":
          Customer.MenuAction(input, s);
          break;
        case "4":
          retry = false;
          break;
      }
    }
    // Once user exits from menu, the DB statement, connection and scanner close 
    try{
      s.close();
      con.close();
      input.close();
    } catch (Exception ex) {
      System.out.println("Failed to close the DB statement, connection and/or scanner");
    }

  }

  public static String InputParser(Scanner input, String pattern, String errorMessage) {
    // Perpetual while loop until the user input matches the desired regex pattern
    while(true) {
      String line = input.nextLine();
      if(line.matches(pattern)) {
        return line;
      } else {
        System.out.println(errorMessage);
      }
    }
  }
  public static void PrintMainMenu() {
    System.out.printf("\n\nWelcome to Hurts Rent-A-Lemon, where you can rent a painfully bad, cheap car!\n");
    System.out.println("Please input the corresponding number for your role, or exit:");
    System.out.printf("[1]\tHurts Manager\n[2]\tHurts Employee\n[3]\tHurts (not so) Valued Customer\n[4]\tExit\n\n");
  }
}


class Manager {
  public static void PrintMenu() {
      System.out.printf("\nWelcome, Hurts Manager!\n");
      System.out.println("Please input the corresponding number for the desired action:");
      System.out.printf("[1]\tAdd an organization for discounted rates\n[2]\tView all customers\n[3]\tView all current rentals\n[4]\tView all vehicles\n[5]\tView all organizations\n[6]\tView all locations\n[7]\tView all amenities offered\n[8]\tBack\n\n");
  }

  public static void MenuAction(Scanner input, Statement s) {
      boolean retry = true;
      while (retry){
        PrintMenu();
        String menuOption = DatabaseApplication.InputParser(input, "[1-8]", "Input a number corresponding with an action. Try again.");
        try {
          switch(menuOption) {
            case "1":
              addOrganization(input, s);
              break;
            case "2":
              viewCustomers(s);
              break;
            case "3":
              viewRentals(s);
              break;
            case "4":
              viewVehicles(s);
              break;
            case "5":
              viewOrganizations(s);
              break;
            case "6":
              viewLocations(s);
              break;
            case "7":
              viewAmenities(s);
              break;
            case "8":
              retry = false;
              break;
          }
        } catch(Exception ex) {
           System.out.println("Try Again.");
        }
      }

  }

  public static void addOrganization(Scanner input, Statement s) {
    boolean retry = true;
    while(retry) {
      try {
        System.out.println("Please enter the organization name: ");
        String group_name = DatabaseApplication.InputParser(input, "(\\S)+.*", "Please enter a valid organization name. Example: Walmart");
        System.out.println("Please enter the discount code: ");
        String discount_code = DatabaseApplication.InputParser(input, "(\\S)+", "Please enter a valid organization name. Example: GreatSavings230234");
        s.executeUpdate("insert into organization values(0, 0, '"+ group_name +"', '"+ discount_code +"')");
        retry = false;
      } catch (Exception ex) {
        retry = true;
        System.out.println("Failed to add organization, Try Again.");
      }
    }

  }

  public static void viewCustomers(Statement s) {
    try {
      ResultSet customerResult = s.executeQuery("select * from Customer");
      if(customerResult.next()) {
        System.out.printf("\n\nCustomer List\n");
        System.out.printf("%-5s%-30s%-30s%-10s\n", "ID", "Name", "Address", "Driver ID");
        do {
          System.out.printf("%-5d%-30s%-30s%-10d\n", 
                            customerResult.getInt("customer_id"), 
                            customerResult.getString("name"), 
                            customerResult.getString("address"), 
                            customerResult.getInt("driver_id"));
        } while(customerResult.next());
      } else {System.out.println("No current customers.");}
    } catch (Exception ex) {
      System.out.println("Failed to retrieve customer list");
    }
  }

  public static void viewRentals(Statement s) {
    try {
      ResultSet customerResult = s.executeQuery("select * from Rents where return_date is not null");
      if(customerResult.next()) {
        System.out.printf("\n\nRental List\n");
        System.out.printf("%-5s%-15s%-15s%-30s%-30s%-30s%-15s\n", "ID", "Customer ID", "Vehicle ID", "Pickup Date", "Return Date", "Return Location", "Return Tank");
        do {
          System.out.printf("%-5d%-15d%-15d%-30s%-30s%-30s%-15d\n", 
                            customerResult.getInt("rental_id"), 
                            customerResult.getInt("customer_id"),
                            customerResult.getInt("vehicle_id"),
                            customerResult.getString("pickup_date"), 
                            customerResult.getString("return_date"), 
                            customerResult.getString("return_location"), 
                            customerResult.getInt("return_tank"));
        } while(customerResult.next());
      } else {System.out.println("No current rentals.");}
    } catch (Exception ex) {
      System.out.println("Failed to retrieve rental list");
    }
  }

  public static void viewVehicles(Statement s) {
    try {
      ResultSet customerResult = s.executeQuery("select * from Vehicle");
      if(customerResult.next()) {
        System.out.printf("\n\nVehicle List\n");
        System.out.printf("%-5s%-20s%-20s%-20s%-15s%-30s\n", "ID", "Make", "Model", "Type", "Mileage", "Location");
        do {
          System.out.printf("%-5d%-20s%-20s%-20s%-15d%-30s\n", 
                            customerResult.getInt("vehicle_id"), 
                            customerResult.getString("make"), 
                            customerResult.getString("model"), 
                            customerResult.getString("type"),
                            customerResult.getInt("mileage"),
                            customerResult.getString("location"));
        } while(customerResult.next());
      } else {System.out.println("No current vehicles.");}
    } catch (Exception ex) {
      System.out.println("Failed to retrieve vehicle list");
    }
  }

  public static void viewOrganizations(Statement s) {
    try {
      ResultSet customerResult = s.executeQuery("select distinct group_name, discount_code from Organization");
      if(customerResult.next()) {
        System.out.printf("\n\nOrganization List\n");
        System.out.printf("%-40s%-30s\n","Group Name", "Discount Code");
        do {
          System.out.printf("%-40s%-30s\n", 
                            customerResult.getString("group_name"), 
                            customerResult.getString("discount_code"));
        } while(customerResult.next());
      } else {System.out.println("No current organizations.");}
    } catch (Exception ex) {
      System.out.println("Failed to retrieve organization list");
    }
  }

  public static void viewLocations(Statement s) {
    try {
      ResultSet customerResult = s.executeQuery("select distinct location from Vehicle");
      if(customerResult.next()) {
        System.out.printf("\n\nLocation List\n");
        do {
          System.out.printf("%-30s\n", customerResult.getString("location"));
        } while(customerResult.next());
      } else {System.out.println("No current locations.");}
    } catch (Exception ex) {
      System.out.println("Failed to retrieve organization list");
    }
  }
  public static void viewAmenities(Statement s) {
    try {
      ResultSet amenityResult = s.executeQuery("select distinct amenity_type from amenities");
      if(amenityResult.next()) {
        System.out.printf("\n\nAmenities Available to Customers\n");
        do {
          System.out.printf("%-30s\n", amenityResult.getString("amenity_type"));
        } while(amenityResult.next());
      } else {System.out.println("No current amenities.");}
    } catch (Exception ex) {
      System.out.println("Failed to retrieve amenity list");
    }
  }

}


class Customer {
  public static void PrintMenu() {
      System.out.printf("\nWelcome, Hurts Customer!\n");
      System.out.println("Please input the corresponding number for the desired action:");
      System.out.printf("[1]\tMake a reservation\n[2]\tCreate or update account\n[3]\tView Account\n[4]\tView current rental vehicles available\n[5]\tBack\n\n");
  }

  public static void MenuAction(Scanner input, Statement s) {
    boolean retry = true;
    while (retry){
      PrintMenu();
      String menuOption = DatabaseApplication.InputParser(input, "[1-5]", "Input a number corresponding with an action. Try again.");
      try {
        switch(menuOption) {
          case "1":
            addReservation(input, s);
            break;
          case "2":
            updateCustomer(input, s);
            break;
          case "3":
            viewAccount(input, s);
            break;
          case "4":
            try{
              while(retry) {
                Manager.viewLocations(s);
                System.out.println("Please enter the location you want to check for available vehicles:");
                String location = input.nextLine();
                ResultSet location_search = s.executeQuery("select distinct location from Vehicle where location = '" + location + "'");
                if(location_search.next()) {
                  System.out.println("Vehicles available from " + location + ":");
                  viewAvailableVehicles(s, location);
                  retry = false;
                } else {
                  System.out.println(location + "is not on the list. Try again.");
                  retry = true;
                }
              }
              retry = true;
            }catch(Exception ex) {
              ex.printStackTrace();
            }
            break;
          case "5":
            retry = false;
            break;
        }
      } catch(Exception ex) {
         System.out.println("Try Again.");
      }
    }
  }

  public static void addReservation(Scanner input, Statement s) {
    System.out.println("Please enter your first and last name: ");
    String name = DatabaseApplication.InputParser(input, "(\\S{1,}) (\\S{1,})", "Please enter a valid first and last name. Example: John Appleseed");
    boolean retry = true;
    do {
      try{
        ResultSet customer = s.executeQuery("select * from customer where name = '" + name + "'");
        if(customer.next()) { // This means the customer was found in database
          String customer_id = Integer.toString((customer.getInt("customer_id")));
          System.out.println("Welcome back, " + name + "!");
          if(customer.getInt("driver_id") == 0) { // If driver_id is not in db, we have customer enter
            System.out.println("You do not have a driver's license ID on our record, please enter it here (Example: 12345678): ");
            String driver_id = DatabaseApplication.InputParser(input, "^\\d{8}$", "Invalid ID, please enter your 8 digit driver's license.");
            s.executeUpdate("update customer set driver_id = " +driver_id+ " where customer_id = " + Integer.toString((customer.getInt("customer_id"))));
            System.out.println("Thank you for your driver's license ID.");
          }
          System.out.println("Which location would you like to reserve a rental?");
          while(retry) {
            Manager.viewLocations(s);
            System.out.println("Please enter the desired location:");
            String location = input.nextLine();
            ResultSet location_search = s.executeQuery("select distinct location from Vehicle where location = '" + location + "'");
            if(location_search.next()) {
              System.out.println("Vehicles available from " + location + ":");
              String vehicle_id = "";
              if(viewAvailableVehicles(s, location)) {
                while(retry) {
                  System.out.println("Enter the desired vehicle's ID to reserve");
                  vehicle_id = DatabaseApplication.InputParser(input, "[0-9]+", "Please enter a valid vehicle ID");  
                  ResultSet vehicle = s.executeQuery("select * from Vehicle where vehicle_id = " + vehicle_id+ " and location = '"+location+"'");
                  if(vehicle.next()) {
                    retry = false;
                  } else {
                    System.out.println("No vehicle was found under this vehicle ID. Try again.");
                  }
                }
                retry = true;
                String date = "";
                while(retry) {
                  System.out.println("Please enter the date you will pick up the vehicle (in yyyy-mm-dd format): ");
                  date = DatabaseApplication.InputParser(input, "^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$", "Please enter pickup date in yyyy-mm-dd format. Try again.");
                  if(new Date().before(new SimpleDateFormat("yyyy-MM-dd").parse(date))) {  // This checks if date is in the future
                    retry = false;
                  } else {
                    System.out.println("You can't reserve a vehicle in the past, unless you are Marty McFly or Doc Brown. You are neither.");
                  }
                }
                s.executeUpdate("insert into rents values (default, "+ customer_id +", "+ vehicle_id +", TIMESTAMP '" + date + " 08:00:00', null, null, null)");
                System.out.println("Successfully reserved this vehicle for " + date + " at Hurts' rental building in " + location + ".");
              } else {
                System.out.println("Pick another location, this one has no available vehicles currently.");
                retry = true;
              }
            } else {
              System.out.println("There was no location found named " + location + ", try again.");
              retry = true;
            }
          }

        } else { // No record of customer, creating new customer to continue
          System.out.println("There is no customer in our records named " + name + ", please select option 2 to create your account.");
          retry = false;
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        System.out.println("There was an error with making a reservation. Try again.");
        retry = true;
      }
    } while(retry);

    
  }

  public static void updateCustomer(Scanner input, Statement s) {
    System.out.println("Please enter your first and last name: ");
    String name = DatabaseApplication.InputParser(input, "(\\S{1,}) (\\S{1,})", "Please enter a valid first and last name. Example: John Appleseed");
    boolean retry = true;
    do {
      try{
        ResultSet customer = s.executeQuery("select * from customer where name = '" + name + "'");
        if(customer.next()) {
          String customer_id = Integer.toString((customer.getInt("customer_id")));
          System.out.println("Welcome back, " + name + "!");
          System.out.println("Current address: " + customer.getString("address"));
          System.out.println("Please enter an updated address, or press enter to skip update: ");
          String address = DatabaseApplication.InputParser(input, "(^(\\d{1,}) [a-zA-Z\\s]+)|^\\s*$", "Please enter a valid address. Example: 123 Woods Avenue");
          if(!address.matches("^\\s*$")) {
            s.executeUpdate("update customer set address = '" +address+ "' where customer_id = "+ customer_id);
            System.out.println("Address updated successfully to " + address);
          }
          String current_driver_id = Integer.toString(customer.getInt("driver_id"));
          if(current_driver_id.matches("^\\d{8}$")) {
            System.out.println("Current driver's license ID: " + current_driver_id);
          } else {
            System.out.println("Your driver's license ID is not specified");
          }
          System.out.println("Please enter your driver's license ID, or press enter to skip update: ");
          String driver_id = DatabaseApplication.InputParser(input, "(^\\d{8}$)|^\\s*$", "Please enter a valid 8 digit driver's license, or press enter to skip update. Example: 12345678");
          if(driver_id.matches("^\\d{8}$")) {
            s.executeUpdate("update customer set driver_id = " +driver_id+ " where customer_id = "+ customer_id);
            System.out.println("Driver's license ID updated successfully to " + driver_id);
          }
          retry = false;
        } else {
          System.out.println("Please enter your address: ");
          String address = DatabaseApplication.InputParser(input, "^(\\d{1,}) [a-zA-Z\\s]+", "Please enter a valid address. Example: 123 Woods Avenue");
          System.out.println("Please enter your driver's license (if not available at this time please enter 0)");
          String driver_id = DatabaseApplication.InputParser(input, "(^\\d{8}$)|0", "Please enter a valid 8 digit driver's license or 0 if unavailable. Example: 12345678");
          s.executeUpdate("insert into customer values (default, '"+ name +"', '"+ address +"', "+driver_id+")");
          System.out.println("Your customer account has been created, " + name + "!");
          retry = false;
        }
      } catch(Exception ex) {
        ex.printStackTrace();
      }

    } while(retry);
    
  }

  public static void viewAccount(Scanner input, Statement s) {
    System.out.println("Please enter your first and last name: ");
    String name = DatabaseApplication.InputParser(input, "(\\S{1,}) (\\S{1,})", "Please enter a valid first and last name. Example: John Appleseed");
    try{
      ResultSet customer = s.executeQuery("select * from customer where name = '" + name + "'");
      if(customer.next()) {
        String driver_id = Integer.toString((customer.getInt("driver_id")));
        String address = customer.getString("address");
        System.out.printf("\n\nAccount Information\n");
        System.out.println("Name: " + name);
        System.out.println("Address: " + address);
        if(driver_id.equals("0")) {
          System.out.println("Driver's license ID is not specified");
        } else {
          System.out.println("Driver's license ID: " + driver_id);
        }
      } else {
        System.out.println("We do not have an account under the name " + name + ", please select option 2 to create your account.");
      }
    } catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public static boolean viewAvailableVehicles(Statement s, String location) {
    try {
      ResultSet customerResult = s.executeQuery("select vehicle_id, make, model, type, mileage, location from Vehicle where location = '"+location+"'and vehicle_id not in (select vehicle_id from Rents)");
      if(customerResult.next()) {
        System.out.printf("%-5s%-20s%-20s%-20s%-15s\n", "ID", "Make", "Model", "Type", "Mileage");
        do {
          System.out.printf("%-5d%-20s%-20s%-20s%-15d\n", 
                            customerResult.getInt("vehicle_id"), 
                            customerResult.getString("make"), 
                            customerResult.getString("model"), 
                            customerResult.getString("type"),
                            customerResult.getInt("mileage"));
        } while(customerResult.next());
        return true;
      } else {
        System.out.println("No current vehicles at " + location);
      }
    } catch (Exception ex) {
      System.out.println("Failed to retrieve vehicle list");
    }
    return false;
  }
}


class Employee {
  public static void PrintMenu() {
      System.out.printf("\nWelcome, Hurts Employee!\n");
      System.out.println("Please input the corresponding number for the desired action:");
      System.out.printf("[1]\tPrepare a new rental\n[2]\tAccept a rental return\n[3]\tBack\n\n");
  }

  public static void MenuAction(Scanner input, Statement s, Connection conn) {
    boolean retry = true;
    while (retry){
      try {
        PrintMenu();
        String menuOption = DatabaseApplication.InputParser(input, "[1-3]", "Input a number corresponding with an action. Try again.");
        switch(menuOption) {
          case "1":
            addRental(input, s, conn);
            break;
          case "2":
            acceptRental(input, s);
            break;
          case "3":
            retry = false;
            break;
        }
      } catch(Exception ex) {
         System.out.println("Try Again.");
      }
    }
  }

  public static void addRental(Scanner input, Statement s, Connection conn) {
    // Enter customer name
    System.out.println("Please enter the customer's first and last name: ");
    String name = DatabaseApplication.InputParser(input, "(\\S{1,}) (\\S{1,})", "Please enter a valid first and last name. Example: John Appleseed");
    boolean retry = true;
    ArrayList<String> amenityQueryList = new ArrayList<String>();
    do {
      try {
        // Search and list customers of that name, else prompt customer to create account on their app
        ResultSet customer = s.executeQuery("select * from customer where name = '" + name + "'");
        if(customer.next()) {
          // Customer selected
          String customer_id = Integer.toString(customer.getInt("customer_id"));
          while(retry) {
            // Enter your current Hurts' location
            Manager.viewLocations(s);
            System.out.println("Please select and enter your Hurts' location from this list: ");
            String location = input.nextLine();
            ResultSet location_search = s.executeQuery("select distinct location from Vehicle where location = '" + location + "'");
            if(location_search.next()) {
              String vehicle_id = "";
              while(retry){
                Customer.viewAvailableVehicles(s, location);
                System.out.printf("Please enter the vehicle ID desired from this list of vehicles available at %s: \n", location);
                vehicle_id = DatabaseApplication.InputParser(input, "[0-9]+", "Please enter a valid vehicle ID");  
                ResultSet vehicle = s.executeQuery("select * from Vehicle where vehicle_id = " + vehicle_id + " and location = '"+location+"'");
                if(vehicle.next()) {
                  while(retry){
                    Manager.viewAmenities(s);
                    System.out.println("Please enter any amenity the customer desires from this list, or press enter to skip");
                    String amenity = input.nextLine();
                    if(!amenity.isEmpty()) {
                      ResultSet amenity_chosen = s.executeQuery("select * from amenities where amenity_type = '"+amenity+"'");
                      if(amenity_chosen.next()) {
                        amenityQueryList.add("insert into amenities values(?, '"+amenity+"', "+Integer.toString(amenity_chosen.getInt("amenity_cost"))+ ")");
                        System.out.println(amenity+" has been added to the customer's rental");
                      } else {
                        System.out.println(amenity+" is not an amenity provided. Try again.");
                        retry = true;
                      }
                    } else {
                      retry = false;
                    }
                  }
                  ResultSet rental = s.executeQuery("select rental_id from rents natural join vehicle where location = '"+location+ "' and customer_id = "+customer_id);
                  String rental_id = "";
                  if(rental.next()) {
                    rental_id = Integer.toString(rental.getInt("rental_id"));
                  }
                  // updateQuery(insert into rents values(default, customer_id, vehicle_id, current date, null, null, null))
                  // if amenities chosen updateQuery(insert into amenities values(rental_id, amenity_type, amenity_cost))
                  s.executeUpdate("insert into rents values(default, "+ customer_id+ ", "+vehicle_id+", CURRENT_TIMESTAMP, null, null, null)");
                  while(!amenityQueryList.isEmpty()) {
                    String q = amenityQueryList.remove(0);
                    PreparedStatement stmt = conn.prepareStatement(q);
                    stmt.setString(1, rental_id);
                    stmt.executeUpdate();
                  }
                } else {
                  System.out.println("No vehicle was found under this vehicle ID. Try again.");
                }
              }
            } else {
              System.out.println(location + " is not on this list. Try again.");
            }
          }

        } else {
          System.out.println("Customer not found. Have the customer sign up on their interface.");
          retry = false;
        }
      } catch(Exception ex) {
        ex.printStackTrace();
      }
    } while(retry);
    
    
    // Show available amenities (including insurance) for this vehicle
    // Choose amenities or skip

  }

  public static void acceptRental(Scanner input, Statement s) {
    // Enter customer name
    // Search and list customers of that name, else prompt customer to create account on their app
    // Select customer
    // Enter your current Hurts' location
    // List locations
    // Select location
    // Update location for vehicle_id
    // List rental(s) customer has out (PICKUP_DATE is null)
    // Select rental
    // Ask customer if they have any discount codes with an organization (list all orgs), apply discount in cost
    // Calculate cost using amenities, discount code, type of vehicle, length of rental, location of return vs pickup, time of year
    // updateQuery(insert into cost values(...))
  }

}