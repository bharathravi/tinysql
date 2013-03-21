package ORG.as220.tinySQL;

import ORG.as220.tinySQL.util.Log;

import java.io.*;
import java.sql.Types;
import java.util.Properties;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: bharath
 * Date: 2/24/13
 * Time: 9:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class memoryTextFileDatabase extends textFileDatabase {
  public memoryTextFileDatabase(String dataDir, Properties properties) throws tinySQLException {
    super(dataDir, properties);
  }


  @Override
  public tinySQLTable openTable(String table_name) throws tinySQLException {
    Log.debug("InMemory TextFile-Engine: Opening table " + table_name);
    return (tinySQLTable) new InMemoryTextFileTable(dataDir, table_name, this);
  }

  @Override
  protected void mkDataDirectory() throws NullPointerException {
    // Nothing to do: No directories in memory.
  }

  protected void db_createTable(String table_name, Vector v)
      throws IOException, tinySQLException
  {
    // create the table definition file
    //
    MemoryFile memFile = new MemoryFile(dataDir + File.separator
        + table_name + getDefinitionExtension(), "rw");

    if (deleteMode == DELETE_DEFAULT)
    {
      // write out the column definition for the _DELETED column
      //
      memFile.write("CHAR|_DELETED|1\n".getBytes());
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
      memFile.write((stype + "|").getBytes());
      memFile.write((col.getPhysicalName() + "|").getBytes());
      memFile.write((col.getSize() + "\n").getBytes());
    }

    // Close the file
    memFile.close();

    // create the table definition file
    //
    memFile = new MemoryFile(dataDir + File.separator
        + table_name + getTableExtension(), "rw");
    memFile.write(getTablePrefix());
    memFile.write(getTablePostfix());
    memFile.close();

  }

  @Override
  protected void db_createIndex(String table_name, String primaryKey) throws IOException, tinySQLException {
    // create the table index file
    //
    //
    MemoryFile memFile = new MemoryFile(dataDir + File.separator
        + table_name + getIndexExtension(), "rw");

    memFile.write(("PRIMARY_KEY|" + primaryKey + "\n").getBytes());
    memFile.write("NUM_ROWS|0\n".getBytes());

    // Close the file
    memFile.close();
  }

  @Override
  protected void db_removeTable(String table_name) throws IOException {
    MemoryFile.delFile(dataDir + File.separator + table_name + getTableExtension());
    MemoryFile.delFile(dataDir + File.separator + table_name + getDefinitionExtension());
    MemoryFile.delFile(dataDir + File.separator + table_name + getIndexExtension());
  }

  @Override
  protected void db_renameTable(String table_name, String newname) throws IOException {
    String source = dataDir + File.separator + table_name;
    String dest = dataDir + File.separator + newname;
    if (MemoryFile.renameFile(source + getTableExtension(), dest + getTableExtension()) == false ||
        MemoryFile.renameFile(source + getDefinitionExtension(), dest + getDefinitionExtension()) == false)
      throw new IOException("Renaming of table " + table_name + " to " + newname + " failed");
  }

}
