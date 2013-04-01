import java.util.*;
import java.sql.*;

public class TestTextPrimKey
{

  static void executeUpdate(Statement stmt, String sql) throws java.sql.SQLException
  {
    System.out.println("  " + sql);
    long startTime = System.currentTimeMillis();
    int res = stmt.executeUpdate(sql);
    System.out.println("    -> Elapsed time: "+ (System.currentTimeMillis() - startTime) + " msecs: " + res);
  }

  static ResultSet executeQuery(Statement stmt, String sql) throws java.sql.SQLException
  {
    System.out.println("  " + sql);
    long startTime = System.currentTimeMillis();
    ResultSet rs = stmt.executeQuery(sql);
    System.out.println("    -> Elapsed time: "+ (System.currentTimeMillis() - startTime) + " msecs");
    return rs;
  }

  public static void main(String argv[]) {

    try {
      // Register the textFileDriver.
      //
      Class.forName("ORG.as220.tinySQL.textFileDriver");
    } catch (ClassNotFoundException e) {
      System.err.println(
          "I could not find the tinySQL classes. Did you install\n" +
              "them as directed in the README file?");
      e.printStackTrace();
    }

    try { // watch out for SQLExceptions!

      // Make a connection to the tinySQL Driver.
      //
      Properties p = new Properties ();
      p.setProperty ("user" , "me");
      p.setProperty ("encoding", "Cp850");

      DriverManager.setLogStream (System.out);
      Connection con = DriverManager.getConnection("jdbc:tinySQL:./test", p);
      System.out.println (p);

      // get a Statement object from the Connection
      //
      Statement stmt = con.createStatement();

      executeUpdate(stmt, "DROP TABLE IF EXISTS cars");
      System.out.println("");
      System.out.println("CREATE TABLE cars ...");
      System.out.println("=====================");
      executeUpdate(stmt, "CREATE TABLE cars (name CHAR(25), id NUMERIC(4,0), PRIMARY KEY (id))");

      System.out.println("");
      System.out.println("INSERT INTO cars ...");
      System.out.println("====================");
      executeUpdate(stmt, "INSERT INTO cars (name, id) VALUES('Fiat', 1)");
      executeUpdate(stmt, "insert into cars (name, id) values('Pinto', 2)");
      executeUpdate(stmt, "INSerT inTO cars (name, id) VALueS('Thing', 3)");
      executeUpdate(stmt, "INSERT INTO cars (name, id) VALUES('Bug', 4)");
      executeUpdate(stmt, "INSERT INTO cars (name, id) VALUES('Newport', 5)");
      executeUpdate(stmt, "INSERT INTO cars (name, id) VALUES('Rangerover', 6)");
      executeUpdate(stmt, "INSERT INTO cars (name, id) VALUES('Jeep', 7)");
      executeUpdate(stmt, "INSERT INTO cars (name, id) VALUES('Hummer', 8)");
      executeUpdate(stmt, "INSERT INTO cars (name, id) VALUES('Lexus', 9)");

      for (int i = 10; i < 80000; ++i) {
        executeUpdate(stmt, "INSERT INTO cars (name, id) VALUES('Lexus"+ i + "', " + i + ")");
      }

      for (int i = 10; i < 100; ++i) {
        ResultSet rs = executeQuery(stmt, "SELECT * FROM cars WHERE id = " + i);
        System.out.println ("No Of Recs: " + QueryDbf.displayResults(rs));
      }

      // Execute a query and get the result set.

      stmt.close();
      con.close();

      System.out.println("\nGood bye");

    } catch( Exception e ) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

}

