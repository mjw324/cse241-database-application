package com.generator.sql;

import java.util.ArrayList;
import java.util.Scanner;

import com.github.javafaker.Faker;

import java.io.*;
import java.sql.*;

public class App {
  public static void main (String[] arg) 
  throws SQLException, IOException, java.lang.ClassNotFoundException {
    boolean retry = true; // Set to false when user inputs correct credentials to - else retry
    //String user_name = "";
    //char [] pwd = {};
    Connection con = null;
    Statement s = null;
    Scanner in = new Scanner(System.in);
    Faker faker = new Faker();
    do { // do while try catch block for login attempt(s) to Edgar1
      try {
        // System.out.println("Please input your Oracle username on Edgar1:");
        // user_name = in.nextLine();
        // System.out.println("Please input your Oracle password on Edgar1:");
        // designed for inputting password without displaying the password:
        // Console console = System.console();
        // pwd = console.readPassword();
        //con = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241",user_name, new String(pwd));
        con = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241","mjw324", "KSI2800!!!");
        s=con.createStatement();
        retry = false;
      } catch(Exception ex) {
        System.out.println("Invalid username/password. Logon denied. " + ex.getMessage());
      }
    } while (retry);
    // Reset retry boolean for next step: user input for searching database
    retry = true;
    try {
        // String q = "DELETE FROM CUSTOMER WHERE CUSTOMER_ID > 0";
        // int result = s.executeUpdate(q);
        // result = s.executeUpdate(q);
      VehiclePopulate(faker, s, in);
    } catch(Exception ex) {
        System.out.println("Try Again. " + ex.getMessage());
        ex.printStackTrace();
    }
    in.close();
  }
  public static void VehiclePopulate(Faker faker, Statement s, Scanner in) throws Exception{
    System.out.println("Please input the number of vehicles to generate for each location:");
    String no_vehicle = in.nextLine();
    ResultSet vehicleResult = s.executeQuery("select distinct location from Vehicle");
    ArrayList<String> locations = new ArrayList<String>();
    if(vehicleResult.next()) {
      do {
        locations.add(vehicleResult.getString("location"));
      } while(vehicleResult.next());
    } else {System.out.println("No current locations.");}
    int size = locations.size();
      for(int j = 0; j < size; j++) {
        String location = locations.remove(0);
        for(int i = 0; i < Integer.parseInt(no_vehicle); i++) {
          String q;
          int result = 0;
          ArrayList<String> types = new ArrayList<String>();
          types.add("Mini Van");
          types.add("Convertible");
          types.add("Hatchback");
          types.add("Station Wagon");
          types.add("SUV");
          types.add("Sedan");
          String make = faker.ancient().god();
          String model = faker.pokemon().name();
          String type = types.get(faker.number().numberBetween(0, types.size()));
          String mileage = Integer.toString(faker.number().numberBetween(23000, 250000));
          q = "INSERT INTO VEHICLE VALUES(default, '"+ make +"', '"+ model +"', '" +type+ "', "+ mileage +", '"+location+"')";
          result = s.executeUpdate(q);
          System.out.println(result);
        }
    }
  }

  public static void CustomerPopulate(Faker faker, Statement s, Scanner in) throws Exception{
    System.out.println("Please input the number of customers to generate:");
    String no_customer = in.nextLine();
    for(int i = 0; i < Integer.parseInt(no_customer); i++) {
      String q;
      int result = 0;
      String name = faker.name().firstName() + " " + faker.name().lastName();
      String address = faker.address().streetAddress();
      String driver_id = faker.number().digits(8);
      q = "INSERT INTO CUSTOMER VALUES(default, '"+ name +"', '"+ address +"', "+ driver_id +")";
      result = s.executeUpdate(q);
      System.out.println(result);
    }
  }
}
