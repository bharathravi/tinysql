import java.sql.*;

import java.util.*;

public class Test
{
  public static void main(String argv[]) 
  {

    try 
    {
        // Register the textFileDriver.
        //
        Class.forName("ORG.as220.tinySQL.dbfFileDriver");
    } catch (ClassNotFoundException e) 
    {
        System.err.println(
            "I could not find the tinySQL classes. Did you install\n" +
            "them as directed in the README file?");
        e.printStackTrace();
    }

//    try 
//    { // watch out for SQLExceptions!
//
//      // Make a connection to the tinySQL Driver.
//      //
//      Properties p = new Properties ();
//      p.setProperty ("user" , "me");
//      p.setProperty ("encoding" , "Cp437");
//
//      DriverManager.setLogStream (System.out);      
//      Connection con = DriverManager.getConnection("jdbc:dbfFile:./test", null);
//
//      // get a Statement object from the Connection
//      //
//      Statement cstmt = con.createStatement ();
//			
//
//      ResultSet res = cstmt.executeQuery ("SELECT * FROM \"table\"");      
//      ORG.as220.tinySQL.Utils.printResultSet (res, System.out);
//
//      cstmt.close ();
//      con.close ();
//    }
//    catch (SQLException sqle)
//    {
//      sqle.printStackTrace ();
//    }
    System.out.println ((byte) '0');
    System.out.println ((byte) '1');
    System.out.println ((byte) '2');
    System.out.println ((byte) '3');
    System.out.println ((byte) '4');
    System.out.println ((byte) '5');
    System.out.println ((byte) '6');
    System.out.println ((byte) '7');
    System.out.println ((byte) '8');
    System.out.println ((byte) '9');
  }

}
