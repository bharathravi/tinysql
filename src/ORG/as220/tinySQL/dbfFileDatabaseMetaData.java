/*
 * dbfFileDatabaseMetaData.java
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

import java.io.File;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;


/**
 * Comprehensive information about the database as a whole.
 *
 * <P>Many of the methods here return lists of information in
 * the form of <code>ResultSet</code> objects.
 * You can use the normal ResultSet methods such as getString and getInt
 * to retrieve the data from these ResultSets.  If a given form of
 * metadata is not available, these methods should throw an SQLException.
 *
 * <P>Some of these methods take arguments that are String patterns.  These
 * arguments all have names such as fooPattern.  Within a pattern String, "%"
 * means match any substring of 0 or more characters, and "_" means match
 * any one character. Only metadata entries matching the search pattern
 * are returned. If a search pattern argument is set to a null ref,
 * that argument's criteria will be dropped from the search.
 *
 * <P>An <code>SQLException</code> will be thrown if a driver does not support a meta
 * data method.  In the case of methods that return a ResultSet,
 * either a ResultSet (which may be empty) is returned or a
 * SQLException is thrown.
 *
 * dBase read/write access <br>
 * @author Brian Jepson <bjepson@home.com>
 * @author Marcel Ruff <ruff@swand.lake.de> Added DatabaseMetaData with JDK 2 support
 * @author Thomas Morgner <mgs@sherito.org> Changed DatabaseMetaData to use java.sql.Types.
 */
public class dbfFileDatabaseMetaData extends tinySqlDatabaseMetaData
{

  /**
   * creates a new dbfFileDatabaseMetaData. These MetaDatas will override
   * some of tinySQLs default values.
   */
  public dbfFileDatabaseMetaData(tinySQLConnection connection)
  {
    super(connection);

  }

  /**
   * @returns the current data directory
   */
  private String getDataDir()
  {
    String url = ((dbfFileConnection) getConnection()).getUrl();
    if (url.length() <= 13)
      return null;

    String dataDir = url.substring(13);
    return dataDir;
  }

  /**
   * Gets a description of all the standard SQL types supported by
   * this database. They are ordered by DATA_TYPE and then by how
   * closely the data type maps to the corresponding JDBC SQL type.
   *
   * <P>Each type description has the following columns:
   *  <OL>
   *  <LI><B>TYPE_NAME</B> String => Type name
   *  <LI><B>DATA_TYPE</B> short => SQL data type from java.sql.Types
   *  <LI><B>PRECISION</B> int => maximum precision
   *  <LI><B>LITERAL_PREFIX</B> String => prefix used to quote a literal
   *      (may be null)
   *  <LI><B>LITERAL_SUFFIX</B> String => suffix used to quote a literal
   (may be null)
   *  <LI><B>CREATE_PARAMS</B> String => parameters used in creating
   *      the type (may be null)
   *  <LI><B>NULLABLE</B> short => can you use NULL for this type?
   *      <UL>
   *      <LI> typeNoNulls - does not allow NULL values
   *      <LI> typeNullable - allows NULL values
   *      <LI> typeNullableUnknown - nullability unknown
   *      </UL>
   *  <LI><B>CASE_SENSITIVE</B> boolean=> is it case sensitive?
   *  <LI><B>SEARCHABLE</B> short => can you use "WHERE" based on this type:
   *      <UL>
   *      <LI> typePredNone - No support
   *      <LI> typePredChar - Only supported with WHERE .. LIKE
   *      <LI> typePredBasic - Supported except for WHERE .. LIKE
   *      <LI> typeSearchable - Supported for all WHERE ..
   *      </UL>
   *  <LI><B>UNSIGNED_ATTRIBUTE</B> boolean => is it unsigned?
   *  <LI><B>FIXED_PREC_SCALE</B> boolean => can it be a money value?
   *  <LI><B>AUTO_INCREMENT</B> boolean => can it be used for an
   *      auto-increment value?
   *  <LI><B>LOCAL_TYPE_NAME</B> String => localized version of type name
   *      (may be null)
   *  <LI><B>MINIMUM_SCALE</B> short => minimum scale supported
   *  <LI><B>MAXIMUM_SCALE</B> short => maximum scale supported
   *  <LI><B>SQL_DATA_TYPE</B> int => unused
   *  <LI><B>SQL_DATETIME_SUB</B> int => unused
   *  <LI><B>NUM_PREC_RADIX</B> int => usually 2 or 10
   *  </OL>
   *
   * @return ResultSet - each row is a SQL type description
   * @exception SQLException if a database access error occurs
   */
  public ResultSet getTypeInfo() throws SQLException
  {

    Vector columns = new Vector();

    tsColumn jsc = new tsColumn("TYPE_NAME");
    jsc.setType(Types.CHAR);
    jsc.setSize(10);
    columns.addElement(jsc);

    jsc = new tsColumn("DATA_TYPE");
    jsc.setType(Types.NUMERIC);
    jsc.setSize(6);
    columns.addElement(jsc);

    jsc = new tsColumn("PRECISION");
    jsc.setType(Types.NUMERIC);
    jsc.setSize(8);
    columns.addElement(jsc);

    jsc = new tsColumn("LITERAL_PREFIX");
    jsc.setType(Types.CHAR);
    jsc.setSize(1);
    columns.addElement(jsc);

    jsc = new tsColumn("LITERAL_SUFFIX");
    jsc.setType(Types.CHAR);
    jsc.setSize(1);
    columns.addElement(jsc);

    jsc = new tsColumn("CREATE_PARAMS");
    jsc.setType(Types.CHAR);
    jsc.setSize(20);
    columns.addElement(jsc);

    jsc = new tsColumn("NULLABLE");
    jsc.setType(Types.NUMERIC);
    jsc.setSize(6);
    columns.addElement(jsc);

    jsc = new tsColumn("CASE_SENSITIVE");
    jsc.setType(Types.BIT);
    jsc.setSize(1);
    columns.addElement(jsc);

    jsc = new tsColumn("SEARCHABLE");
    jsc.setType(Types.NUMERIC);
    jsc.setSize(6);
    columns.addElement(jsc);

    tsResultSet jrs = new tsResultSet(new tsPhysicalRow(columns));
    /*
    *  <LI><B>UNSIGNED_ATTRIBUTE</B> boolean => is it unsigned?
    *  <LI><B>FIXED_PREC_SCALE</B> boolean => can it be a money value?
    *  <LI><B>AUTO_INCREMENT</B> boolean => can it be used for an
    *      auto-increment value?
    *  <LI><B>LOCAL_TYPE_NAME</B> String => localized version of type name
    *      (may be null)
    *  <LI><B>MINIMUM_SCALE</B> short => minimum scale supported
    *  <LI><B>MAXIMUM_SCALE</B> short => maximum scale supported
    *  <LI><B>NUM_PREC_RADIX</B> int => usually 2 or 10
    */
    tsPhysicalRow record = jrs.createPhysicalRow();
    record.put(record.findColumn("TYPE_NAME"), DBFHeader.typeToLiteral(Types.CHAR));    // "CHAR", String
    record.put(record.findColumn("DATA_TYPE"), new Integer(Types.CHAR));
    record.put(record.findColumn("PRECISION"), new Integer(254));
    record.put(record.findColumn("LITERAL_PREFIX"), "\'");
    record.put(record.findColumn("LITERAL_SUFFIX"), "\'");
    record.put(record.findColumn("CREATE_PARAMS"), new Integer(0));
    record.put(record.findColumn("NULLABLE"), new Integer(typeNullable));
    record.put(record.findColumn("CASE_SENSITIVE"), "Y");
    record.put(record.findColumn("SEARCHABLE"), new Integer(typeSearchable));
    jrs.addPhysicalRow(record);

    record = jrs.createPhysicalRow();
    record.put(record.findColumn("TYPE_NAME"), DBFHeader.typeToLiteral(Types.NUMERIC));    // "NUMERIC", double
    record.put(record.findColumn("DATA_TYPE"), new Integer(Types.FLOAT));
    record.put(record.findColumn("PRECISION"), new Integer(19));
    record.put(record.findColumn("LITERAL_PREFIX"), "");
    record.put(record.findColumn("LITERAL_SUFFIX"), "");
    record.put(record.findColumn("CREATE_PARAMS"), new Integer(0));
    record.put(record.findColumn("NULLABLE"), new Integer(typeNullable));
    record.put(record.findColumn("CASE_SENSITIVE"), "N");
    record.put(record.findColumn("SEARCHABLE"), new Integer(typeSearchable));
    jrs.addPhysicalRow(record);

    record = jrs.createPhysicalRow();
    record.put(record.findColumn("TYPE_NAME"), DBFHeader.typeToLiteral(Types.BIT));     // "CHAR", boolean "YyNnTtFf"
    record.put(record.findColumn("DATA_TYPE"), new Integer(Types.BIT));
    record.put(record.findColumn("PRECISION"), new Integer(1));
    record.put(record.findColumn("LITERAL_PREFIX"), "");
    record.put(record.findColumn("LITERAL_SUFFIX"), "");
    record.put(record.findColumn("CREATE_PARAMS"), new Integer(0));
    record.put(record.findColumn("NULLABLE"), new Integer(typeNullable));
    record.put(record.findColumn("CASE_SENSITIVE"), "N");
    record.put(record.findColumn("SEARCHABLE"), new Integer(typeSearchable));
    jrs.addPhysicalRow(record);

    record = jrs.createPhysicalRow();
    record.put(record.findColumn("TYPE_NAME"), DBFHeader.typeToLiteral(Types.INTEGER));     // "INT", unsigned long == NUMERIC
    record.put(record.findColumn("DATA_TYPE"), new Integer(Types.INTEGER));
    record.put(record.findColumn("PRECISION"), new Integer(19));
    record.put(record.findColumn("LITERAL_PREFIX"), "");
    record.put(record.findColumn("LITERAL_SUFFIX"), "");
    record.put(record.findColumn("CREATE_PARAMS"), new Integer(0));
    record.put(record.findColumn("NULLABLE"), new Integer(typeNullable));
    record.put(record.findColumn("CASE_SENSITIVE"), "N");
    record.put(record.findColumn("SEARCHABLE"), new Integer(typeSearchable));
    jrs.addPhysicalRow(record);

    record = jrs.createPhysicalRow();
    record.put(record.findColumn("TYPE_NAME"), DBFHeader.typeToLiteral(Types.DATE));     // "DATE", date
    record.put(record.findColumn("DATA_TYPE"), new Integer(Types.DATE));
    record.put(record.findColumn("PRECISION"), new Integer(8));
    record.put(record.findColumn("LITERAL_PREFIX"), "\'");
    record.put(record.findColumn("LITERAL_SUFFIX"), "\'");
    record.put(record.findColumn("CREATE_PARAMS"), new Integer(0));
    record.put(record.findColumn("NULLABLE"), new Integer(typeNullable));
    record.put(record.findColumn("CASE_SENSITIVE"), "N");
    record.put(record.findColumn("SEARCHABLE"), new Integer(typeSearchable));
    jrs.addPhysicalRow(record);

    record = jrs.createPhysicalRow();
    record.put(record.findColumn("TYPE_NAME"), DBFHeader.typeToLiteral(Types.TIMESTAMP));     // "TIMESTAMP", timestamp
    record.put(record.findColumn("DATA_TYPE"), new Integer(Types.TIMESTAMP));
    record.put(record.findColumn("PRECISION"), new Integer(8));
    record.put(record.findColumn("LITERAL_PREFIX"), "\'");
    record.put(record.findColumn("LITERAL_SUFFIX"), "\'");
    record.put(record.findColumn("CREATE_PARAMS"), new Integer(0));
    record.put(record.findColumn("NULLABLE"), new Integer(typeNullable));
    record.put(record.findColumn("CASE_SENSITIVE"), "N");
    record.put(record.findColumn("SEARCHABLE"), new Integer(typeSearchable));
    jrs.addPhysicalRow(record);

    return new tinySQLResultSet(jrs, null);
  }

  public boolean supportsSavepoints() throws SQLException {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean supportsNamedParameters() throws SQLException {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean supportsMultipleOpenResults() throws SQLException {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean supportsGetGeneratedKeys() throws SQLException {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public ResultSet getSuperTypes(String s, String s2, String s3) throws SQLException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public ResultSet getSuperTables(String s, String s2, String s3) throws SQLException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public ResultSet getAttributes(String s, String s2, String s3, String s4) throws SQLException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean supportsResultSetHoldability(int i) throws SQLException {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public int getResultSetHoldability() throws SQLException {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public int getDatabaseMajorVersion() throws SQLException {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public int getDatabaseMinorVersion() throws SQLException {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public int getJDBCMajorVersion() throws SQLException {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public int getJDBCMinorVersion() throws SQLException {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public int getSQLStateType() throws SQLException {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean locatorsUpdateCopy() throws SQLException {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean supportsStatementPooling() throws SQLException {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public RowIdLifetime getRowIdLifetime() throws SQLException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public ResultSet getSchemas(String s, String s2) throws SQLException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public ResultSet getClientInfoProperties() throws SQLException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public ResultSet getFunctions(String s, String s2, String s3) throws SQLException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public ResultSet getFunctionColumns(String s, String s2, String s3, String s4) throws SQLException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public ResultSet getPseudoColumns(String s, String s2, String s3, String s4) throws SQLException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean generatedKeyAlwaysReturned() throws SQLException {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * Gets a description of tables available in a catalog.
   *
   * <P>Only table descriptions matching the catalog, schema, table
   * name and type criteria are returned.  They are ordered by
   * TABLE_TYPE, TABLE_SCHEM and TABLE_NAME.
   *
   * <P>Each table description has the following columns:
   *  <OL>
   *  <LI><B>TABLE_CAT</B> String => table catalog (may be null)
   *  <LI><B>TABLE_SCHEM</B> String => table schema (may be null)
   *  <LI><B>TABLE_NAME</B> String => table name
   *  <LI><B>TABLE_TYPE</B> String => table type.  Typical types are "TABLE",
   *      "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY",
   *      "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
   *  <LI><B>REMARKS</B> String => explanatory comment on the table
   *  </OL>
   *
   * <P><B>Note:</B> Some databases may not return information for
   * all tables.
   *
   * @param catalog a catalog name; "" retrieves those without a
   * catalog; null means drop catalog name from the selection criteria
   * THIS VALUE IS IGNORED
   * @param schemaPattern THIS VALUE IS IGNORED
   * @param tableNamePattern a table name pattern, onullo or "%" delivers all
   *                         token will be handled as substrings
   * @param types a list of table types to include; null returns all DBF types
   *              only "TABLE" is supported, others like "VIEW", "SYSTEM TABLE", "SEQUENCE"
   *              are ignored.
   * @return ResultSet - each row is a table description
   * @exception SQLException if a database access error occurs
   * @see #getSearchStringEscape
   *
   */
  public ResultSet getTables(String catalog, String schemaPattern,
                             String tableNamePattern, String types[])
      throws tinySQLException
  {
    String dataDir = getDataDir();
    if (dataDir == null) return null;

    if (types == null)
    {
      types = new String[1];
      types[0] = "TABLE";
    }

    Vector columns = new Vector();

    // add the header ...
    tsColumn jsc = new tsColumn("TABLE_CAT");
    jsc.setType(Types.CHAR);    // CHAR max 254 bytes
    jsc.setDefaultValue("");
    jsc.setSize(10);
    columns.addElement(jsc);

    jsc = new tsColumn("TABLE_SCHEM");
    jsc.setType(Types.CHAR);    // CHAR max 254 bytes
    jsc.setDefaultValue("");
    jsc.setSize(10);
    columns.addElement(jsc);

    jsc = new tsColumn("TABLE_NAME");
    jsc.setType(Types.CHAR);    // CHAR max 254 bytes
    jsc.setSize(250);
    columns.addElement(jsc);

    jsc = new tsColumn("TABLE_TYPE");
    jsc.setType(Types.CHAR);    // CHAR max 254 bytes
    jsc.setSize(40);
    jsc.setDefaultValue("TABLE");
    columns.addElement(jsc);

    jsc = new tsColumn("TABLE_REMARKS");
    jsc.setType(Types.CHAR);    // CHAR max 254 bytes
    jsc.setSize(254);
    jsc.setDefaultValue("");
    columns.addElement(jsc);
    tsResultSet jrs = new tsResultSet(new tsPhysicalRow(columns));

    // add the data ...
    for (int itype = 0; itype < types.length; itype++)
    {
      String type = types[itype];
      if (type == null) continue;
      String extension = null;
      if (type.equalsIgnoreCase("TABLE"))
      {
        extension = dbfFileTable.DBF_EXTENSION; // ".DBF";
      }
      if (extension == null)
        continue;

      Vector vec = Utils.getAllFiles(dataDir, extension);
      for (int ii = 0; ii < vec.size(); ii++)
      {
        String tableName = Utils.stripPathAndExtension(((File) vec.elementAt(ii)).toString());
        if (tableNamePattern == null || tableNamePattern.equals("%") || tableName.indexOf(tableNamePattern) >= 0)
        {
          if (tableName.length() > jsc.getSize())
          {
            jsc.setSize(tableName.length());
          }
          tsPhysicalRow record = jrs.createPhysicalRow();
          record.put(record.findColumn("TABLE_NAME"), tableName);
          jrs.addPhysicalRow(record);
        }
      }
    }

    // This Resultset is not created by an statement
    return new tinySQLResultSet(jrs, null);
  }


  /**
   * What's the maximum length of a table name? dbase-files can have any filename-length,
   * but we are limiting it to 250 chars.
   *
   * @return max name length in bytes;
   *      a result of zero means that there is no limit or the limit is not known
   * @exception SQLException if a database access error occurs
   */
  public int getMaxTableNameLength()
  {
    return 250; // !!! filesystem name
  }

  /**
   * What's the version of this database product?
   *
   * @return database version
   * @exception SQLException if a database access error occurs
   */
  public String getDatabaseProductVersion()
  {
    return "dbf-driver";
  }

  /**
   * How many hex characters can you have in an inline binary literal?
   * dbase stores up to 255 characters in the main table, if you want to
   * store greater values, use binary memo fields.
   *
   * @return max binary literal length in hex characters;
   *      a result of zero means that there is no limit or the limit is not known
   * @exception SQLException if a database access error occurs
   */
  public int getMaxBinaryLiteralLength()
  {
    return 254;
  }

  /**
   * What's the max length for a character literal?
   * dbase stores up to 255 characters in the main table, if you want to
   * store greater values, use memo fields.
   *
   * @return max literal length;
   *      a result of zero means that there is no limit or the limit is not known
   * @exception SQLException if a database access error occurs
   */
  public int getMaxCharLiteralLength()
  {
    return 254;
  }

  /**
   * What's the limit on column name length?
   *
   * @return max column name length;
   *      a result of zero means that there is no limit or the limit is not known
   * @exception SQLException if a database access error occurs
   */
  public int getMaxColumnNameLength()
  {
    return 10;
  }

  /**
   * What's the maximum number of columns in a table?
   *
   * @return max number of columns;
   *      a result of zero means that there is no limit or the limit is not known
   * @exception SQLException if a database access error occurs
   */
  public int getMaxColumnsInTable()
  {
    return 255;
  }

  /**
   * What's the maximum length of a single row?
   *
   * @return max row size in bytes;
   *      a result of zero means that there is no limit or the limit is not known
   * @exception SQLException if a database access error occurs
   */
  public int getMaxRowSize()
  {
    return 65500;
  }


  public <T> T unwrap(Class<T> tClass) throws SQLException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isWrapperFor(Class<?> aClass) throws SQLException {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }
}



