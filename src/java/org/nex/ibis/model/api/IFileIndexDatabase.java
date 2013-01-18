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
import java.util.List;
import java.sql.Connection;

import org.nex.ibis.IBISException;
import org.nex.persist.IDatabase;

/**
 * 
 * @author Park
 *
 */
public interface IFileIndexDatabase extends IDatabase {
	
	void exportSchema() throws IBISException;
	
	/**
	 * List all file descriptions, platforms, and paths
	 * @param con
	 * @return does not return <code>null</code>
	 * @throws IBISException
	 */
	List<List<String>> listAllFiles(Connection con) throws IBISException;
	
	/**
	 * 	List all file descriptions and paths for a given <code>platform</code>
	 * @param con
	 * @param platform (e.g. compendium, debategraph, ...)
	 * @return does not return <code>null</code>
	 * @throws IBISException
	 */
	List<List<String>> listFiles(Connection con, String platform) throws IBISException;
	
	/**
	 * Add a file to the index
	 * @param con 
	 * @param path
	 * @param platform
	 * @param description
	 * @param userLocator
	 * @throws IBISException
	 */
	void addFile(Connection con, String path, String platform, String description, String userLocator) throws IBISException;
	
	/**
	 * Remove a file from the index
	 * @param con 
	 * @param path
	 * @param platform
	 * @param userLocator
	 * @return <code>true</code> if successful
	 * @throws IBISException
	 */
	boolean removeFile(Connection con, String path, String platform, String userLocator) throws IBISException;
	
	/**
	 * Given a <code>platform</code> and <code>description</code>, return its path
	 * @param con 
	 * @param platform
	 * @param description
	 * @return can return <code>null</code>
	 * @throws IBISException
	 */
	String getFilePath(Connection con, String platform, String description) throws IBISException;
}
