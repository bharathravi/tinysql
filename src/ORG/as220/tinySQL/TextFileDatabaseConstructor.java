package ORG.as220.tinySQL;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: bharath
 * Date: 2/24/13
 * Time: 9:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextFileDatabaseConstructor {

  public static textFileDatabase createTextFileDatabase(String dataDir,
                                                        Properties properties) throws tinySQLException {
    boolean inmemory = properties.getProperty("inmemory", "false")
        .equalsIgnoreCase("true");

    if (!inmemory) {
      return new diskTextFileDatabase(dataDir, properties);
    } else {
      return new memoryTextFileDatabase(dataDir, properties);
    }

  }

  protected BufferedOutputStream getFileOutputStream(String filename)
      throws FileNotFoundException {
      return new BufferedOutputStream(
          new FileOutputStream(filename));
  }
}
