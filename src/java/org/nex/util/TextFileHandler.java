/*
 *  Copyright (C) 2004,2005  Jack Park,
 * 	mail : jackpark@thinkalong.com
 *
 *  Part of <NexistGroup Objects>, an open source project.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.nex.util;

import  java.io.File;
import  java.io.FileInputStream;
import  java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import  java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import  java.io.BufferedReader;
import  java.io.IOException;
import java.io.FileNotFoundException;
import  javax.swing.JFileChooser;
import java.util.zip.*;

/**
 * TextFileHandler.java
 *  General purpose Text File handler
 *  @author Jack Park
 */
/**
 * FIXME: Errors should throw new RuntimeException
 */
public class TextFileHandler {
  private String fName = null;
  private String body = null;
  private BufferedReader inStream = null;
  private JFileChooser chooser = null;

	public TextFileHandler() {
	}
  //////////////////////////////////////
  // Directory services
  // To use:
  //      First save:
  //      // caller gets a file e.g. to set a document name
  //      File newFile = handler._saveAs();
  //      // callser uses that file
  //      if (newFile != null)
  //        handler.writeFile(newFile, bodyString);
  //////////////////////////////////////
  public File _saveAs() {
    File result = null;
    if (chooser==null)chooser = new JFileChooser(new File("."));
    int retVal = chooser.showSaveDialog(null);
    if(retVal == JFileChooser.APPROVE_OPTION) {
      result = chooser.getSelectedFile();
    }
    return result;
  }

  public void saveAs(String body) {
    File myFile = _saveAs();
    if (myFile != null) {
        writeFile(myFile, body);
    }
  }

  public File openFile() {
    return openFile(null);
  }

  public File openFile(String title) {
    File result = null;
    JFileChooser chooser = new JFileChooser(new File("."));
    if (title != null)
      chooser.setDialogTitle(title);
    int retVal = chooser.showOpenDialog(null);
    if(retVal == JFileChooser.APPROVE_OPTION) {
      result = chooser.getSelectedFile();
    }
    return result;
  }

  public File [] openFiles(String title) {
	  File [] result = null;
	    JFileChooser chooser = new JFileChooser(new File("."));
	    if (title != null)
	      chooser.setDialogTitle(title);
	    chooser.setMultiSelectionEnabled(true);
	    int retVal = chooser.showOpenDialog(null);
	    if(retVal == JFileChooser.APPROVE_OPTION) {
	      result = chooser.getSelectedFiles();
	    }
	    return result;
  }
  
  public File openDirectory() {
    return openDirectory(null);
  }

  public File openDirectory(String title) {
    File result = null;
    JFileChooser chooser = new JFileChooser(new File("."));
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    if (title != null)
      chooser.setDialogTitle(title);
    int retVal = chooser.showOpenDialog(null);
    if(retVal == JFileChooser.APPROVE_OPTION) {
      result = chooser.getSelectedFile();
    }
    return result;
  }
  //////////////////////////////////////
  //  Simple File handlers
  /////////////////////////////////////
  public String readFile(String fileName) {  // fully qualified name
     File f = new File(fileName);
     fName = fileName;
     return readFile(f);
  }
  public String readFile(File f) {
     int size = (int) f.length();
     int bytesRead = 0 ;
     body = null;
     try {
       FileInputStream in = new FileInputStream(f) ;

       byte[] data = new byte[size] ;
       in.read(data, 0, size);
       body = new String(data) ;
       in.close() ;
     } catch (IOException e) {
         System.out.println("Error: TextFileHandler couldn't read from " + f + "\n") ;
     }
     return body;
  }

  public void writeFile(String fileName, String inBody) {
     File f = new File(fileName) ;
     fName = fileName;
     writeFile(f, inBody);
  }

  public void writeFile(File f, String inBody) {
//  System.out.println("WRITING "+f);
     int size = (int) inBody.length();
     int bytesOut = 0 ;
     byte data[] = inBody.getBytes(); //new byte[size] ;
  //   data = body.getBytes();
     try {
       FileOutputStream out = new FileOutputStream(f) ;
       out.write(data, 0, size);
       out.flush() ;
       out.close() ;
     }
     catch (IOException e) {
        System.out.println("Error: TextFileHandler couldn't write to " + fName + "\n");
     }
    }

    //////////////////////////////////////
    //  Line-oriented File readers
    /////////////////////////////////////
    public String readFirstLine(String fileName) {
      File f = new File(fileName);
      return readFirstLine(f);
    }
    public String readFirstLine(File f) {
    fName = f.getName();
      try {
       FileInputStream in = new FileInputStream(f);
       inStream = new BufferedReader(new InputStreamReader(in));
     } catch (IOException e) {
         System.out.println("Error: TextFileHandler couldn't open a DataInputStream on " + fName + "\n");
     }
     return readNextLine();
    }
    /**
     *  Read a line from an open file
     *  Return null when done
     */
    public String readNextLine() {
      String str = null;
      try {
         str = inStream.readLine();
      } catch (IOException e) {
         System.out.println("Error: TextFileHandler couldn't read from " + fName + "\n");
      }
      return str;
    }

    ////////////////////////////////////////////
    // Serialized Java Class utilities
    ////////////////////////////////////////////

    public void persist(String fileName, Object obj) {
      try {
          new ObjectOutputStream(
                 new FileOutputStream(new File(fileName))).writeObject(obj);
      } catch (Exception e) {
//          e.printStackTrace();
          throw new RuntimeException(e);
      }
    }

    public Object restore(String fileName) {
      Object result = null;
      try {
        result = new ObjectInputStream(
            new FileInputStream(new File(fileName))).readObject();
      }
      catch (Exception e) {
//        e.printStackTrace();
        System.out.println("Restoring "+fileName);
 //       e.printStackTrace();
 //       throw new RuntimeException("Failed");
      }
      return result;
    }
    ////////////////////////////////////////////
    // GZip utilities
    ////////////////////////////////////////////

    /**
     * Save content to a .gz file
     * @param fileName e.g. foo.txt.gz
     * @param content
     */
    public void saveGZipFile(String fileName, String content) {
      try {
        GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(fileName));
        PrintWriter pw = new PrintWriter(out);
        pw.write(content);
        pw.flush();
        pw.close();
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }

    public PrintWriter getGZipWriter(String fileName) throws Exception {
        GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(fileName));
        return new PrintWriter(out);
    }

    public void saveGZipFile(File outFile, String content) throws Exception{
        GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(outFile));
        PrintWriter pw = new PrintWriter(out);
        pw.write(content);
        pw.flush();
        pw.close();
    }
    /**
     * Retrieve a String from a .gz file
     * @param fileName e.g. bar.xml.gz
     * @return
     */
    public String openGZipFile(String fileName) {
      try {
        GZIPInputStream in = new GZIPInputStream(new FileInputStream(
            fileName));
        StringBuffer buf = new StringBuffer();
        byte [] b = new byte[1024];
        int length;
        while ((length = in.read(b)) > 0) {
          String s = new String(b);
          buf.append(s);
        }
        return buf.toString().trim();
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
      return null;
    }

}
/**
	ChangeLog
	20020512	JP: minor fix in readFile
**/