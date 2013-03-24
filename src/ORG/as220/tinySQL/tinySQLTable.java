/*
 *
 * tinySQLTable - abstract class for physical table access under tinySQL
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
 *
 */

package ORG.as220.tinySQL;

import ORG.as220.tinySQL.util.Log;

import java.io.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * tinySQLTable is a abstract base for all representations of physical data
 * tables. It assumes that the datatable is a random accessible collection
 * of records. tinySQLTable can not be used to modify columnvalues directly,
 * it returns and receives tsPhysicalRow-objects which represent a single
 * row of data.
 *
 * tinySQL does not know about the used storage format for JDBC-Objects.
 * Every table supplies a Converter which is can be used to convert objects
 * into the native representation. If you use a tinySQLTableView as frontend
 * to the table, conversion is done while setting the row in updateRow() or
 * insertRow ().
 */
public abstract class tinySQLTable
{

  private String table; // the name of the table
  private tinySQLConverter converter = null;
  private Vector views;

  // Table statistics
  private String primaryKey = "";
  private Object latestPrimaryKey = null;
  private int numRows = 0;
  private boolean hasPrimaryKey;
  private int primaryKeyTablePos;

  /**
   * creates a new tinySQLTable with the name <code>tablename</code>
   */
  public tinySQLTable(String tablename)
  {
    this.table = tablename;
    views = new Vector();
  }

  /**
   * returns the name of this table in the database. Most likely this
   * will be the filename of the tablefile.
   */
  public String getName()
  {
    return table;
  }

  /**
   * callback function called by tinySQLTableView to register itself as
   * a new view and get informed when this table is closed.
   */
  public void createdView(tinySQLTableView view) throws tinySQLException
  {
    views.addElement(view);
  }

  /**
   * callback function called by tinySQLTableView to remove itself from
   * the list of registered columns.
   */
  public void removedView(tinySQLTableView view)
  {
    views.removeElement(view);
    Log.warn("Table " + getName() + ": Open Views left: " + views.size());
  }

  /**
   * return all views as enumeration
   */
  public Enumeration getViews()
  {
    Vector v = new Vector(views);
    return v.elements();
  }

  /**
   * return the number of currently open views for this table
   */
  public int getViewCount()
  {
    return views.size();
  }

  /**
   * Constructs an converter. A converter converts data from JDBC-Objects
   * into the native format and vice versa.
   * If the converter is not yet initialized, it wil be created.
   *
   * @returns the current converter for this table.
   * @see Converter
   */
  public tinySQLConverter getConverter() throws tinySQLException
  {
    if (converter == null)
      converter = new tinySQLConverter();

    return converter;
  }

  /**
   *
   * close method. Try not to call this until you are sure
   * the object is about to go out of scope.
   *
   * This method should be called from dbfFile.closeTable ()
   *
   * @returns true, if the close operation does not want to remove the
   * table yet. The table remains open.
   */
  public abstract boolean close() throws tinySQLException;

  /**
   * Retrieves the column definition for the column on position <code>col</code>.
   * The delete-flag is not a defined column and cannot be queried directly.
   *
   * @returns the requested columndefinition
   */
  public abstract tsColumn getColumnDefinition(int column);

  /**
   * Returns the number of columns visible to the user
   */
  public abstract int getColumnCount();

  /**
   * Returns the number of rows in the table. This may include also deleted
   * records.
   */
  public abstract int getRowCount();

  /**
   *
   * Updates the row in the table.
   *
   * @param row the number of the record to update
   * @param values the row that contains the new JDBC-Objects for the table-row
   * @see tinySQLTable#UpdateCurrentRow
   * @throws tinySQLException if an database error occured or the database is
   * readonly
   */
  public abstract void updateRow(int row, tsRawRow values)
      throws tinySQLException;

  /**
   * Returns the contents of a row
   */
  public abstract tsRawRow getRow(int row) throws tinySQLException;

  /**
   *
   * Insert a row at the end of the table.
   *
   * @param values Ordered Vector (must match order of c) of values
   * @see tinySQLTable#InsertRow()
   *
   */
  public abstract int insertRow(tsRawRow v) throws tinySQLException;

  /**
   *
   * Delete the row.
   *
   */
  public abstract void deleteRow(int row) throws tinySQLException;

  /**
   * checks whether the specified row is deleted
   *
   * @returns true, if the row is deleted, false otherwise
   * @throws tinySQLException on database or IO error
   */
  public abstract boolean isDeleted(int row) throws tinySQLException;

  /**
   * tinySQL has no notion of readonly tables by default.
   *
   * @returns false
   */
  public boolean isReadOnly()
  {
    return false;
  }

  /**
   * creates the table's notion of a empty raw column. The column is used to
   * convert JDBC values into native values and back.
   */
  public abstract tsRawRow getInsertRow() throws tinySQLException;

  /**
   * creates the table's notion of a filled raw column. The column is used to
   * convert JDBC values into native values and back.
   */
  public tsRawRow getUpdateRow(int row) throws tinySQLException
  {
    return getRow(row);
  }

  public boolean isHasPrimaryKey() {
    return hasPrimaryKey;
  }

  public void setHasPrimaryKey(boolean hasPrimaryKey) {
    this.hasPrimaryKey = hasPrimaryKey;
  }

  public String getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(String primaryKey) {
    this.primaryKey = primaryKey;
  }

  public Object getLatestPrimaryKey() {
    return latestPrimaryKey;
  }

  public void setLatestPrimaryKey(Object latestPrimaryKey) {
    this.latestPrimaryKey = latestPrimaryKey;
  }

  public int getNumRows() {
    return numRows;
  }

  public void setNumRows(int numRows) {
    this.numRows = numRows;
  }

  protected void readIndexInputStream(InputStream fdef) throws IOException, tinySQLException {
    // use a StreamTokenizer to break up the stream.
    //
    Reader r = new BufferedReader(
        new InputStreamReader(fdef));
    StreamTokenizer def = new StreamTokenizer(r);

    // set the | as a delimiter, and set everything between
    // 0 and z as word characters. Let it know that eol is
    // *not* significant, and that it should parse numbers.
    //
    def.whitespaceChars('|', '|');
    def.wordChars('0', 'z');
    def.eolIsSignificant(false);
    def.parseNumbers();

    // Read primary key column
    def.nextToken();

    if (def.sval.equals("PRIMARY_KEY"))
    {
      def.nextToken();
      setPrimaryKey(def.sval);
      def.nextToken();
      setPrimaryKeyTablePos((int) def.nval);
    } else {
      throw new tinySQLException("Bad Index");
    }

    // Read the latest primary key in
    def.nextToken();
    Object nativeval = null;
    if (def.sval.equals("PRIMARY_KEY_LATEST")) {
      def.nextToken();
      if (def.sval == null) {

        nativeval = new Double(def.nval).toString().getBytes();

      } else {
        nativeval = new String(def.sval);
      }
    } else {
      System.out.println(def.sval);
      throw new tinySQLException("Bad Index: No latest prim key");
    }

    // Read the number of rows
    def.nextToken();
    if (def.sval.equals("NUM_ROWS")) {
      def.nextToken();
      setNumRows((int) def.nval);
    } else {
      throw new tinySQLException("Bad Index: No latest num rows");
    }

    if (getNumRows() > 0) {
      Object val = getConverter().convertNativeToJDBC(
          getColumnDefinition(primaryKeyTablePos), nativeval);
      setLatestPrimaryKey(val);
    }



    setHasPrimaryKey(true);
  }

  public int getPrimaryKeyTablePos() {
    return primaryKeyTablePos;
  }

  public void setPrimaryKeyTablePos(int primaryKeyTablePos) {
    this.primaryKeyTablePos = primaryKeyTablePos;
  }

  protected byte[] getIndexString() throws tinySQLException {
    return ("PRIMARY_KEY|" + getPrimaryKey() + "|" + getPrimaryKeyTablePos() + "\n"
        + "PRIMARY_KEY_LATEST|" + getLatestPrimaryKey() + "\n"
        +"NUM_ROWS|" + getNumRows() + "\n").getBytes();
  }


}
