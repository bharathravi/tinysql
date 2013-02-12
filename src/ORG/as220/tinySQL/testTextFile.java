/*
 *
 * Test the textFileDriver
 *
 * Copyright 1996, Brian C. Jepson
 *                 (bjepson@ids.net)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class testTextFile
{


  public static void main(String argv[])
  {


    // Uncomment the next line to get noisy messages
    // during the test
    //
    // java.sql.DriverManager.setLogStream(System.out);


    try
    {

      // load the driver
      //
      Class.forName("textFileDriver");

      // the url to the tinySQL data source
      //
      String url = "jdbc:tinySQL";

      // get the connection
      //
      Connection con = DriverManager.getConnection(url, "", "");

      // create a statement, create a table, populate
      // it, and query it
      //
      Statement stmt = con.createStatement();
      stmt.executeUpdate("CREATE TABLE test (name CHAR(10))");
      stmt.executeUpdate("INSERT INTO test (name) VALUES('test')");

      ResultSet rs = stmt.executeQuery("SELECT name FROM test");

      // fetch the row
      //
      rs.next();

      // get the column, and see if it matches our expectations
      //
      String colval = rs.getString(1);
      if (colval.startsWith("test"))
      {
        System.out.println("textFile JDBC driver installed correctly.");
      }
      else
      {
        System.out.println("Test was not successful :-(");
        System.out.println("Got \"" + colval + "\", expected \"test\"");
      }

      stmt.close();
      con.close();
    }
    catch (Exception e)
    {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }

  }

}

