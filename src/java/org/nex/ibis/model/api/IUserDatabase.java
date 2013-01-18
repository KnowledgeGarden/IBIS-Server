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
package org.nex.ibis.model.api;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.nex.ibis.model.Ticket;
import org.nex.ibis.IBISException;
import org.nex.persist.IDatabase;

/**
 * 
 * @author Park
 *
 */
public interface IUserDatabase extends IDatabase {
    /**
     * Authenticate this user. Return <code>null</code> if doesn't authenticate
     * @param connection
     * @param userName
     * @param password
     * @return			Ticket  can return <code>null</code>
     * @throws IBISException
     */
    Ticket authenticate(Connection con, String userName, String password)
    	throws IBISException;

    /**
     * Very dangerous method. Use only after authentication, e.g. 
     * in web services.
     * @param con
     * @param userName
     * @return
     * @throws IBISException
     */
    Ticket getTicket(Connection con, String userName)
    	throws IBISException;
    /**
     * <p>
     * Throws an exception if user already exists. Should
     * use <code>existsUsername</code> first
     * </p>
     * @param con
     * @param userName
     * @param password
     * @param role
     * @throws IBISException
     */
    void insertUser(Connection con,
    				  String userName,
    				  String password,
    				  String role)
		throws IBISException;
    
    /**
     * Used when importing from an XML export
     * @param con
     * @param userName
     * @param password is already encrypted
     * @param grant
     * @throws IBISException
     */
    void insertEncryptedUser(Connection con, String userName, String password, String grant)
    	throws IBISException;
    
    void insertUserData(Connection con, String userName, String propertyType, String propertyValue)
		throws IBISException;
    void updateUserData(Connection con, String userName, String propertyType, String newValue)
		throws IBISException;
    boolean existsUsername(Connection con, String userName) throws IBISException;
    /**
     *
     * @param con
     * @param userName
      * @throws IBISException
     */
    void removeUser(Connection con, String userName) throws IBISException;

	/**
	 * Used when <code>grants</code> or <code>password</code> changes
     * @param connection
	 * @param ticket
	 * @throws IBISException
	 */
//	void updateUser(Connection con, Ticket ticket) throws IBISException;
    void changeUserPassword(Connection con, String userName, String newPassword)
		throws IBISException;
    
 //   void changeUserGrant(Connection con, String userName, String newGrant)
//		throws IBISException;
    
    void addUserRole(Connection con, String userName, String newRole) 
    	throws IBISException;
    
    void removeUserRole(Connection con, String userName, String oldRole)
    	throws IBISException;


    /**
     * <p>Return List of <code>locators</code></p>
     * @param con
     * @return	can return <code>null</code>
     * @throws IBISException
     */
    List<String> listUserLocators(Connection con) throws IBISException;
	/**
	 *
	 * @return
	 * @throws SQLException
	 */
	Connection getConnection() throws SQLException;
	/**
	 * Clear the database
	 * @throws IBISException
	 */
	void exportSchema() throws IBISException;
	/**
	 * Required when shutting down.
	 *
	 */
	void shutdownDriver();
}
