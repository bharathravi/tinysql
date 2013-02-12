import java.sql.*;

import java.util.*;

public class TestLuke
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

  public static void main(String argv[]) 
  {

    try 
    {
        // Register the textFileDriver.
        //
        Class.forName("ORG.as220.tinySQL.textFileDriver");
    } catch (ClassNotFoundException e) 
    {
        System.err.println(
            "I could not find the tinySQL classes. Did you install\n" +
            "them as directed in the README file?");
        e.printStackTrace();
    }

    try 
    { // watch out for SQLExceptions!

      // Make a connection to the tinySQL Driver.
      //
      Properties p = new Properties ();
      p.setProperty ("user" , "me");
      p.setProperty ("encoding" , "Cp437");

      DriverManager.setLogStream (System.out);      
      Connection con = DriverManager.getConnection("jdbc:tinySQL:./test", null);

      // get a Statement object from the Connection
      //
      Statement stmt = con.createStatement ();
			
      executeUpdate(stmt, "DROP TABLE IF EXISTS party");

      System.out.println("");
      System.out.println("CREATE TABLE party ...");
      System.out.println("=====================");
      executeUpdate(stmt, "CREATE TABLE party (birthday DATE, age NUMERIC(10,0))");

      System.out.println("");
      System.out.println("INSERT INTO party ...");
      System.out.println("====================");
      executeUpdate(stmt, "INSERT INTO party (birthday, age) VALUES('2000-12-01', 1)");

      ResultSet res = stmt.executeQuery ("SELECT * FROM party");      
      ORG.as220.tinySQL.Utils.printResultSet (res, System.out);

      stmt.close ();
      con.close ();
    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace ();
    }
  }

}
