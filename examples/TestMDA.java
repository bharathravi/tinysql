import java.sql.*;

import java.util.*;

public class TestMDA
{
  public static void executeUpdate(Statement stmt, String sql) throws java.sql.SQLException
  {
    System.out.println("  " + sql);
    long startTime = System.currentTimeMillis();
    int res = stmt.executeUpdate(sql);
    System.out.println("    -> Elapsed time: "+ (System.currentTimeMillis() - startTime) + " msecs: " + res);
  }

  public static ResultSet executeQuery(Statement stmt, String sql) throws java.sql.SQLException
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
    } catch (ClassNotFoundException e) {
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
      p.setProperty ("separator", ";");
      p.setProperty ("encoding", "Cp850");
      
      DriverManager.setLogStream (System.out);      
      Connection con = DriverManager.getConnection("jdbc:tinySQL:./test", p);

      // get a Statement object from the Connection
      //
      Statement stmt = con.createStatement ();
			
      executeUpdate(stmt, "DROP TABLE IF EXISTS WowDepartments");

      System.out.println("");
      System.out.println("CREATE TABLE WowDepartments ...");
      System.out.println("=====================");
      executeUpdate(stmt, "CREATE TABLE WowDepartments (WowDeptNumber NUMERIC(3,0), WowDeptAbbr CHAR(10) NOT NULL, CpmDeptAbbr CHAR(3))");


      System.out.println("");
      System.out.println("INSERT INTO WowDepartments ...");
      System.out.println("====================");
      executeUpdate(stmt, "INSERT INTO WowDepartments SET WowDeptNumber=-12, WowDeptAbbr='URG CARE', CpmDeptAbbr='MED'");
      
      System.out.println("");
      System.out.println("SELECTING ALL FROM WowDepartments ...");
      System.out.println("====================");
      ResultSet res = stmt.executeQuery ("SELECT WowDeptNumber FROM WowDepartments");      
      ORG.as220.tinySQL.Utils.printResultSet (res, System.out);
      res.close ();
      
      System.out.println("");
      System.out.println("SELECTING WHERE WowDepartments ...");
      System.out.println("====================");
      res = stmt.executeQuery ("SELECT WowDeptNumber FROM WowDepartments WHERE WowDeptAbbr = 'URG CARE'");      
      ORG.as220.tinySQL.Utils.printResultSet (res, System.out);
      res.close ();

      stmt.close ();
      con.close ();
    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace ();
    }
  }

}
