/*
 *
 * textFile - an extension of tinySQL for text file access
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
import ORG.as220.tinySQL.util.Log;

import java.io.*;
import java.sql.Types;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public abstract class textFileDatabase extends tinySQL
{

  public static final int DELETE_DEFAULT = -1;
  public static final int DELETE_NONE = 1;
  public static final int DELETE_PACK = 2;

  public static final int INSERT_DEFAULT = 0;
  public static final int INSERT_SIZE = 1;
  public static final int INSERT_SPEED = 2;

  // the data directory where textFileDatabase stores its files
  //
  protected String dataDir;// = System.getProperty("user.home") + "/.tinySQL";
  protected Hashtable tables;
  protected String encoding;
  protected boolean readOnly;
  protected Properties p;

  protected byte[] delpref;
  protected byte[] delpost;
  protected byte[] colpref;
  protected byte[] colpost;
  protected byte[] rowpref;
  protected byte[] rowpost;
  protected byte[] tablepref;
  protected byte[] tablepost;

  protected String defext;
  protected String indext;
  protected String tableext;
  protected String quoting;
  protected textFileQuoting quotingEngine;
  protected int deleteMode;
  protected int insertMode;
  protected boolean ignoreLastColumnPostfix;
  protected boolean ignoreFirstColumnPrefix;
  protected boolean inMemory;

  public textFileDatabase(String datadir, Properties p) throws tinySQLException
  {
    this.dataDir = datadir;
    setProperties(p);
  }

  public void setProperties(Properties p) throws tinySQLException
  {
    this.p = p;
    this.inMemory = p.getProperty("inmemory", "false").equalsIgnoreCase("true");
    this.encoding = p.getProperty("encoding", "Cp1252");
    this.readOnly = p.getProperty("readonly", "false").equalsIgnoreCase("true");
    this.defext = p.getProperty("definition-extension", ".def");
    this.indext = p.getProperty("index-extension", ".ind");
    this.tableext = p.getProperty("table-extension", "");
    this.quoting = p.getProperty("qouting.engine", textFileQuoting.class.getName());
    quotingEngine = loadTextFileQuoting(quoting);
    String delpref = p.getProperty("delete.prefix", "");
    String delpost = p.getProperty("delete.postfix", "");
    String colpref = p.getProperty("column.prefix", "");
    String colpost = p.getProperty("column.postfix", "");
    String rowpref = p.getProperty("row.prefix", "");
    String rowpost = p.getProperty("row.postfix", "\n");
    String tablepref = p.getProperty("table.prefix", "");
    String tablepost = p.getProperty("table.postfix", "");
    String deleteMode = p.getProperty("config.delete", "default").trim(); // may be none or pack
    String insertMode = p.getProperty("config.insert", "default").trim();

    String ignoreFirstColumnPrefix = p.getProperty("ignore.first.column.prefix", "false");
    String ignoreLastColumnPostfix = p.getProperty("ignore.first.column.postfix", "false");

    this.ignoreFirstColumnPrefix = ignoreFirstColumnPrefix.trim().equalsIgnoreCase("true");
    this.ignoreLastColumnPostfix = ignoreLastColumnPostfix.trim().equalsIgnoreCase("true");

    if (deleteMode.equals("none"))
    {
      this.deleteMode = DELETE_NONE;
    }
    else if (deleteMode.equals("pack"))
    {
      this.deleteMode = DELETE_PACK;
    }
    else
    {
      this.deleteMode = DELETE_DEFAULT;
    }

    if (insertMode.equals("speed"))
      this.insertMode = INSERT_SPEED;
    else if (insertMode.equals("size"))
      this.insertMode = INSERT_SIZE;
    else
      this.insertMode = INSERT_DEFAULT;

    try
    {
      this.delpref = delpref.getBytes(encoding);
      this.delpost = delpost.getBytes(encoding);
      this.colpref = colpref.getBytes(encoding);
      this.colpost = colpost.getBytes(encoding);
      this.rowpref = rowpref.getBytes(encoding);
      this.rowpost = rowpost.getBytes(encoding);
      this.tablepref = tablepref.getBytes(encoding);
      this.tablepost = tablepost.getBytes(encoding);
    }
    catch (UnsupportedEncodingException use)
    {
      throw new tinySQLException(use);
    }
  }

  protected textFileQuoting loadTextFileQuoting(String classname) throws tinySQLException
  {
    try
    {
      Class c = Class.forName(classname);
      textFileQuoting o = (textFileQuoting) c.newInstance();
      o.setDatabase(this);
      o.init();
      return o;
    }
    catch (Exception e)
    {
      throw new tinySQLException("QuotingEngine not found: " + classname);
    }
  }

  public Properties getProperties()
  {
    return p;
  }

  public textFileQuoting getQuoting()
  {
    return quotingEngine;
  }

  public boolean isIgnoringFirstColumnPrefix()
  {
    return ignoreFirstColumnPrefix;
  }

  public boolean isIgnoringLastColumnPostfix()
  {
    return ignoreLastColumnPostfix;
  }

  /**
   * @return either DELETE_NONE, DELETE_PACK or DELETE_DEFAULT
   */
  public int getDeleteMode()
  {
    //Log.debug("Default DeleteMode : " + deleteMode);

    return deleteMode;
  }

  public int getInsertMode()
  {
    return insertMode;
  }

  public boolean isReadOnly()
  {
    return readOnly;
  }

  /**
   * Creates a table given the name and a vector of
   * column definition (tsColumn) arrays.<br>
   *
   * @param table_name the name of the table
   * @param v a Vector containing arrays of column definitions.
   * @see tinySQL#CreateTable
   */
  public boolean CreateTable(CreateTableStatement stmt)
      throws tinySQLException
  {

    if (isReadOnly())
    {
      throw new tinySQLException("Database is readonly");
    }
    try
    {
      String table_name = stmt.getTable();
      Vector coldefs = stmt.getColumnDefinitions();

      int numCols = coldefs.size();
      int recordLength = 1;        // 1 byte for the flag field

      // make the data directory, if it needs to be make
      //
      mkDataDirectory();


      Vector v = new Vector();
      for (int i = 0; i < numCols; i++)
      {
        ColumnDefinition coldef = (ColumnDefinition) coldefs.elementAt(i);

        if (!coldef.isPrimaryKey()) {
          tsColumn col = coldef.getColumn();
          v.add(col);
        } else {
          // Primary key found, create index
          db_createIndex(table_name, coldef.getName(), i);
        }
      }


      db_createTable(table_name, v);
    }
    catch (IOException ioe)
    {
      throw new tinySQLException("Create Table failed", ioe);
    }
    return false;
  }

  public String getTableExtension()
  {
    return tableext;
  }

  public String getDefinitionExtension()
  {
    return defext;
  }

  public String getIndexExtension()
  {
    return indext;
  }

  public String getEncoding()
  {
    return encoding;
  }

  public byte[] getDelPostfix()
  {
    return delpost;
  }

  public byte[] getDelPrefix()
  {
    return delpref;
  }

  public byte[] getColumnPostfix()
  {
    return colpost;
  }

  public byte[] getColumnPrefix()
  {
    return colpref;
  }

  public byte[] getRowPostfix()
  {
    return rowpost;
  }

  public byte[] getRowPrefix()
  {
    return rowpref;
  }

  public byte[] getTablePostfix()
  {
    return tablepost;
  }

  public byte[] getTablePrefix()
  {
    return tablepref;
  }


  /**
   * Low level function of physicaly removing a table. While drop table
   * may alter associated tables and databases, this function simply
   * removes any files needed for the data table.
   */
  protected abstract void db_removeTable(String table_name) throws IOException;

  /**
   * Low level function of physicaly removing a table. While drop table
   * may alter associated tables and databases, this function simply
   * removes any files needed for the data table.
   */
  protected abstract void db_renameTable(String table_name, String newname) throws IOException;

  /**
   * Create the META and DATA files for the table. While CreateTable may update some
   * arbitary database structures, this function just creates the specifiy
   * file structures needed for a data table.
   */
  protected abstract void db_createTable(String table_name, Vector v)
      throws IOException, tinySQLException;

  /**
   * Create the INDEX files for the table.
   */
  protected abstract void db_createIndex(String table_name, String primaryKey,
                                         int primaryKeyTablePos)
      throws IOException, tinySQLException;


  /**
   *
   * Return a tinySQLTable object, given a table name.
   *
   * @param table_name
   * @see tinySQL#getTable
   *
   */
  public abstract tinySQLTable openTable(String table_name) throws tinySQLException;


  /*
   *
   * Make the data directory unless it already exists
   *
   */
  protected abstract void mkDataDirectory() throws NullPointerException;

}

