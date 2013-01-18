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
package org.nex.ibis.model.user;
import java.util.*;
import java.sql.Connection;
import java.sql.SQLException;

import org.nex.ibis.IBISException;
import org.nex.ibis.model.Ticket;
import org.nex.ibis.model.api.IUserDatabase;
import org.nex.ibis.model.api.ISecurity;
import org.nex.ibis.model.user.UserDatabase;
import org.nex.ibis.model.Environment;
import org.nex.ibis.model.CredentialCache;
import org.nex.util.Base64;
import org.nex.ibis.model.api.ISecurity;

/**
 * 
 * @author park
 *
 */
public class UserModel {
	private static UserModel instance;
	private CredentialCache credentialCache;
	private Environment environment;
	private IUserDatabase database;
	/**
     * Pools Connections for each local thread
     * Must be closed when the thread terminates
     */
    private ThreadLocal <Connection>localConnection = new ThreadLocal<Connection>();


	public UserModel(Environment environment) throws IBISException {
		Hashtable properties = environment.getProperties();
		this.environment = environment;
        String dbName = (String)properties.get("UserDatabase");
        String dbUserName = (String)properties.get("MyDatabaseUser");
        String dbUserPassword = (String)properties.get("MyDatabasePwd");
        String path = (String)properties.get("MyDataPath");
        environment.logDebug("UserModel- "+dbName+" "+path);
        try {
        	database = new UserDatabase(dbName,dbUserName,dbUserPassword,path);
        	validateDatabase();
        } catch (Exception e) {
        	environment.logError("UserModel init error "+e.getMessage(),e);
        	throw new IBISException(e);
        }
		credentialCache = CredentialCache.getInstance();
		instance = this;
		environment.logDebug("UserModel started");
	}
	
	/**
	 * Make sure the database is initialized
	 * @throws IBISException
	 */
	private void validateDatabase() throws IBISException {
		Connection con = null;
		try {
			environment.logDebug("UserModel.validateDatabase-");
			con = getLocalConnection();
			environment.logDebug("UserModel.validateDatabase 1 "+con);
			boolean truth = database.existsUsername(con, "test");
			environment.logDebug("UserModel.validateDatabase 2 "+truth);
		} catch (Exception e1) {
			environment.logDebug("UserModel.validateDatabase 3");
			database.exportSchema();
		} 
		environment.logDebug("UserModel.validateDatabase+");
	}
	public static UserModel getInstance() {
		return instance;
	}
	/**
	 * This must also create a proxy in the map
	 * @param userName
	 * @param password
	 * @param initialPermission
	 * @param credentials
	 * @return
	 * @throws IBISException
	 */
	public Ticket createUser(String userName, String password, String initialPermission) 
			throws IBISException {
		try {
			String pwd = new String (Base64.encodeObject(password));
			database.insertUser(getLocalConnection(), userName, pwd, initialPermission);
	//		map.newUser(userName, credentials.getOwner(), "Made by UserManager");
			Ticket result = new Ticket();
			result.setOwner(userName);
			result.addRole(initialPermission);
			credentialCache.putCredential(userName, result);
			return result;
		} catch (Exception e) {
			environment.logError("UserManager.createUser error "+userName+" "+e.getMessage(),e);
			throw new IBISException(e);
		}
	}
	
	public Ticket getTicket(String userLocator) throws IBISException {
		try {
			boolean isSystem = userLocator.equals(ISecurity.SYSTEM_USER);
			Ticket result = null;
			if (isSystem) {
				result = credentialCache.getCredential(userLocator);
				if (result != null)
					return result;
			}
			result = database.getTicket(getLocalConnection(), userLocator);
			if (result != null)
				result.setOwner(userLocator);
//			result.setOwner(userLocator);
//			result.addRole(ISecurity.ADMINISTRATOR_ROLE);
			if (isSystem)
				credentialCache.putCredential(userLocator, result);
			return result;
		} catch (Exception e) {
			environment.logError("UserManager.createApplicationUser error "+userLocator+" "+e.getMessage(),e);
			throw new IBISException(e);
		}	
	}
	
	public Ticket getApplicationTicket(String userLocator) throws IBISException {
		try {
			Ticket result = database.getTicket(getLocalConnection(), userLocator);
			if (result != null)
				result.setOwner(userLocator);
//			result.setOwner(userLocator);
//			result.addRole(ISecurity.ADMINISTRATOR_ROLE);
			return result;
		} catch (Exception e) {
			environment.logError("UserManager.createApplicationUser error "+userLocator+" "+e.getMessage(),e);
			throw new IBISException(e);
		}	
	}
	/**
	 * This must also create a proxy in the map
	 * @param userName
	 * @param password
	 * @return
	 * @throws IBISException
	 */
	public Ticket createApplicationUser(String userName, String password) throws IBISException {
		try {
			String pwd = new String (Base64.encodeObject(password));
			database.insertUser(getLocalConnection(), userName, pwd, ISecurity.ADMINISTRATOR_ROLE);
			Ticket result = new Ticket();
			result.setOwner(userName);
			result.addRole(ISecurity.ADMINISTRATOR_ROLE);
			credentialCache.putCredential(userName, result);
			return result;
		} catch (Exception e) {
			environment.logError("UserManager.createApplicationUser error "+userName+" "+e.getMessage(),e);
			throw new IBISException(e);
		}	
	}
	
	public Ticket createAdminUser(String userName, String password) throws IBISException {
		try {
			String pwd = new String (Base64.encodeObject(password));
			environment.logDebug("UserModel.createAdminUser "+userName+" "+password+" "+pwd);
			database.insertUser(getLocalConnection(), userName, pwd, ISecurity.ADMINISTRATOR_ROLE);
			Ticket result = new Ticket();
			result.setOwner(userName);
			result.addRole(ISecurity.ADMINISTRATOR_ROLE);
			credentialCache.putCredential(userName, result);
			return result;
		} catch (Exception e) {
			environment.logError("UserManager.createAdminUser error "+userName+" "+e.getMessage(),e);
			throw new IBISException(e);
		}
		
	}
	
	/**
	 * First convert password to Base64
	 * @param userName
	 * @param password is plain
	 * @return
	 * @throws IBISException
	 */
	public Ticket authenticate(String userName, String password) throws IBISException {
		try {
			String pwd = new String (Base64.encodeObject(password));
			System.out.println("AUTHENTICATE "+userName+" "+pwd);
			Ticket result = database.authenticate(getLocalConnection(), userName, pwd);
			if (result != null) {
				result.setOwner(userName);
				credentialCache.putCredential(userName, result);
			}
			return result;
		} catch (Exception e) {
			environment.logError("UserManager.authenticate error "+userName+" "+e.getMessage(),e);
			throw new IBISException(e);
		}
	}
	
	/**
	 * 
	 * @param userName
	 * @param password must be Base64
	 * @return
	 * @throws IBISException
	 */
	public Ticket authenticateWeb(String userName, String password) throws IBISException {
		try {
			System.out.println("AUTHENTICATEWEB "+userName+" "+password);
			Ticket result = database.authenticate(getLocalConnection(), userName, password);
			if (result != null) {
				result.setOwner(userName);
				credentialCache.putCredential(userName, result);
			}
			return result;
		} catch (Exception e) {
			environment.logError("UserManager.authenticateWeb error "+userName+" "+e.getMessage(),e);
			throw new IBISException(e);
		}
	}
	public Ticket authenticateApplication(String userName, String password) throws IBISException {
		try {
			String pwd = new String (Base64.encodeObject(password));
			Ticket result = database.authenticate(getLocalConnection(), userName, pwd);
			if (result != null) {
				result.setOwner(userName);
				credentialCache.putCredential(userName, result);
			}
			return result;
		} catch (Exception e) {
			environment.logError("UserManager.authenticate error "+userName+" "+e.getMessage(),e);
			throw new IBISException(e);
		}
	}
	
	/**
	 * 
	 * @param userName
	 * @param newRole
	 * @throws IBISException
	 */
	public void addUserRole(String userName, String newRole) throws IBISException {
		try {
			database.addUserRole(getLocalConnection(), userName,newRole);
		} catch (Exception e) {
			environment.logError("UserManager.addUserRole error "+userName+" "+e.getMessage(),e);
			throw new IBISException(e);
		}
	}
	/**
	 * 
	 * @param userName
	 * @param oldRole
	 * @throws IBISException
	 */
	public void removeUserRole(String userName, String oldRole) throws IBISException {
		try {
			database.removeUserRole(getLocalConnection(), userName,oldRole);
		} catch (Exception e) {
			environment.logError("UserManager.removeUserRole error "+userName+" "+e.getMessage(),e);
			throw new IBISException(e);
		}
	}
	
	public void changeUserPassword(String userName, String newPassword) throws IBISException {
		try {
			String pwd = new String (Base64.encodeObject(newPassword));
			database.changeUserPassword(getLocalConnection(), userName, pwd);
		} catch (Exception e) {
			environment.logError("UserManager.changeUserPassword error "+userName+" "+e.getMessage(),e);
			throw new IBISException(e);
		}
	}
	/**
	 * 
	 * @param userName
	 * @param propertyType
	 * @param value
	 * @throws IBISException
	 */
	public void addUserData(String userName, String propertyType, String value) throws IBISException {
		try {
			database.insertUserData(getLocalConnection(), userName, propertyType, value);
		} catch (Exception e) {
			environment.logError("UserManager.addUserData error "+userName+" "+e.getMessage(),e);
			throw new IBISException(e);
		}
	}
	/**
	 * 
	 * @param userName
	 * @param propertyType
	 * @param value
	 * @throws IBISException
	 */
	public void updateUserData(String userName, String propertyType, String value) throws IBISException {
		try {
			database.updateUserData(getLocalConnection(), userName, propertyType, value);
		} catch (Exception e) {
			environment.logError("UserManager.updateUserData error "+userName+" "+e.getMessage(),e);
			throw new IBISException(e);
		}
	}
	
	public List<String> listUserLocators() throws IBISException {
		try {
			List<String> result = database.listUserLocators(getLocalConnection());
			return result;
		} catch (Exception e) {
			environment.logError("UserManager.listUserLocators error "+e.getMessage(),e);
			throw new IBISException(e);
		}
	}
	
	public void shutDown() {
		database.shutdownDriver();
		try {
			this.closeLocalConnection();
		} catch (Exception e) {}
	}
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
}
