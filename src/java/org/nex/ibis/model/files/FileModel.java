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
 */package org.nex.ibis.model.files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.io.File;
import org.nex.ibis.model.Environment;
import org.nex.ibis.IBISException;
import org.nex.ibis.model.user.UserModel;
import org.nex.ibis.model.Ticket;
import org.nex.ibis.model.CredentialCache;


//json.jar requires commons.logging.jar, ezmorph.jar, commons.lang.jar
//commons.collections.jar, commons.beanutils.jar, commons.codec.jar
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JSONArray;

/**
 * 
 * @author Park
 *
 */
public class FileModel {
	private Environment environment;
	private FlatFileManager fileManager;
	private FileDatabase database;
	private UserModel userModel;
	private CredentialCache credentialCache;
	/**
     * Pools Connections for each local thread
     * Must be closed when the thread terminates
     */
    private ThreadLocal <Connection>localConnection = new ThreadLocal<Connection>();

	public FileModel(Environment e) throws IBISException {
		environment = e;
		userModel = environment.getUserModel();
		environment.logDebug("FileModel- "+userModel);
		credentialCache = CredentialCache.getInstance();
		fileManager = new FlatFileManager(environment);
        String dbName = (String)e.getProperty("MyDatabase");
        String dbUserName = (String)e.getProperty("MyDatabaseUser");
        String dbUserPassword = (String)e.getProperty("MyDatabasePwd");
        String path = (String)e.getProperty("MyDataPath");
        environment.logDebug("FileModel-2 "+path);
		try {
			environment.logDebug("FileModel-3");
			database = new FileDatabase(dbName,dbUserName,dbUserPassword,path);
			environment.logDebug("FileModel-4 "+database);
			validateDatabase();
		} catch (Exception x) {
        	environment.logError("FileModel init error "+x.getMessage(),x);
        	database.exportSchema();
        	//throw new IBISException(x);
		}
		environment.logDebug("FileModel started");
	}
	
	/**
	 * Make sure the database is initialized
	 * @throws IBISException
	 */
	private void validateDatabase() throws IBISException {
		Connection con = null;
		try {
			environment.logDebug("FileModel.validateDatabase-");
			con = getLocalConnection();
			environment.logDebug("FileModel.validateDatabase 1 "+con);
			String px = database.getFilePath(con, "foo","bar");
			environment.logDebug("FileModel.validateDatabase 2 "+px);
		} catch (Exception e1) {
			environment.logDebug("FileModel.validateDatabase 3 "+database);
			database.exportSchema();
		} 
		environment.logDebug("FileModel.validateDatabase+");
	}

	public void shutDown() {
		database.shutDown();
		try {
			this.closeLocalConnection();
		} catch (Exception e) {}
	}
	
	public void addFile(String fileName, String platform, String description, String cargo, String userLocator)
			throws IBISException {
		environment.logDebug("FileModel.addFile "+fileName+" "+platform+" "+description+" "+cargo.length());
		String x = fileName.toLowerCase();
		if (x.endsWith(".xml") || x.endsWith(".json") || x.endsWith(".rdf")) {
			fileManager.put(platform+"/"+fileName, cargo);
			database.addFile(getLocalConnection(), fileName, platform, description, userLocator);
		} else {
			environment.logDebug("FileModel.addFile rejected: "+fileName);
		}
	}
	
	public List<List<String>> listAllFiles() throws IBISException {
		return database.listAllFiles(getLocalConnection());
	}
	
	/**
	 * <p>Returns a JSON String that is an array, e.g.
	 * [["ggw-3.xml","cm","GrainsGoneWild"],["TrivialMap.xml","cm","TrivialMap"]]</p>
	 * @return
	 * @throws IBISException
	 */
	public String listAllFilesAsJSON() throws IBISException {
		List<List<String>> f = listAllFiles();
		JSONArray jo = (JSONArray)JSONSerializer.toJSON(f);
		return jo.toString();
	}
	public List<List<String>> listSomeFiles(String platform) throws IBISException {
		return database.listFiles(getLocalConnection(), platform);
	}
	
	public boolean removeFile(String platform, String fileName, String userName) throws IBISException {
		fileManager.remove(platform+"/"+fileName);
		return database.removeFile(getLocalConnection(), fileName,platform, userName);
	}
	
	public String getFileOnDescription(String platform, String description) throws IBISException {
		String fileName = database.getFilePath(getLocalConnection(), platform, description);
		if (fileName != null)
			return fileManager.getXML(platform+"/"+fileName);
		return null;
	}
	public String getFile(String platform, String fileName) throws IBISException {
		if (fileName != null)
			return fileManager.getXML(platform+"/"+fileName);
		return null;
	}
	
	public File getBinaryFile(String name) throws IBISException {
		File result = fileManager.getFile(name);
		return result;
	}
/**
 * This does not work
 	public String getFileAsJSON(String platform, String fileName) throws IBISException {
		String result = getFile(platform,fileName);
		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON( result );
		result = jsonObject.toString();
		return result;
	}
*/
  	private Connection getLocalConnection() throws IBISException {
  	   synchronized(localConnection) {
            Connection con = this.localConnection.get();
            if (con == null) {
            	try {
            		con = database.getConnection();
            		localConnection.set(con);
            	} catch (Exception e) {
            		environment.logError("FileModel.getLocalConnection error "+e.getMessage(),e);
            		throw new IBISException(e);
            	}
            }
            return con;
  	   }
    }

     public void closeLocalConnection() throws IBISException {
         try {
      	   synchronized(localConnection) {
  	         Connection con = this.localConnection.get();
  	         if (con != null)
  	           con.close();
  	         localConnection.set(null);
      	   }
         } catch (SQLException e) {
           throw new IBISException(e);
         }
     }
     
     /**
      * Plan password--not Base64
      * @param login
      * @param password
      * @return
      * @throws IBISException
      */
     public Ticket authenticate(String login, String password) throws IBISException {
    	 System.out.println("Authenticating "+login+" "+password);
  
    	 Ticket user = userModel.authenticate(login, password);
    	 if (user != null)
    		 credentialCache.putCredential(login, user);
    	 return user;
     }
     
     /**
      * Base64 password
      * @param login
      * @param password
      * @return
      * @throws IBISException
      */
     public Ticket authenticate64(String login, String password) throws IBISException {
    	 Ticket user = userModel.authenticateWeb(login, password);
    	 if (user != null)
    		 credentialCache.putCredential(login, user);
    	 return user;
     }

}
