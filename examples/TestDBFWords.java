import java.io.*;
import java.util.*;
import java.sql.*;

public class TestDBFWords {

    static void executeUpdate(Statement stmt, String sql) throws java.sql.SQLException
    {
      System.out.println("  " + sql);
      long startTime = System.currentTimeMillis();
      stmt.executeUpdate(sql);
      System.out.println("    -> Elapsed time: "+ (System.currentTimeMillis() - startTime) + " msecs");
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
            Class.forName("ORG.as220.tinySQL.dbfFileDriver");
        } catch (ClassNotFoundException e) {
            System.err.println(
                "I could not find the tinySQL classes. Did you install\n" +
                "them as directed in the README file?");
            e.printStackTrace();
        }

        try { // watch out for SQLExceptions!

            // Make a connection to the tinySQL Driver.
            //
            Connection con = 
                DriverManager.getConnection("jdbc:dbfFile:.", "", "");

            // get a Statement object from the Connection
            //
            Statement stmt = con.createStatement();

            try {
                executeUpdate(stmt, "DROP TABLE words");
            } catch (Exception e) {
                // do nothing
            }

            System.out.println("");
            System.out.println("CREATE TABLE words ...");
            System.out.println("=====================");
            executeUpdate(stmt, "CREATE TABLE words (word CHAR(128))");
            System.err.println("Created the table.");

            System.err.println("Inserting rows from /usr/dict/words - this will take a long time!");
            BufferedReader r = new BufferedReader(new FileReader("/usr/dict/words"));
            String s;
            while( (s = r.readLine()) != null ) {
                if(s.indexOf('\'') < 0) { /* Someday, I should escape quotes */
                    String sql = "INSERT INTO words (word) VALUES('" + s + "')";
                    stmt.executeUpdate(sql);
                }
            }
            System.err.println("Done inserting rows.");

            // Execute a query and get the result set.
            //
            System.out.println("");
            System.out.println("SELECT words ...");
            System.out.println("=======================");
            ResultSet rs = executeQuery(stmt, "SELECT * FROM words");
            int numrows = 0;
            while (rs.next()) {
                //System.out.println(rs.getString(1));
                numrows++;
            }
            System.out.println(numrows + " rows in words table.");
            System.out.println("Please compare to:");
            System.out.println("grep -v \\' /usr/dict/words  | wc -l");

            stmt.close();
            con.close();

            System.out.println("\nGood bye");

        } catch( Exception e ) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}

