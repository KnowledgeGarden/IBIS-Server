/*
 *  Copyright (C) 2009 Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Part of IBIS Server, an open source project.
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
package org.nex.ibis.model.files;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import org.apache.log4j.Logger;
import org.nex.ibis.IBISException;
import org.nex.ibis.model.api.IFileIndexDatabase;
import org.nex.ibis.model.api.IFileIndexDatabaseSchema;
import org.nex.persist.derby.DerbyDatabase;

/**
 * 
 * @author park
 *
 */
public class FileDatabase extends DerbyDatabase implements IFileIndexDatabase {
	private Logger log = Logger.getLogger(getClass());

	public FileDatabase(String dbName, String userName, String pwd,
			String filePath) throws Exception {
		super(dbName, userName, pwd, filePath);
	}

	
	public void addFile(Connection con, String path, String platform, String description, String userLocator)
			throws IBISException {
	    PreparedStatement s = null;
	    try {
	        s = con.prepareStatement(IFileIndexDatabaseSchema.insertFile);
	        s.setString(1,path); 
	        s.setString(2, platform);
	        s.setString(3, description);
	        s.setString(4, userLocator);
	        s.execute();
	    }
	    catch (SQLException e) {
	      log.error("FileDatabase.addFile " + e.getMessage());
	      throw new IBISException(e);
	    }
	    finally {
	      close(s);
	    }		
	}

	
	public String getFilePath(Connection con, String platform, String description)
			throws IBISException {
	    PreparedStatement s = null;
	    ResultSet rs = null;
	    try {
	        s = con.prepareStatement(IFileIndexDatabaseSchema.getFilePath);
	        s.setString(1,platform); 
	        s.setString(2, description);
	        rs = s.executeQuery();
	        if (rs.next())
	        	return rs.getString("filePath");
	    }
	    catch (SQLException e) {
	      log.error("FileDatabase.getFilePath " + e.getMessage());
	      throw new IBISException(e);
	    }
	    finally {
	      close(s);
	      close(rs);
	    }			
	    return null;
	}

	
	public List<List<String>> listAllFiles(Connection con) throws IBISException {
		List<List<String>> result = new ArrayList<List<String>>();
	    Statement s = null;
	    ResultSet rs = null;
	    try {
	        s = con.createStatement();
	        rs = s.executeQuery(IFileIndexDatabaseSchema.listAllFiles);
	        List<String>x = null;
	        while (rs.next()) {
	        	x = new ArrayList<String>(3);
	        	x.add(rs.getString("filePath"));
	        	x.add(rs.getString("platform"));
	        	x.add(rs.getString("description"));
	        	x.add(rs.getString("userLocator"));
	        	result.add(x);
	        }
    	    log.debug("FileDatabase.listAllFiles "+result);
	    }
	    catch (SQLException e) {
	      log.error("FileDatabase.listAllFiles " + e.getMessage());
	      throw new IBISException(e);
	    }
	    finally {
	      close(s);
	      close(rs);
	    }					
		return result;
	}

	
	public List<List<String>> listFiles(Connection con, String platform)
			throws IBISException {
		List<List<String>> result = new ArrayList<List<String>>();
	    PreparedStatement s = null;
	    ResultSet rs = null;
	    try {
	        s = con.prepareStatement(IFileIndexDatabaseSchema.listSomeFiles);
	        s.setString(1,platform); 
	        rs = s.executeQuery();
	        List<String>x = null;
	        while (rs.next()) {
	        	x = new ArrayList<String>(3);
	        	x.add(rs.getString("filePath"));
	        	x.add(rs.getString("platform"));
	        	x.add(rs.getString("description"));
	        	x.add(rs.getString("userLocator"));
	        	result.add(x);
	        }
	    }
	    catch (SQLException e) {
	      log.error("FileDatabase.listFiles " + e.getMessage());
	      throw new IBISException(e);
	    }
	    finally {
	      close(s);
	      close(rs);
	    }
	    return result;
	}

	
	public boolean removeFile(Connection con, String path, String platform, String userLocator) throws IBISException {
	    PreparedStatement s = null;
	    boolean result = false;
	    try {
	        s = con.prepareStatement(IFileIndexDatabaseSchema.removeFile);
	        s.setString(1,path); 
	        s.setString(2, platform);
	        s.setString(3,userLocator);
	        result =  s.execute();
	        log.debug("FileDatabase.removeFile "+result+" "+path+" "+platform+" "+userLocator);
	    }
	    catch (SQLException e) {
	      log.error("FileDatabase.removeFile " + e.getMessage());
	      throw new IBISException(e);
	    }
	    finally {
	      close(s);
	    }	
	    return result;
	}
	
	public void exportSchema() throws IBISException {
		log.debug("FileDatabase.exportSchema-");
	    Connection con = null;
	    try {
	      String[] sql = IFileIndexDatabaseSchema.FILE_INDEX_SCHEMA;
	      int len = sql.length;
	      con = getConnection();
	      Statement s = con.createStatement();
	      for (int i = 0; i < len; i++) {
	    	log.debug(sql[i]);
	        s.execute(sql[i]);
	      }
	      s.close();
	      con.close();
	    }
	    catch (SQLException e) {
			log.error("FileDatabase.exportSchema "+e.getMessage());
	      throw new IBISException(e);
	    }
		log.debug("FileDatabase.exportSchema+");
	}

}
