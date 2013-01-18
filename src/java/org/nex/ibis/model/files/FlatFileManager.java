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
import java.util.*;
import java.io.*;

import org.nex.ibis.IBISException;
import org.nex.ibis.model.Environment;
import org.nex.util.TextFileHandler;

/**
 * 
 * @author Park
 * <p>A class to manage flat files.</p>
 * <p>In the directory <code>webapps/Root/data</code> are several
 *    sub directories, each defined in in the config file
 *    <code>core-portal-props.xml</code>. It is the job of this
 *    class to maintain (create) those directories and to maintain
 *    an index (Map) of those files of the form {filePath, docname}.</p>
 *    
 */
public class FlatFileManager {
	private Environment environment;
	private Map<String,List<String>> index = new HashMap<String,List<String>>();
	private String dataPath;
	private TextFileHandler handler;

	public FlatFileManager(Environment e) throws IBISException {
		environment = e;
		Hashtable<String,Object> properties = e.getProperties();
		dataPath = (String)environment.getProperty("MyDataPath");
		environment.logDebug("FlatFileManager dataPath: "+dataPath);
		// TODO setup file paths, etc
		validatePaths();
		handler = new TextFileHandler();
	}
	
	private void validatePaths() throws IBISException {
		List<List> dirNames = (List<List>)environment.getProperty("DocumentTypes");
		int len = dirNames.size();
		environment.logDebug("FlatFileManager.validatePaths- "+len);
		try {
			List<String> dirname;
			String name;
			File f;
			for (int i=0;i<len;i++) {
				dirname = dirNames.get(i);
				name = dirname.get(1);
				f = new File(dataPath+name);
				if (!f.exists())
					f.mkdir();
			}
		} catch (Exception e) {
			environment.logError("FlatFileManager.validatePaths error "+e.getMessage(),e);
			throw new IBISException(e);
		}
	}
	/**
	 * <p>Used to create the index on bootup</p>
	 * <p>Individual <code>put</code> operations should update the index</p>
	 * <p>Should probably run in a thread</p>
	 */
	private void reIndex() {
		//TODO
	}
	
	/**
	 * <p><code>filePath</code> must correspond to one of the types defined
	 * in the config file for the list property <code>DocumentTypes</code>
	 * + fileName, e.g. cm/weather.xml</p>
	 * 
	 * @param filePath
	 * @param doc
	 * @throws IBISException
	 */
	public void put(String filePath, String doc) throws IBISException {
		handler.writeFile(dataPath+filePath, doc);
	}
	
	/**
	 * Return a JSON string
	 * @param filePath
	 * @param docName
	 * @return
	 * @throws IBISException
	 */
	public String getJSON(String filePath, String docName) throws IBISException {
		return null; //TODO
	}
	
	/**
	 * Return an XML string
	 * @param filePath
	 * @return
	 * @throws IBISException
	 */
	public String getXML(String filePath) throws IBISException {
		environment.logDebug("FlatFileManager.getXML "+dataPath+filePath);
		return handler.readFile(dataPath+filePath);
	}
	
	public File getFile(String filePath) throws IBISException {
		File result = new File(dataPath+filePath);
		environment.logDebug("FlatFileManager.getFile "+result);
		return result;
	}
	public void remove(String filePath) throws IBISException {
		File f = new File(dataPath+filePath);
		if (f != null && f.exists())
			f.delete();
	}
	/**
	 * Return a JSON string of the index
	 * @return
	 */
	public String getIndex() {
		return null; //TODO
	}

}
