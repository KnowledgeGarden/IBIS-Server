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
import org.nex.ibis.IBISException;
import org.nex.ibis.model.conversation.IBISNode;

/**
 * 
 * @author Park
 * <p>Apply Lucene full-text indexing to IBIS conversations</p>
 */
public interface ITextIndexAgent extends IPluginAgent {

	/**
	 * Add the contents of <code>node</code> to the full-text index
	 * @param node
	 * @throws IBISException
	 */
	void indexNode(IBISNode node) throws IBISException;
	
	/**
	 * <p>Return a list of {@link IBISNode} identifiers that satisfy
	 *  the <code>queryString</code></p>
	 * @param queryString
	 * @return
	 * @throws IBISException
	 */
	List<String> query(String queryString) throws IBISException;
	
	/**
	 * Reindex the entire database of IBIS conversations
	 * @throws IBISException
	 */
	void reindex() throws IBISException;
}
