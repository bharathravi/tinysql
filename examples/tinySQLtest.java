import java.util.*;
import java.sql.*;

public class tinySQLtest {

    public static void main(String argv[]) {

        try {
            // Register the textFileDriver.
            //
            Class.forName("ORG.as220.tinySQL.textFileDriver").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
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
                DriverManager.getConnection("jdbc:tinySQL", "", "");
   
            // get a Statement object from the Connection
            //
            Statement stmt = con.createStatement();

            // Try to drop the table if it exists.
            // Ignore any exception for DROP TABLE;
            // it will most assuredly throw one if
            // the table does not exist
            //
            try {
                stmt.execute("DROP TABLE test");
                System.err.println("Dropped the test table.");
            } catch (Exception e) {
                // do nothing
            }

            // Create and drop a table
            stmt.execute("CREATE TABLE test (name ChArAcTeR(25), id iNtEgEr)");
            stmt.execute("DROP TABLE test");
            
            // Create a table (for real this time)
            //stmt.execute("CREATE TABLE test (name cHaR(25), id InT)");
	    stmt.execute("CREATE TABLE test (name cHaR(25), id NUMERIC(4,0) PRIMARY KEY)");
            System.err.println("Created the test table.");

            // Insert a couple of rows.
            stmt.executeUpdate(
                "INSERT INTO test (name, id) VALUES('Arian', 1)");
            stmt.executeUpdate(
                "INSERT INTO test (name, id) VALUES('Bletus',  2)");
	    stmt.executeUpdate("INSERT INTO test (name, id) VALUES('Claus',  3)");
	    

            // Execute a query and get the result set.
            //
	    ResultSet rs = stmt.executeQuery("SELECT * FROM test where id =2");
	    //ResultSet rs = stmt.executeQuery("SELECT * FROM test where id <2");
            //ResultSet rs = stmt.executeQuery("SELECT * FROM test where id >=2");	    
	    //ResultSet rs = stmt.executeQuery("SELECT * FROM test where id >=1 and id <=2");

            // Display column headers
            //
            System.out.println("Name                      Id ");
            System.out.println("========================= ===");

            // process each row
            //
            while(rs.next()) {

                // retrieve each column by name
                //
                String name = rs.getString("name");
                int    id   = rs.getInt("id");

                // display each row
                //
                System.out.println(name + " " + id);

            }

            int cnt = stmt.getUpdateCount();
            System.out.println(cnt + " row(s) affected.");

            stmt.close();
            con.close();

        } catch( Exception e ) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
  
