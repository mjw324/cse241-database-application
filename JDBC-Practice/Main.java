import java.util.Scanner;
import java.io.*;
import java.sql.*;

class TeachingRecord {
  public static void main (String[] arg) 
  throws SQLException, IOException, java.lang.ClassNotFoundException {
    boolean retry = true; // Set to false when user inputs correct credentials to - else retry
    String user_name = "";
    char [] pwd = {};
    Connection con = null;
    Statement s = null;
    Scanner in = new Scanner(System.in);
    do { // do while try catch block for login attempt(s) to Edgar1
      try {
        System.out.println("Please input your Oracle username on Edgar1:");
        user_name = in.nextLine();
        System.out.println("Please input your Oracle password on Edgar1:");
        // designed for inputting password without displaying the password:
        Console console = System.console();
        pwd = console.readPassword();
        con = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241",user_name, new String(pwd));
        s=con.createStatement();
        retry = false;
      } catch(Exception ex) {
        System.out.println("Invalid username/password. Try again.");
      }
    } while (retry);
    // Reset retry boolean for next step: user input for searching database
    retry = true;
    do{
      try {
        String q;
        ResultSet result;
        String search;
        int ID;
        System.out.println("Please input the desired instructor:");
        search = in.nextLine();
        q = String.format("select id, name from instructor where name like \'%%%s%%\'", search); // uses like construct to find keyword
        result = s.executeQuery(q);
        if (!result.next()) {System.out.print("Empty Result. "); throw new Exception();}
        do {
          String id = String.format("%05d", Integer.parseInt(result.getString("id"))); // Lpad id
          System.out.println (id + " " + result.getString("name"));
        } while (result.next());

        System.out.println("Please input the desired instructor ID:");
        ID = Integer.parseInt(in.nextLine());
        if(ID >= 100000) {System.out.print("ID out of range. "); throw new Exception();}
        q = String.format("select count(*) as id_count from instructor where id = %d", ID);
        System.out.printf("Teaching record for instructor %d\n", ID);
        result = s.executeQuery(q);
        if (!result.next()) {System.out.print("Empty Result. "); throw new Exception();}
        if((result.getString("id_count")).equals("1")) { // Found 1 instructor with specified id
          q = "select course.dept_name,teaches.course_id,course.title,enrollment.sec_id,teaches.semester,teaches.year,enrollment.total " +
            "from(select distinct semester,year,course_id,sec_id, count(*) as total " +
            "from takes " +
            "group by course_id,sec_id,semester,year) enrollment,teaches,course " +
            "where enrollment.course_id=teaches.course_id and enrollment.sec_id=teaches.sec_id and course.course_id=teaches.course_id and teaches.id=%d and enrollment.semester = teaches.semester and enrollment.year = teaches.year " +
            "order by course.dept_name ASC, teaches.course_id ASC, teaches.year ASC, teaches.semester DESC";
          String newq = String.format(q, ID);
          result = s.executeQuery(newq);
          if (!result.next()) {System.out.print("This Professor has no teaching record. "); throw new Exception();}
          System.out.printf("%-15s%-5s%-35s%-5s%-10s%-5s%-15s\n", "Department", "CNO", "Title", "Sec", "Sem", "Year", "Enrollment");
          do {
            String dept_name = String.format("%-15s", result.getString("dept_name"));
            String course_id = String.format("%-5s", result.getString("course_id"));
            String title = String.format("%-35s", result.getString("title"));
            String sec_id = String.format("%-5s", result.getString("sec_id"));
            String semester = String.format("%-10s", result.getString("semester"));
            String year = String.format("%-5s", result.getString("year"));
            String enrollment = String.format("%-15s", result.getString("total"));
            System.out.println(dept_name + course_id + title + sec_id + semester + year + enrollment);
          } while (result.next());
        }
        s.close();
        con.close();
        retry = false;
      } catch(Exception ex) {
         System.out.println("Try Again.");
      }
    } while(retry);
    in.close();
  }
}
