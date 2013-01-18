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

import java.sql.*;

import java.util.*;

import org.nex.ibis.IBISException;
import org.nex.ibis.model.Ticket;
import org.nex.ibis.model.api.IUserDatabase;
import org.nex.ibis.model.api.IUserDatabaseSchema;
import org.nex.ibis.model.api.ISecurity;
import org.nex.persist.derby.DerbyDatabase;
import org.apache.log4j.Logger;

/**
 * 
 * @author Park
 *
 */
public class UserDatabase extends DerbyDatabase implements IUserDatabase {
	private Logger log = Logger.getLogger(getClass());
	private static UserDatabase instance;
	private final String DB_STRING = "USERS.xml";
	private String dbName, dbUserName, dbUserPassword;
	private Random ran;
	private Ticket systemUser = null;

	public UserDatabase(String dbName, String userName, String pwd,
			String filePath) throws Exception {
		super(dbName, userName, pwd, filePath);
        ran = new Random();
        instance = this;
	}


	public void addUserRole(Connection con, String userName, String newRole)
			throws IBISException {
	    PreparedStatement s = null;
	    try {
	        s = con.prepareStatement(IUserDatabaseSchema.insertPermission);
	        //we need a random unique id for userLocator
	        String x = getNewUID();
	        System.out.println("INSERTING USER ROLE "+x+" "+userName+" "+newRole);
	        s.setString(1,x); // forced to insert fake locator we don't use anyway
	        s.setString(2, userName);
	        s.setString(3, newRole);
	        s.execute();
	    }
	    catch (SQLException e) {
	      log.error("UserDatabase.addUserPermission " + e.getMessage());
	      throw new IBISException(e);
	    }
	    finally {
	      close(s);
	    }	
	}

	
	public Ticket authenticate(Connection con, String userName, String password)
			throws IBISException {
		if ((userName.indexOf(" ") > -1) ||
				(password.indexOf(" ") > -1))
			throw new IBISException("MalformedException--Suspect:"+userName+" Or: "+password);
		if (!_authenticate(con,userName,password))
			return null;
		Ticket result = null;
	    ResultSet rs = null;
	    PreparedStatement s = null;
	    try {
	      s = con.prepareStatement(IUserDatabaseSchema.getUser2);
	      s.setString(1, userName);
	      rs = s.executeQuery();
	        String priv;
	      while (rs.next()) {
	    	  if (result == null) {
		        result = new Ticket();
		        result.setId(rs.getString("userName"));
		        result.setOwner(userName);
	    	  }
	    	  priv = rs.getString("authority");
	    	  if (priv != null && !priv.equals("") && !priv.equals(" "))
	    		  result.addRole(priv);
	      }
	      System.out.println("UserDatabase.authenticate "+userName+" "+result.getPrivileges());
	      //get the user data
	      if (result != null)
	    	  getUserData(con,result);
	    }
	    catch (SQLException e) {
	      log.error("UserDatabase.authenticate error " + e.getMessage());
	      throw new IBISException(e);
	    }
	    finally {
	    	close(rs);
	      close(s);
	    }
		return result;
	}
	
	void getUserData(Connection con, Ticket t) throws SQLException, IBISException {
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			s = con.prepareStatement(IUserDatabaseSchema.getUserData);
			s.setString(1, t.getOwner());
			rs = s.executeQuery();
			String key,val;
			while (rs.next()) {
		        key = rs.getString("propertyName");
		        val = rs.getString("propertyValue");
		        if (key != null)
		        	t.addUserData(key, val);
			}
		} finally {
	    	close(rs);
			close(s);
		}
	}
	boolean _authenticate(Connection con, String userName, String password) 
			throws IBISException {
		boolean result = false;
		ResultSet rs = null;
		PreparedStatement s = null;
		try {
		s = con.prepareStatement(IUserDatabaseSchema.getUser);
		  s.setString(1, userName);
		  s.setString(2,password);
		  rs = s.executeQuery();
		  result = rs.next();
		} catch (SQLException e) {
			throw new IBISException(e);
		} finally {
			close(rs);
			close(s);
		}
		return result;
	}

	
	public void changeUserPassword(Connection con, String userName,
			String newPassword) throws IBISException {
    	if (newPassword.indexOf(" ") > -1)
    		throw new IBISException("MalformedAuthenticationException Suspect: "+newPassword);
        PreparedStatement s = null;
        try {
        	log.debug("UserDatabase.updateUserPassword "+newPassword+
                    " "+userName);
          s = con.prepareStatement(IUserDatabaseSchema.updatePassword);
          s.setString(1, newPassword);
          s.setString(2, userName);
          s.executeUpdate();
        }
        catch (SQLException e) {
          log.error("UserDatabase.changeUserPassword " +
                    e.getMessage());
          throw new IBISException(e);
        }
        finally {
          close(s);
        }	
    }

	
	public boolean existsUsername(Connection con, String userName)
			throws IBISException {
		boolean result = false;
		Statement s = null;
		ResultSet rs = null;
		try {
			s = con.createStatement();
			rs = s.executeQuery("select userName from TS_USER where userName='"+userName+"'");
			if (rs.next())
				result = true;
		} catch (SQLException e) {
			log.error("UserDatabase.existsUsername error "+e.getMessage());
			throw new IBISException(e);
		} finally {
	    	close(rs);
	    	close(s);
		}
		return result;
	}

	
	public void exportSchema() throws IBISException {
		log.debug("UserDatabase.exportSchema-");
	    Connection con = null;
	    try {
	      String[] sql = IUserDatabaseSchema.USER_SCHEMA;
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
			log.error("UserDatabase.exportSchema "+e.getMessage());

	      throw new IBISException(e);
	    }
		log.debug("UserDatabase.exportSchema+");
	}

	
	public Ticket getTicket(Connection con, String userName)
			throws IBISException {
	    System.out.println("UserDatabase.getTicket 0 "+userName);
	    boolean isSystemUser = userName.equals(ISecurity.SYSTEM_USER);
	    if (isSystemUser && this.systemUser != null)
	    	return this.systemUser;
		Ticket result = null;
	    ResultSet rs = null;
	    PreparedStatement s = null;
	    try {
	      s = con.prepareStatement(IUserDatabaseSchema.getUser2);
	      s.setString(1, userName);
	      rs = s.executeQuery();
	      String key,val,priv;
	      System.out.println("UserDatabase.getTicket 1 "+userName);
	      while (rs.next()) {
		      System.out.println("UserDatabase.getTicket 2 "+userName);
	    	  if (result == null) {
		        result = new Ticket();
		        result.setId(rs.getString("userLocator"));
		        result.setOwner(userName);
	    	  }
	    	  priv = rs.getString("authority");
	    	  if (priv != null)
	    		  result.addRole(priv);
	      }
	      if (result != null)
	    	  getUserData(con,result);
	      System.out.println("UserDatabase.getTicket 3 "+userName);
	    }
	    catch (SQLException e) {
	      log.error("UserDatabase.getTicket error " + e.getMessage());
	      throw new IBISException(e);
	    }
	    finally {
	    	close(rs);
	      close(s);
	    }
	    if (isSystemUser)
	    	this.systemUser = result;
		return result;
	}

	
	public void insertEncryptedUser(Connection con, String userName,
			String password, String grant) throws IBISException {
	    PreparedStatement s = null;
	    try {
	        s = con.prepareStatement(IUserDatabaseSchema.insertUser2);
	        s.setString(1, userName);
	        s.setString(2, userName);
	        s.setString(3, password);
	        s.setString(4, grant);
	        s.execute();
	    }
	    catch (SQLException e) {
	      log.error("UserDatabase.insertUser " + e.getMessage());
	      throw new IBISException(e);
	    }
	    finally {
	      close(s);
	    }
	}

	
	public void insertUser(Connection con, String userName, String password,
			String role) throws IBISException {
	    PreparedStatement s = null;
	    try {
	        s = con.prepareStatement(IUserDatabaseSchema.insertUser);
	        s.setString(1, userName);
	        s.setString(2, userName);
	        s.setString(3, password);
	        s.setString(4, role);
	        s.execute();
	        log.debug("UserDatabase.insertUser "+userName+" "+role);
	    }
	    catch (SQLException e) {
	      log.error("UserDatabase.insertUser " + e.getMessage());
	      throw new IBISException(e);
	    }
	    finally {
	      close(s);
	    }
	}

	
	public void insertUserData(Connection con, String userName,
			String propertyType, String propertyValue) throws IBISException {
		PreparedStatement s = null;
		try {
		    s = con.prepareStatement(IUserDatabaseSchema.insertUserData);
		    s.setString(1, userName);
		    s.setString(2, propertyType);
		    s.setString(3, propertyValue);
		    s.execute();
		}
		catch (SQLException e) {
		  log.error("UserDatabase.insertUserData " + e.getMessage());
		  throw new IBISException(e);
		}
		finally {
		  close(s);
		}
	}

	
	public List<String> listUserLocators(Connection con) throws IBISException {
    	List<String> result = new ArrayList<String>();
    	Statement s = null;
		ResultSet rs = null;
		try {
			s = con.createStatement();
			rs = s.executeQuery(IUserDatabaseSchema.listUsers);
			String loc;
			//each loc might have several rows
			while (rs.next()) {
				loc = rs.getString("userName");
				if (!result.contains(loc))
					result.add(loc);
			}
        }
        catch (SQLException e) {
          log.error("listUserLocators.changeUserGrant " +
                    e.getMessage());
          throw new IBISException(e);
        }
        finally {
	    	close(rs);
          close(s);
        }
    	
    	return result;	}

	
	public void removeUser(Connection con, String userName)
			throws IBISException {
	       PreparedStatement s = null;
	        try {
	          log.debug("UserDatabase.removeUser "+userName);
	          s = con.prepareStatement(IUserDatabaseSchema.removeUser);
	          s.setString(1, userName);
	          s.execute();
	        }
	        catch (SQLException e) {
	          log.error("UserDatabase.removeUser " +
	                    e.getMessage());
	          throw new IBISException(e);
	        }
	        finally {
	          close(s);
	        }
	}

	
	public void removeUserRole(Connection con, String userName, String oldRole)
			throws IBISException {
        PreparedStatement s = null;
        try {
          log.debug("UserDatabase.removeUserPermission "+userName);
          s = con.prepareStatement(IUserDatabaseSchema.removePermission);
          s.setString(1, userName);
          s.setString(2,oldRole);
          s.execute();
        }
        catch (SQLException e) {
          log.error("UserDatabase.removeUserData " +
                    e.getMessage());
          throw new IBISException(e);
        }
        finally {
          close(s);
        }
	}

	
	public void shutdownDriver() {
		super.shutDown();
	}

	
	public void updateUserData(Connection con, String userName,
			String propertyType, String newValue) throws IBISException {
			PreparedStatement s = null;
			try {
				log.debug("UserDatabase.updateUserData "+userName+
			            " "+propertyType);
			  s = con.prepareStatement(IUserDatabaseSchema.updateUserData);
			  s.setString(1, newValue);
			  s.setString(2, userName);
			  s.setString(3,propertyType);
			  s.executeUpdate();
			}
			catch (SQLException e) {
			  log.error("UserDatabase.updateUserData " +
			            e.getMessage());
			  throw new IBISException(e);
			}
			finally {
			  close(s);
			}
	}

	
    private String getNewUID() {
    	return Long.toHexString(System.currentTimeMillis()) + "." +
        Long.toHexString(ran.nextInt()>>>2);  // make positive
    }

}
