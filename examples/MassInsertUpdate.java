import java.util.*;
import java.sql.*;

public class MassInsertUpdate {

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

            try 
            {
                stmt.executeUpdate("DROP TABLE giant");
            } 
            catch (Exception e) 
            {
                // do nothing
            }

            stmt.executeUpdate("CREATE TABLE giant (name CHAR(25), id NUMERIC(8,0), bulk CHAR(250), bulk2 CHAR(250))");
            
            long time = System.currentTimeMillis ();
            long lastTime = time;
            System.out.println ("Estaminated Time: BEGIN COUNT!");
            for (int i = 0; i < 2500; i++)
            {
            
              stmt.executeUpdate(
                  "INSERT INTO giant (name, id, bulk, bulk2) VALUES(" + String.valueOf(i) + "," + 
                  String.valueOf(i) + " , '" + String.valueOf(i*2) +"', '')"
              );
            }
 
            lastTime = time;
            time = System.currentTimeMillis ();
            System.out.println ("Estaminated Time: " + (time - lastTime));

            // Execute a query and get the result set.
            //
            ResultSet rs = stmt.executeQuery("SELECT * FROM giant");

            // process each row
            //
            int count = 0;
            while(rs.next()) {

                // retrieve each column by name

                // display each row
                //
                count ++;
            }
            System.out.println ("Made it through " + count + " rows");

            lastTime = time;
            time = System.currentTimeMillis ();
            System.out.println ("Estaminated Time: " + (time - lastTime));


            
            int cnt = stmt.executeUpdate ("UPDATE giant SET bulk='test', bulk2='test_too'");
            System.out.println(cnt + " row(s) affected.");

            lastTime = time;
            time = System.currentTimeMillis ();
            System.out.println ("Estaminated Time: " + (time - lastTime));

            stmt.close();
            con.close();

        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

}
  
