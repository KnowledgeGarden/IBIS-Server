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
package org.nex.ibis.model;
import java.util.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.nex.config.ConfigPullParser;
import org.nex.ibis.servlet.FileServlet;
import org.nex.ibis.model.user.UserModel;
import org.nex.ibis.model.files.FileModel;
import org.nex.ibis.IBISException;

/**
 * 
 * @author Park
 *
 */
public class Environment {
	protected final Logger log = Logger.getLogger(Environment.class);
	private static Environment instance=null;
	private Hashtable properties;
	private UserModel userModel;
	private FileModel fileModel;
	
	public Environment(String configFilePath) throws IBISException {
		ConfigPullParser p = new ConfigPullParser(configFilePath);
		properties = p.getProperties();
		PropertyConfigurator.configure((String)properties.get("MyLogPath"));
		instance = this;
		logDebug("Environment starting");
		userModel = new UserModel(this);
		logDebug("Environment 1");
		fileModel = new FileModel(this);
		logDebug("Environment 2");
		validateDefaultAdmin();
		logDebug("Environment started");
	}

	/**
	 * Can return <code>null</code>
	 * @return
	 */
	public static Environment getInstance() {
		return instance;
	}
	
	public Hashtable getProperties() {
		return properties;
	}
		
	/**
	 * 
	 * @param key
	 * @return can return <code>null</code>
	 */
	public Object getProperty(String key) {
		return properties.get(key);
	}
	
	public UserModel getUserModel() {
		return this.userModel;
	}
	
	public FileModel getFileModel() {
		return this.fileModel;
	}
	/**
	 * Utility logging
	 * @param msg
	 */
	public void logDebug(String msg) {
		log.debug(msg);
	}
	
	/**
	 * Utility logging
	 * @param msg
	 * @param e
	 */
	public void logError(String msg, Exception e) {
		if (e == null)
			log.error(msg);
		else
			log.error(msg,e);
	}
	
	/**
	 * Called when the Servlets shut down
	 */
	public void shutDown() {
		logDebug("Environment shutting down.");
		userModel.shutDown();
		fileModel.shutDown();
	}
	
	/**
	 * <p>Make sure we have a default administrator available</p>
	 * @throws IBISException
	 */
	private void validateDefaultAdmin() throws IBISException {
		String defaultAdmin = (String)getProperty("DefaultAdminName");
		Ticket t = userModel.getTicket(defaultAdmin);
		if (t == null) {
			String defaultPwd = (String)getProperty("DefaultAdminPwd");
			userModel.createAdminUser(defaultAdmin, defaultPwd);
			logDebug("Environment.validateDefaultAdmin created admin");
		}
	}
}
