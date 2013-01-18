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
import org.nex.ibis.IBISException;
import org.nex.ibis.model.Ticket;
import org.nex.ibis.model.conversation.IBISNode;
/**
 * 
 * @author Park
 *
 */
public interface IConversationModel {
	public static final String DM_SUFFIX = "_DM";
	public static final String Q_SUFFIX = "_Q";
	public static final String R_SUFFIX = "_R";
	public static final String PRO_SUFFIX = "_PRO";
	public static final String CON_SUFFIX = "_CON";
	public static final String CNC_SUFFIX = "_CNCLD";
	public static final String CTX_SUFFIX = "_CNTXT";
	public static final String REF_SUFFIX = "_REF";
	public static final String NOTE_SUFFIX = "_N";
	public static final String MAP_SUFFIX = "_M";
	public static final String ARG_SUFFIX = "_ARG";

	/**
	 * Return the locator for a new Issue/Question node
	 * @param title must be a readable string
	 * @param body cannot be <code>null</code>, must be at least an empty string ""
	 * @param x  can be <code>null</code> x location in a Compendium graph
	 * @param y  can be <code>null</code> y location in a Compendium graph
	 * @param nodeType  can be <code>null</code> allows for import of other "node type integers"
	 * @param parentNodeLocator we are building AIRs so we need a parentLocator
	 * @param userLocator can be <code>null</code>--if null, use locator from <code>credentials</code>
	 * @param credentials
	 * @param nodeLocator can be <code>null</code> allows to pass in a locator for the new node
	 * @return
	 * @throws IBISException
	 */
	String newQuestion(String title, String body, String x, String y, String nodeType, 
					   String parentNodeLocator, String userLocator, Ticket credentials, 
					   String nodeLocator) throws IBISException;
	/**
	 * Return the locator for a new Response/Answer node
	 * @param title must be a readable string
	 * @param body cannot be <code>null</code>, must be at least an empty string ""
	 * @param x  can be <code>null</code> x location in a Compendium graph
	 * @param y  can be <code>null</code> y location in a Compendium graph
	 * @param nodeType  can be <code>null</code> allows for import of other "node type integers"
	 * @param parentNodeLocator we are building AIRs so we need a parentLocator
	 * @param userLocator can be <code>null</code>--if null, use locator from <code>credentials</code>
	 * @param credentials
	 * @param nodeLocator can be <code>null</code> allows to pass in a locator for the new node
	 * @return
	 * @throws IBISException
	 */
	String newResponse(String title, String body, String x, String y, String nodeType, 
					   String parentNodeLocator, String userLocator, Ticket credentials, 
					   String nodeLocator) throws IBISException;
	/**
	 * Return the locator for a new Pro argument node
	 * @param title must be a readable string
	 * @param body cannot be <code>null</code>, must be at least an empty string ""
	 * @param x  can be <code>null</code> x location in a Compendium graph
	 * @param y  can be <code>null</code> y location in a Compendium graph
	 * @param nodeType  can be <code>null</code> allows for import of other "node type integers"
	 * @param parentNodeLocator we are building AIRs so we need a parentLocator
	 * @param userLocator can be <code>null</code>--if null, use locator from <code>credentials</code>
	 * @param credentials
	 * @param nodeLocator can be <code>null</code> allows to pass in a locator for the new node
	 * @return
	 * @throws IBISException
	 */
	String newPro(String title, String body, String x, String y, String nodeType, 
				  String parentNodeLocator, String userLocator, Ticket credentials, 
				  String nodeLocator) throws IBISException;
	/**
	 * Return the locator for a new Con argument node
	 * @param title must be a readable string
	 * @param body cannot be <code>null</code>, must be at least an empty string ""
	 * @param x  can be <code>null</code> x location in a Compendium graph
	 * @param y  can be <code>null</code> y location in a Compendium graph
	 * @param nodeType  can be <code>null</code> allows for import of other "node type integers"
	 * @param parentNodeLocator we are building AIRs so we need a parentLocator
	 * @param userLocator can be <code>null</code>--if null, use locator from <code>credentials</code>
	 * @param credentials
	 * @param nodeLocator can be <code>null</code> allows to pass in a locator for the new node
	 * @return
	 * @throws IBISException
	 */
	String newCon(String title, String body, String x, String y, String nodeType, 
				  String parentNodeLocator, String userLocator, Ticket credentials, 
				  String nodeLocator) throws IBISException;
	/**
	 * Return the locator for a new conclusion node
	 * @param title must be a readable string
	 * @param body cannot be <code>null</code>, must be at least an empty string ""
	 * @param x  can be <code>null</code> x location in a Compendium graph
	 * @param y  can be <code>null</code> y location in a Compendium graph
	 * @param nodeType  can be <code>null</code> allows for import of other "node type integers"
	 * @param parentNodeLocator we are building AIRs so we need a parentLocator
	 * @param userLocator can be <code>null</code>--if null, use locator from <code>credentials</code>
	 * @param credentials
	 * @param nodeLocator can be <code>null</code> allows to pass in a locator for the new node
	 * @return
	 * @throws IBISException
	 */
//	String newConclusion(String title, String body, String x, String y, String nodeType, 
//						 String parentNodeLocator, String userLocator, Ticket credentials, 
//						 String nodeLocator) throws IBISException;
	/**
	 * Return the locator for a new Reference node
	 * @param title must be a readable string
	 * @param body cannot be <code>null</code>, must be at least an empty string ""
	 * @param x  can be <code>null</code> x location in a Compendium graph
	 * @param y  can be <code>null</code> y location in a Compendium graph
	 * @param nodeType  can be <code>null</code> allows for import of other "node type integers"
	 * @param parentNodeLocator we are building AIRs so we need a parentLocator
	 * @param userLocator can be <code>null</code>--if null, use locator from <code>credentials</code>
	 * @param credentials
	 * @param nodeLocator can be <code>null</code> allows to pass in a locator for the new node
	 * @return
	 * @throws IBISException
	 */
//	String newReference(String title, String body, String x, String y, String nodeType, 
//						String parentNodeLocator, String userLocator, Ticket credentials, 
//						String nodeLocator) throws IBISException;
//	String newContext(String title, String body, String parentNodeLocator, Ticket credentials) throws IBISException;
	/**
	 * Return the locator for a new Note node
	 * @param title must be a readable string
	 * @param body cannot be <code>null</code>, must be at least an empty string ""
	 * @param x  can be <code>null</code> x location in a Compendium graph
	 * @param y  can be <code>null</code> y location in a Compendium graph
	 * @param nodeType  can be <code>null</code> allows for import of other "node type integers"
	 * @param parentNodeLocator we are building AIRs so we need a parentLocator
	 * @param userLocator can be <code>null</code>--if null, use locator from <code>credentials</code>
	 * @param credentials
	 * @param nodeLocator can be <code>null</code> allows to pass in a locator for the new node
	 * @return
	 * @throws IBISException
	 */
//	String newNote(String title, String body, String x, String y, String nodeType, 
//				   String parentNodeLocator, String userLocator, Ticket credentials, 
//				   String nodeLocator) throws IBISException;
	/**
	 * Return the locator for a new Argument (+/-) node
	 * @param title must be a readable string
	 * @param body cannot be <code>null</code>, must be at least an empty string ""
	 * @param x  can be <code>null</code> x location in a Compendium graph
	 * @param y  can be <code>null</code> y location in a Compendium graph
	 * @param nodeType  can be <code>null</code> allows for import of other "node type integers"
	 * @param parentNodeLocator we are building AIRs so we need a parentLocator
	 * @param userLocator can be <code>null</code>--if null, use locator from <code>credentials</code>
	 * @param credentials
	 * @param nodeLocator can be <code>null</code> allows to pass in a locator for the new node
	 * @return
	 * @throws IBISException
	 */
//	String newArgument(String title, String body, String x, String y, String nodeType, 
//			   String parentNodeLocator, String userLocator, Ticket credentials, 
//			   String nodeLocator) throws IBISException;
	/**
	 * Return the locator for a new Map node
	 * @param title must be a readable string
	 * @param body cannot be <code>null</code>, must be at least an empty string ""
	 * @param x  can be <code>null</code> x location in a Compendium graph
	 * @param y  can be <code>null</code> y location in a Compendium graph
	 * @param nodeType  can be <code>null</code> allows for import of other "node type integers"
	 * @param parentNodeLocator we are building AIRs so we need a parentLocator -- this
	 * parenLocator might be the IBIS Legend Root, for an outer map, or another map node in the
	 * case of nested maps.
	 * @param userLocator can be <code>null</code>--if null, use locator from <code>credentials</code>
	 * @param credentials
	 * @param nodeLocator can be <code>null</code> allows to pass in a locator for the new node
	 * @return
	 * @throws IBISException
	 */
	String newMap(String title, String body, String x, String y, String nodeType, 
				  String parentNodeLocator, String userLocator, Ticket credentials, 
				  String nodeLocator) throws IBISException;
	
	/**
	 * Link two IBIS nodes that haven't been wired yet
	 * @param fromNode
	 * @param toNode
	 * @param credentials
	 * @throws IBISException
	 */
	void connectNodes(IBISNode fromNode, IBISNode toNode, Ticket credentials) throws IBISException;
	
    /**
     * <p>Create a new Dialog Map with the given <code>title</code> and return its URL</p>
     * <p><code>mapPath</code> was added to allow for a wiki-like creation of a dialog map
     * by specifying a path, e.g. in a blog entry, and allowing this model to deal with it
     * and give users the option to create a new map with that path->locator</p>
     * @param mapPath  can be ""
     * @param mapTitle
     * @param mapDescription
     * @param credentials
     * @return the locator for the Map object itself
     * @throws TopicSpacesException
     */
	String newIbisMap(String mapPath, String mapTitle, String mapDescription, Ticket credentials) throws IBISException;

}
