package ORG.as220.tinySQL;

import ORG.as220.tinySQL.util.Log;

import java.io.*;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: bharath
 * Date: 2/23/13
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class MemoryFile {
  // A mem file is simply a string stored in memory
  // TODO(bharath): Is this sufficient or do we need a ByteString?
  private static HashMap<String, byte[] > memFiles = new HashMap<String, byte[]>();
  private static HashMap<String, Integer> memFileLength = new HashMap<String, Integer>();
  private static HashMap<String, String> filePermissions = new HashMap<String, String>();
  private int location = 0;

  private String myFilename = new String("");
  private Integer myFileLength = 0;

  private byte[] myFileContents = new byte[0];
  private String myPermissions = "";

  private static int FILE_BLOCK_SIZE = 65535;
  private int fileBlockCount = 1;

  public MemoryFile(String filename, String permissions) throws FileNotFoundException {

    this.myFilename = filename;
    this.location = 0;

    if (!memFiles.containsKey(myFilename)) {
      if (permissions.equals("r")) {
        throw new FileNotFoundException();
      }

      if (permissions.contains("rw")) {
        // System.out.println("Creating non existent file" + myFilename);

        myFileContents = new byte[FILE_BLOCK_SIZE * fileBlockCount];
        memFiles.put(myFilename, myFileContents);
        memFileLength.put(myFilename, 0);
      }

    } else {
      myFileContents = memFiles.get(myFilename);
      myFileLength = memFileLength.get(myFilename);
      myPermissions = filePermissions.get(myFilename);
      fileBlockCount = myFileContents.length/FILE_BLOCK_SIZE;
    }

  }

  public void close() throws IOException {
    checkFileOpen();
    memFiles.put(myFilename, myFileContents);
    memFileLength.put(myFilename, myFileLength);
    filePermissions.put(myFilename, myPermissions);
  }

  public void seek(int location) throws IOException {
    checkFileOpen();

    if (location < 0) {
      throw new IOException("Seek position less than 0");
    }

    if (location >= myFileContents.length) {
      this.location = myFileContents.length;
    } else {
      this.location = location;
    }
  }

  private void checkFileOpen() throws IOException {
    if (myFilename.isEmpty()) throw new IOException("File not open");
  }

  public void write(byte[] ovalue) throws IOException {
    checkFileOpen();

    boolean  expand = false;
    while (location + ovalue.length > FILE_BLOCK_SIZE * fileBlockCount) {
      fileBlockCount++;
      expand = true;
    }

    if (expand) {
      byte[] temp = new byte[FILE_BLOCK_SIZE * fileBlockCount];
      System.arraycopy(myFileContents, 0, temp, 0, location);
      myFileContents = temp;
    }

    System.out.println("LIMIT " + myFileContents.length);
    System.arraycopy(ovalue, 0, myFileContents, location, ovalue.length);
    location += ovalue.length;
    myFileLength += ovalue.length;
  }

  public void readFully(byte[] line) throws IOException {
    checkFileOpen();

    if (location + line.length > myFileContents.length) {
//      Byte[] bytes = new Byte[line.length];
//      //myFileContents.subList(location, myFileContents.size()).toArray(bytes);
//      for (int i = 0; i < bytes.length; ++i) {
//        line[i] = myFileContents.get(location + i);
//      }

      throw new EOFException();
    }

    System.arraycopy(myFileContents, location, line, 0, line.length);
    location += line.length;
  }

  public int length() throws IOException {
    checkFileOpen();

    return memFileLength.get(myFilename);
  }

  public static InputStream getInputStream(String filename) throws FileNotFoundException {
    if (memFiles.containsKey(filename)) {
//      Byte[] bytes = new Byte[memFiles.get(filename).size()];
//      memFiles.get(filename).toArray(bytes);
//
//      byte[] primitive = new byte[bytes.length];
//      for (int i = 0 ; i < bytes.length; ++i) {
//        primitive[i] = bytes[i];
//      }
      return new ByteArrayInputStream(memFiles.get(filename));
    } else {
      throw new FileNotFoundException(filename);
    }
  }

  public static void delFile(String myFilename) {
    if (memFiles.containsKey(myFilename)) {
      memFiles.remove(myFilename);
      memFileLength.remove(myFilename);
    } else {
      //Log.debug("File: " + myFilename + " does not exist. No action taken on delete.");
    }
  }

  public static boolean renameFile(String filename, String newFileName) {
    if (!memFiles.containsKey(filename)) {
      Log.warn("File " + filename + " does not exist");
      return false;
    }

    if (memFiles.containsKey(newFileName)) {
      Log.warn("File " + newFileName + " exists");
      return false;
    }

    memFiles.put(newFileName, memFiles.get(filename));
    memFileLength.put(newFileName, memFileLength.get(filename));
    memFiles.remove(filename);
    memFileLength.remove(filename);
    return true;
  }
}