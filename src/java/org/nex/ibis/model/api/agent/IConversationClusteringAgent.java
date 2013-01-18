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
package org.nex.ibis.model.api.agent;

import java.util.List;
import java.util.Map;
import org.nex.ibis.IBISException;
import org.nex.ibis.model.conversation.IBISNode;

/**
 * 
 * @author Park
 * <p>Apply the Carrot2 Clustering engine to IBIS conversations</p>
 */
public interface IConversationClusteringAgent extends IPluginAgent {
	
	/**
	 * <p>Return a Map that contains clusters as follows:
	 * <ul>
	 * <li>key = cluster name</li>
	 * <li>value = List of {@link IBISNode} identifiers included in that cluster</li>
	 * </ul></p>
	 * @param nodes
	 * @return
	 * @throws IBISException
	 */
	List<Map<String,List<String>>> clusterNodes(List<IBISNode> nodes) throws IBISException;
}
