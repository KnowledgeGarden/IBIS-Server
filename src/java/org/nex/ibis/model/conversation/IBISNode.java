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

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Park
 *
 */
public class IBISNode implements Comparable {
	public String noderef, x,y,/*parentNodeLocator,*/type,author,title,details,created,lastUpdate,viewid;
	public List<String>children = new ArrayList<String>();
	public List<String>parents = new ArrayList<String>();
	
	public IBISNode(String loc) {
		noderef = loc;
	}
	
	public IBISNode clone() {
		IBISNode result = new IBISNode(noderef);
		result.viewid = viewid;
		result.author = author;
		result.x = x;
		result.y = y;
		result.type = type;
		result.title = title;
		result.details = details;
		result.parents = copyList(parents);
		result.children = copyList(children);
		return result;
	}
	
	List<String> copyList(List<String> in) {
		int len = in.size();
		List<String> result = new ArrayList<String>(len);
		for (int i=0;i<len;i++)
			result.add(in.get(i));
		return result;
	}
	public String toString() {
		return "NODE: "+noderef+" "+viewid+" "+type+" "+parents+" "+title+" "+children;
	}
	/**
	 * Children are <code>noderef</code>s that link to this node
	 * @param noderef
	 */
	public void addChild(String noderef) {
		children.add(noderef);
	}
	/**
	 * Parents are <code>noderef</code>s that this node links to
	 * @param noderef
	 */
	public void addParent(String noderef) {
		parents.add(noderef);
	}
	public boolean hasChildren() {
		return children.size() > 0;
	}
	public boolean hasParent() {
		return parents.size() > 0;
	}
	public int compareTo(Object o) {
		if (((IBISNode)o).noderef.equals(noderef))
			return 0;
		return -1;
	}
}
