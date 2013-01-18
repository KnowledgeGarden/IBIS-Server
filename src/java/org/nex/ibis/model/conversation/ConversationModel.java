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
package org.nex.ibis.model.conversation;

import org.nex.ibis.model.api.IConversationModel;
import org.nex.ibis.model.Environment;
import org.nex.ibis.model.Ticket;
import org.nex.ibis.IBISException;

/**
 * 
 * @author park
 *
 */
public class ConversationModel implements IConversationModel {
	private Environment env;
	
	public ConversationModel(Environment e) {
		env = e;
		
		env.logDebug("ConversationModel started");
	}
	
	public String newIbisMap(String mapPath, String mapTitle, String mapDescription, Ticket credentials) throws IBISException {
		//TODO
		return null;
	}
	public String newQuestion(String title, String body, String x, String y, String nodeType, 
			   String parentNodeLocator, String userLocator, Ticket credentials, 
			   String nodeLocator) throws IBISException {
		//TODO
		return null;
	}

	public String newResponse(String title, String body, String x, String y, String nodeType, 
			   String parentNodeLocator, String userLocator, Ticket credentials, 
			   String nodeLocator) throws IBISException {
		//TODO
		return null;
	}

	public String newPro(String title, String body, String x, String y, String nodeType, 
			  String parentNodeLocator, String userLocator, Ticket credentials, 
			  String nodeLocator) throws IBISException {
		//TODO
		return null;
	}

	public String newCon(String title, String body, String x, String y, String nodeType, 
			  String parentNodeLocator, String userLocator, Ticket credentials, 
			  String nodeLocator) throws IBISException {
		//TODO
		return null;
	}

	public String newMap(String title, String body, String x, String y, String nodeType, 
			  String parentNodeLocator, String userLocator, Ticket credentials, 
			  String nodeLocator) throws IBISException {
		//TODO
		return null;
	}

	public void connectNodes(IBISNode fromNode, IBISNode toNode, Ticket credentials) throws IBISException {
		//TODO
	}

}
