/* INVOKE:  java QueryDbf "SELECT * FROM people"
 *
 * Test the dbfFileDriver
 *
 */
import java.net.URL;
import java.sql.*;
import java.util.Properties;

import ORG.as220.tinySQL.Utils;

public class QueryDbf {

  public static void main(String argv[]) {


    // Uncomment the next line to get noisy messages
    // during the test
    //
    java.sql.DriverManager.setLogStream(System.out);

    try {


      // load the driver
      //
      Class.forName("ORG.as220.tinySQL.dbfFileDriver").newInstance();

      // the url to the tinySQL data source
      //
      String url = "jdbc:dbfFile:.";

      // get the connection
      //
      Properties p = new Properties ();
      p.setProperty ("user" , "me");
      p.setProperty ("encoding", "Cp850");
      p.setProperty ("autoenc", "false");

      DriverManager.setLogStream (System.out);
      Connection con = DriverManager.getConnection(url, p);

      // create a statement and execute a query
      //
      Statement stmt = con.createStatement();

      String str = null;
      if (argv.length == 1)
        str = argv[0];
      else
        str = "SELECT * FROM MALI"; // WHERE pe_name = 'Dean Moriarty'";

      System.out.println("\nSQL: " + str + "\n");
      long startTime = System.currentTimeMillis();
      ResultSet rs = stmt.executeQuery(str);
      System.out.println ("ColCount: " + rs.getMetaData().getColumnCount ());

      int numCols = QueryDbf.displayResults(rs);
      long endTime = System.currentTimeMillis();

      long diffTime = endTime - startTime;
      if (numCols > 0)
        System.out.println("\n    -> Elapsed time: " + diffTime + " msecs [" + diffTime/numCols + " msecs/tuple]");
      else
        System.out.println("\n    -> Elapsed time: " + diffTime + " msecs, no results found");

      stmt.close();
      con.close();

    } catch( Exception e ) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
    System.exit(0);
  }

  /**
  Formatted output to stdout
  @return number of tuples
  */
  static int displayResults(ResultSet rs) throws java.sql.SQLException
  {
    if (rs == null) 
    {
      System.err.println("ERROR in displayResult(): No data in ResulSet");
      return 0;
    }

    return Utils.printResultSet (rs, System.out);
  }

  /**
  Cut or padd the string to the given size
  @param a string
  @param size the wanted length
  @param padChar char to use for padding (must be of length()==1!)
  @return the string with correct lenght, padded with pad if necessary
  */
  public static String forceToSizeLeft(String str, int size, char padChar)
  {
    if (str != null && str.length() == size)
      return str;

    StringBuffer tmp;
    if (str == null)
    {
      tmp = new StringBuffer(size);
    }
    else
    {
      tmp = new StringBuffer(str);
    }

    if (tmp.length() > size) 
    {
      tmp.setLength (size);
      return tmp.toString ();  // do cutting
    }
    else 
    {
      int arsize = size - tmp.length();
      char[] ar = new char[arsize];
      for (int i = 0; i < arsize; i++)
      {
        ar[i] = padChar;
      }
      tmp.append (ar);
      return tmp.toString ();
    }
  }


}

