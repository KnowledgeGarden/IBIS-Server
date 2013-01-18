/*
 *  Copyright (C) 2007  Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Part of <TopicSpaces>, an open source project.
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

/**
 * 
 * @author park
 * <p>
 * TODO
 * Implement session timeout checking
 * </p>
 */
public class CredentialCache {
	private static CredentialCache instance;
	private Map<String,Ticket> cache = new HashMap<String,Ticket>();

	protected CredentialCache() {
	}

	public static CredentialCache getInstance() {
		if (instance == null) instance = new CredentialCache();
		return instance;
	}
	
	public void putCredential(String userLocator, Ticket credential) {
		synchronized(cache) {
			cache.put(userLocator, credential);
		}
	}
	
	/**
	 * 
	 * @param userLocator
	 * @return can return <code>null</code>
	 */
	public Ticket getCredential(String userLocator) {
		synchronized(cache) {
			return cache.get(userLocator);
		}
	}
	
	public void removeCredential(String userLocator) {
		synchronized(cache) {
			cache.remove(userLocator);
		}
	}
}
