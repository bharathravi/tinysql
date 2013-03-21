/*
 *
 * diskTextFileDatabase - an extension of textFileDatabase for on-disk text file access
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

package ORG.as220.tinySQL;

import ORG.as220.tinySQL.sqlparser.ColumnDefinition;
import ORG.as220.tinySQL.sqlparser.CreateTableStatement;
import ORG.as220.tinySQL.textFileDatabase;
import ORG.as220.tinySQL.util.Log;

import java.io.*;
import java.lang.String;
import java.sql.Types;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class diskTextFileDatabase extends textFileDatabase
{
  public diskTextFileDatabase(String dataDir, Properties properties) throws tinySQLException {
    super(dataDir, properties);
  }

  @Override
  public tinySQLTable openTable(String table_name) throws tinySQLException {
    Log.debug("Disk TextFile-Engine: Opening table " + table_name);
    return (tinySQLTable) new textFileTable(dataDir, table_name, this);
  }

  /*
 *
 * Make the data directory unless it already exists
 *
 */
  protected void mkDataDirectory() throws NullPointerException
  {

    File dd = new File(dataDir);

    if (!dd.exists())
    {
      dd.mkdir();
    }

  }

  protected void db_createTable(String table_name, Vector v)
      throws IOException, tinySQLException
  {
    // create the table definition file
    //
    BufferedOutputStream fdef =
        new BufferedOutputStream(
            new FileOutputStream(dataDir + File.separator + table_name + getDefinitionExtension()
            ));

    // open it as a DataOutputStream
    //
    DataOutputStream def = new DataOutputStream(fdef);

    if (deleteMode == DELETE_DEFAULT)
    {
      // write out the column definition for the _DELETED column
      //
      def.writeBytes("CHAR|_DELETED|1\n");
    }

    // write out the rest of the columns' definition. The
    // definition consists of datatype, column name, and
    // size delimited by a pipe symbol
    //

    for (int i = 0; i < v.size(); i++)
    {
      tsColumn col = (tsColumn) v.elementAt(i);

      int type = col.getType();
      String stype;
      switch (type)
      {
        case Types.INTEGER:
          stype = "NUMERIC";
          col.setSize(10, 0);
          break;
        case Types.NUMERIC:
          stype = "NUMERIC";
          break;
        case Types.CHAR:
          stype = "CHAR";
          break;
        case Types.DATE:
          stype = "DATE";
          col.setSize(10, 0);
          break;
        default:
          throw new tinySQLException("Unsupported text file type: " + type);
      }
      def.writeBytes(stype + "|");
      def.writeBytes(col.getPhysicalName() + "|");
      def.writeBytes(col.getSize() + "\n");
    }

    // flush the DataOutputStream and jiggle the handle
    //
    def.flush();
    // close the file
    //
    fdef.close();

    // create the table definition file
    //
    fdef = new BufferedOutputStream(new FileOutputStream(dataDir + File.separator + table_name + getTableExtension()));
    fdef.write(getTablePrefix());
    fdef.write(getTablePostfix());
    fdef.close();

  }

  @Override
  protected void db_createIndex(String table_name, String primaryKey) throws IOException, tinySQLException {
    // Create Index file
    BufferedOutputStream fdef =
        new BufferedOutputStream(
            new FileOutputStream(dataDir + File.separator + table_name + getIndexExtension()
            ));

    // open it as a DataOutputStream
    //
    DataOutputStream def = new DataOutputStream(fdef);

    System.out.println(primaryKey + " is PRIMKEY");
    def.write(("PRIMARY_KEY|" + primaryKey + "\n").getBytes());
    def.write("NUM_ROWS|0\n".getBytes());

    // Close the file
    def.flush();
    fdef.close();
  }



  protected void db_removeTable(String table_name) throws IOException
  {
    Utils.delFile(dataDir + File.separator + table_name + getTableExtension());
    Utils.delFile(dataDir + File.separator + table_name + getDefinitionExtension());
    Utils.delFile(dataDir + File.separator + table_name + getIndexExtension());
  }

  protected void db_renameTable(String table_name, String newname) throws IOException {
    String source = dataDir + File.separator + table_name;
    String dest = dataDir + File.separator + newname;
    if (Utils.renameFile(source + getTableExtension(), dest + getTableExtension()) == false ||
        Utils.renameFile(source + getDefinitionExtension(), dest + getDefinitionExtension()) == false)
      throw new IOException("Renaming of table " + table_name + " to " + newname + " failed");
  }

}

