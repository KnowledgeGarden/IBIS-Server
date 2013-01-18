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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Park
 * <p>A <em>credential</em> for an authenticated user</p>
 */
public class Ticket {
	   /** */
    public static final String TIMEOUT_INFINITE = "Infinite";
    /** */
    public static final String PRIVILEGE_READ = "read";
    /** */
    public static final String PRIVILEGE_WRITE = "write";
    /** */
    public static final String PRIVILEGE_FREEBUSY = "freebusy";

    private String id;
    /**
     * Owner is same as "user locator"
     */
    private String owner;
    private String timeout;
    private Set<String> roles;
    private Date created;

    private String oldPassword;
    private String newPassword;

   // private BaseModelObject user;
    
    /**
     * Data stored in user database
     */
    private List<Property>userData = new ArrayList<Property>();
    /**
     * Session-sensitive data, not persisted
     */
    private Map<String,Object>volatileData = new HashMap<String,Object>();
    /**
     */
    public Ticket() {
        roles = new HashSet();
    }

    public Map<String,Object> getVolatileData() {
    	return volatileData;
    }
    /**
     * Add a new privilege
     * @param newPrivilege
     */
    public void addRole(String newPrivilege) {
    	roles.add(newPrivilege);
    }
    
    /**
     * Remove a privilege
     * @param oldPrivilege
     */
    public void removeRole(String oldPrivilege) {
    	roles.remove(oldPrivilege);
    }
    
    public List<Property> getUserData() {
    	return this.userData;
    }
    
    public Property findUserData(String key) {
    	if (userData==null) return null;
    	Iterator<Property>itr = userData.iterator();
    	Property p;
    	while (itr.hasNext()) {
    		p = itr.next();
    		if (p.getKey().equals(key))
    			return p;
    	}
    	return null;
    }
    
 
    public void setUserNode(List<Property> n) {
    	this.userData= n;
    }
    
    public void addUserData(String key, String value) {
    	Property p = new Property(this.owner);
    	p.setKey(key);
    	p.addValue(value);
    	this.userData.add(p);
    }
    
    public void removeUserData(String key) {
    	Property p = findUserData(key);
    	if (p != null)
    		userData.remove(p);
    }
    /**
     * GUID of the SubjectMap proxy for this user
     */
    public String getId() {
        return id;
    }

    /**
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     */
    public String getOwner() {
        return owner;
    }

    /**
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     */
    public String getTimeout() {
        return timeout;
    }

    /**
     */
    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    /**
     */
    public void setTimeout(Integer timeout) {
        this.timeout = "Second-" + timeout;
    }

    /**
     */
    public Set<String> getPrivileges() {
        return roles;
    }

    public boolean hasRole(String privilege) {
    	return roles.contains(privilege);
    }
    /**
     */
    public void setRoles(Set<String> privileges) {
        this.roles = privileges;
    }

    /**
     */
    public Date getCreated() {
        return created;
    }

    /**
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    public void setOldPassword(String pwd) {
    	this.oldPassword = pwd;
    }
    public void setNewPassword(String pwd) {
    	this.newPassword = pwd;
    }
    public String getOldPassword() {
    	return this.oldPassword;
    }
    public String getNewPassword() {
    	return this.newPassword;
    }
    /**
     */
    public boolean hasTimedOut() {
        if (timeout == null || timeout.equals(TIMEOUT_INFINITE)) {
            return false;
        }

        int seconds = Integer.parseInt(timeout.substring(7));

        Calendar expiry = Calendar.getInstance();
        expiry.setTime(created);
        expiry.add(Calendar.SECOND, seconds);

        return Calendar.getInstance().after(expiry);
    }

 

}
