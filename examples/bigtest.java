import java.util.*;
import java.sql.*;

public class bigtest {

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
            Connection con = 
                DriverManager.getConnection("jdbc:tinySQL", "", "");
   
            // get a Statement object from the Connection
            //
            Statement stmt = con.createStatement();

            try {
                stmt.executeUpdate("DROP TABLE cars");
                stmt.executeUpdate("DROP TABLE people");
            } catch (Exception e) {
                // do nothing
            }

            stmt.executeUpdate("CREATE TABLE cars (name CHAR(25), id INT)");
            stmt.executeUpdate(
                "CREATE TABLE people (pe_name CHAR(25), pe_id INT, car_id INT)");
            System.err.println("Created the tables.");

            stmt.executeUpdate("INSERT INTO cars (name, id) VALUES('Fiat', 1)");
            stmt.executeUpdate("INSERT INTO cars (name, id) VALUES('Pinto', 2)");
            stmt.executeUpdate("INSERT INTO cars (name, id) VALUES('Thing', 3)");
            stmt.executeUpdate("INSERT INTO cars (name, id) VALUES('Bug', 4)");
            stmt.executeUpdate("INSERT INTO cars (name, id) VALUES('Newport', 5)");
            stmt.executeUpdate("INSERT INTO cars (name, id) VALUES('Rangerover', 6)");
            stmt.executeUpdate("INSERT INTO cars (name, id) VALUES('Jeep', 7)");
            stmt.executeUpdate("INSERT INTO cars (name, id) VALUES('Hummer', 8)");

            stmt.executeUpdate("INSERT INTO people (pe_name, pe_id, car_id) VALUES('Irwin Garden', 1, 2)");
            stmt.executeUpdate("INSERT INTO people (pe_name, pe_id, car_id) VALUES('Mr. Fiction', 2, 7)");
            stmt.executeUpdate("INSERT INTO people (pe_name, pe_id, car_id) VALUES('Sal Paradise', 3, 8)");
            stmt.executeUpdate("INSERT INTO people (pe_name, pe_id, car_id) VALUES('Dean Moriarty', 4, 3)");
            stmt.executeUpdate("INSERT INTO people (pe_name, pe_id, car_id) VALUES('Bull Lee', 5, 7)");
            stmt.executeUpdate("INSERT INTO people (pe_name, pe_id, car_id) VALUES('Jack Chip', 6, 1)");
            stmt.executeUpdate("INSERT INTO people (pe_name, pe_id, car_id) VALUES('Glen Runciter', 7, 4)");
            stmt.executeUpdate("INSERT INTO people (pe_name, pe_id, car_id) VALUES('Horselover Fat', 8, 2)");
            stmt.executeUpdate("INSERT INTO people (pe_name, pe_id, car_id) VALUES('Gnossos Pappadopoulos', 9, 7)");
            stmt.executeUpdate("INSERT INTO people (pe_name, pe_id, car_id) VALUES('Hef', 10, 6)");
            stmt.executeUpdate("INSERT INTO people (pe_name, pe_id, car_id) VALUES('Matty Groves', 11, 7)");
            stmt.executeUpdate("INSERT INTO people (pe_name, pe_id, car_id) VALUES('Raoul Frodus', 12, 5)");

            // Execute a query and get the result set.
            //
            ResultSet rs = stmt.executeQuery("SELECT pe_name, name FROM cars, people WHERE car_id = id");

            // Display column headers
            //
            System.out.println("Name                      Id ");
            System.out.println("========================= ===");

            // process each row
            //
            while(rs.next()) {

                // retrieve each column by name
                //
                String pe_name = rs.getString("pe_name");
                String name    = rs.getString("name");

                // display each row
                //
                System.out.println(pe_name + " " + name);

            }

            int cnt = stmt.getUpdateCount();
            System.out.println(cnt + " row(s) affected.");

            stmt.close();
            con.close();

        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

}
  
