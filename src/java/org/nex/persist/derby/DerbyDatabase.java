/*
 *  Copyright (C) 2005  Jack Park,
 * 	mail : jackpark@gmail.com
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
package org.nex.persist.derby;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Statement;
//import javax.sql.DataSource;
import org.nex.ibis.IBISException;
import org.nex.persist.IDatabase;

//import org.apache.derby.jdbc.EmbeddedDataSource;

//import java.util.Properties;

import org.apache.log4j.Logger;
/**
 * <p>Title: NexistGroup utilities</p>
 * <p>Description: Common tools</p>
 * <p>Copyright: Copyright (c) 2005 Jack Park</p>
 * <p>Company: NexistGroup</p>
 * @author Jack Park
 * @version 1.0
 */

public class DerbyDatabase implements IDatabase {
	private Logger log = Logger.getLogger(DerbyDatabase.class);
  public static final String derbyJdbcDriver =
      "org.apache.derby.jdbc.EmbeddedDriver";
  private String userName, password, connectionString;
  private String databaseName;
//  private Properties props;
//  private EmbeddedDataSource database;


  /**
   * <p>
   * Create a Derby database in %INSTALL_HOME%/DerbyDatabase/dbName
   * </p>
   * @param dbName
   * @param userName
   * @param pwd
   * @throws Exception
   */
  public DerbyDatabase(String dbName,String userName, String pwd, String filePath)
                    throws Exception {
	  this.userName = userName;
	  this.password = pwd;
	  this.databaseName = dbName;
//	  database = new EmbeddedDataSource();
//	  database.setUser(userName);
//	  database.setPassword(pwd);
//	  database.setDatabaseName(dbName);
//	  database.setCreateDatabase("create");
//	  database.setShutdownDatabase(null);
      connectionString = "jdbc:derby:"+filePath+/*"/"+*/dbName+";create=true;";
//    props = new Properties();
//    props.put("user",userName);
//    props.put("password",pwd);
//    System.out.println("DerbyDatabase- "+connectionString);
    //force loading driver
    //will toss a bitch if driver class not found
      Class.forName(derbyJdbcDriver).newInstance();
      log.debug("DerbyDatabase started: "+connectionString);
//      Connection c = DriverManager.getConnection(connectionString, userName, password);

//    System.out.println("DerbyDatabase+ "+c);
  }

  /**
   * <p>
   * This method depends on the database being available within this
   * filesystem.
   * </p>
   * <p>
   * Does not set autocommit:
   * To use:
   * <code>con.setAutoCommit(false); con.commit()</code>
   * or
   * <code>con.setAutoCommit(true)</code>
   * </p>
   * @return
   * @throws SQLException
   */
  public Connection getConnection() throws SQLException {
//	  return database.getConnection(/*userName, password*/);
    return DriverManager.getConnection(connectionString/*, userName,password*/);
  }

  /**
   * <p>
   * Note: should only shutdown when everybody is done
   * </p>
   *
   */
  public void shutDown() {
	  try {
              DriverManager.getConnection("jdbc:derby:"+databaseName+";shutdown=true");
	  } catch (SQLException e) {
		  log.error("DerbyDatabase.shutDownDerby "+databaseName+" error "+e.getMessage());
	  }
  }
  
  public void closeConnection(Connection con) throws SQLException {
	  if (con != null)
		  con.close();
  }
  
	public void close(Statement s) throws IBISException {
	    try {
	          if(s != null) s.close();
	      } catch(SQLException e) {
	        log.error("UserDatabase.close Statement "+e.getMessage());
	        throw new IBISException(e);
	      }
	}
	public void close(ResultSet s) throws IBISException {
	    try {
	          if(s != null) s.close();
	      } catch(SQLException e) {
	        log.error("UserDatabase.close ResultSet "+e.getMessage());
	        throw new IBISException(e);
	      }
	}
	public void close(PreparedStatement s) throws IBISException {
	    try {
	          if(s != null) s.close();
	      } catch(SQLException e) {
	        log.error("UserDatabase.close PreparedStatement "+e.getMessage());
	        throw new IBISException(e);
	      }
	}
}
