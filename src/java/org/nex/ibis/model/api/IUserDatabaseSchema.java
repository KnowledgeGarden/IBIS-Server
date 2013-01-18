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

/**
 * 
 * @author Park
 * Adapted from TopicSpaces
 */
public interface IUserDatabaseSchema {

	/**
	 * When used with Derby, "drop if exists" is not valid.
	 * User must delete the tables first
	 */
	public static final String [] USER_SCHEMA = { 
		/* "drop table if exists TS_USER;", */
        "create table TS_USER ("+
        	//userLocator ensures uniqueness
        	"userLocator varchar(255) not null,"+
        	//userName allows multiple rows for authority values
        	"userName varchar(255),"+
        	"userPassword varchar(128) default '',"+
        	"authority varchar(128) not null default '"+ISecurity.USER_ROLE+"', "+
        	"unique(userLocator))",
        	"create index MyUserLocatorIndex on TS_USER(userLocator)",

        	
      /*  "drop table if exists TS_USER_DATA;", */
        "create table TS_USER_DATA ("+
        	"userName varchar(128) not null,"+
        	"propertyName varchar(64) not null,"+
        	"propertyValue varchar(255) not null)",
        	"create index MyUserDataIndex on TS_USER_DATA(userName)"
    };
	/**
	 * NOTE: we are not encrypting passwords here; they must be encrypted externally
	 */
	  public final String getUser =
	      "select * from TS_USER where userName=? and userPassword=?";
	  public final String getUser2 =
	      "select * from TS_USER where userName=?";
	  public final String insertUser =
	      "insert into TS_USER (userLocator,userName,userPassword,authority) " +
	      "values(?,?,?,?)";
	  public final String insertUser2 =
	      "insert into TS_USER (userLocator,userName,userPassword,authority) " +
	      "values(?,?,?,?)";
	  
	  public final String insertPermission =
	      "insert into TS_USER (userLocator,userName,authority) " +
	      "values(?,?,?)";
	  public final String removePermission = 
		  "update TS_USER set authority='' where userName=? and authority=?";
	  public final String updatePassword =
	      "update TS_USER set userPassword=? where userName=?";
	  public final String updateGrant =
	      "update TS_USER set authority=? where userName=?";
	  public final String removeUser =
	      "delete from TS_USER where userName=?";
	  public final String insertUserData =
	      "insert into TS_USER_DATA (userName,propertyName,propertyValue) " +
	      "values(?,?,?)";
	  public final String removeUserData =
	      "delete from TS_USER_Data where userName=? and propertyName=?";
	  public final String updateUserData =
	      "update TS_USER_DATA set propertyValue=? where userName=? and propertyName=?";
	  public final String getUserData =
	      "select * from TS_USER_DATA where userName=?";
	  public final String listUsers = "select userName from TS_USER";
}
