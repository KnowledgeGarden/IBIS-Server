/*
 *  Copyright (C) 2008  Jack Park,
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
package org.nex.ibis.model.conversation;
import java.io.*;
import java.util.*;
/** in xpp.jar */
import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import org.nex.ibis.IBISException;
import org.nex.ibis.model.Environment;
//import org.nex.ts.core.smp.api.IAddressableInformationResource;
import org.nex.ibis.model.Ticket;
import org.nex.ibis.model.api.IConversationModel;
import org.nex.ibis.model.api.ICompendiumConstants;
//import org.nex.ts.server.model.dialog.api.IIbisOntology;;

/**
 * 
 * @author jackpark
 *
 */
public class CommonImportPullParser extends Thread {
	private Environment smp;
	private IConversationModel model;
	private Ticket credentials;
	private IBISNode theMap;
	private InputStream inStream;
//	private HashMap <String, SubjectProxy> maps = new HashMap<String, SubjectProxy>();
    private final String VIEWREF = "viewref";
    private final String NODEREF = "noderef";
    private final String XPOS = "XPosition";
    private final String YPOS = "YPosition";
    private final String CREATE = "created";
    private final String MODIFY = "lastModified";
    private final String ID = "id";
    private final String AUTHOR = "author";
    private final String LABEL = "label";
    private final String NODEID = "nodeid";
    private final String TYPE = "type";
    private final String FROM = "from";
    private final String TO = "to";

    private HashMap<String, IBISNode> nodes = new HashMap<String, IBISNode>();
    
    private List<IBISNode>displayed = new ArrayList<IBISNode>();
    private String rootId;
    /**
     * <p>In a properly exported map, there is IBISNode with the label = "Home Window"
     * which has noderef = rootid. When that view is imported, where viewid=rootid,
     * that node is the rootNode--the Map of this issue map. It's noderef is
     * then the viewref for its children nodes.</p>
     */
    private String rootNodeId;
    
    /**
     * Note: this is not thread-safe: must create individual versions when importing
     * @param s
     */
	public CommonImportPullParser(Environment s, IConversationModel m) 
			throws IBISException {
		smp = s;
		model = m;
	}

	public void parse(String xml, Ticket creds) throws Exception {
		String clean = cleanString(xml);
		ByteArrayInputStream bas = new ByteArrayInputStream(clean.getBytes());
		BufferedInputStream bis = new BufferedInputStream(bas);
		parse(bis,creds);
	}
	public void parse(InputStream is, Ticket creds) {
		this.credentials = creds;
		this.inStream = is;
		start();
	}
	
	public void run() {
	      try {
	         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	         factory.setNamespaceAware(false);
	         XmlPullParser xpp = factory.newPullParser();

	         BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
	         xpp.setInput(in);
	         String tagName = null;
	         IBISNode theNode = null;
	         String text = null;
	         String x=null,y=null,type=null;
	         HashMap attrs = null;
	         String viewref = null, noderef = null,from = null, to=null,created=null,lastmodified=null;
	         boolean isView = false, isNode = false, isLink = false, isLinkView = false;
	         int eventType = xpp.getEventType();
	         boolean isStop = false;
	         while (!(isStop || eventType == XmlPullParser.END_DOCUMENT)) {
	        	 tagName = xpp.getName();
	            if(eventType == XmlPullParser.START_DOCUMENT) {
	                System.out.println("Start document");
	            } else if(eventType == XmlPullParser.END_DOCUMENT) {
	                System.out.println("End document");
	            } else if(eventType == XmlPullParser.START_TAG) {
	            	smp.logDebug("Start tag "+tagName);
	                 attrs = getAttributes(xpp);
	                 if (tagName.equals("model")) {
	                     //there is just one node, a Map, that exists
	                     //inside rootView. We don't care about this
	                     //rootId; we only care about the one that came in,
	                     //the canvas into which this map is imported.
	                     rootId = getAttribute("rootview",attrs);
	                     theNode = new IBISNode(rootId);
	                     nodes.put(rootId, theNode);
	                     //by setting mapTitle=null, we can setName later
	                  //   String locx = model.newIbisMap("", null, credentials);
	                  //   theMap = proxySession.getProxyByLocator(locx, credentials);
	                 } else if (tagName.equals("views")) {
	                     isView = true;
	                 } else if (tagName.equals("view")) {
	                	 //<view viewref="13010765211207597759600" noderef="13010765211207678551684" 
	                	 //     XPosition="262" YPosition="335" created="1207678551684" lastModified="1207678551684">
	                	 //A VIEW is an instance of a Map, as in nested maps
	                	 //There is an "outer VIEW" that is the main Map
	                	 //A VIEW is identified by VIEWREF and is a container for NODE instances
	                	 //The "outer VIEW" has the same VIEWREF as rootId
	                	 //A NODE in a VIEW is identified by NODEREF
	                	 //A NODE also gets XPOS and YPOS (or not)
	                	 //WHEN a noderef--defined in one viewref, is also a viewref, it's a mapview
	                	 // ROOT viewref is the 'outer' node in which a map exists--also a noderef
	                	 //  with no links
	                     viewref = getAttribute(this.VIEWREF, attrs);
	                     noderef = getAttribute(this.NODEREF, attrs);
	                    // if (!noderef.endsWith(IIBIS.MAP_POSTFIX))
	                    //	 noderef += IIBIS.MAP_POSTFIX;
	                     if (viewref.equals(rootId)) {
	                    	 noderef = noderef+IConversationModel.DM_SUFFIX;
	                    	 this.rootNodeId = noderef;
	                     }
	                     x = getAttribute(this.XPOS, attrs);
	                     y = getAttribute(this.YPOS, attrs);
	                     theNode = nodes.get(noderef);
	                     if (theNode == null) {
	                         theNode = new IBISNode(noderef);
	                         nodes.put(noderef,theNode);
	                         smp.logDebug("VIEW noderef "+noderef);
	                     } 
                         theNode.viewid = viewref;
                         theNode.x = x;
                         theNode.y = y;
	                 } else if (tagName.equals("nodes")) {
	                     isNode = true;
	                 } else if (tagName.equals("node")) {
	                	 //<node id="13010765211207597759600" type="2" extendedtype="" originalid="" 
	                	 //author="Jack Park" created="1207597759580" lastModified="1207597759580" label="Home Window" state="-1">
	                     noderef = getAttribute(this.ID,attrs);
	                     if (this.rootNodeId.startsWith(noderef))
	                    	 noderef = rootNodeId;
	                    // System.out.println("NODE id "+noderef);
	                     type = getAttribute(this.TYPE,attrs);
	                    // System.out.println("NODE type "+type);
	                     created = getAttribute("created",attrs);
	                    // System.out.println("NODE created "+created);
	                     lastmodified = getAttribute("lastModified",attrs);
	                    // System.out.println("NODE lastmodified "+lastmodified);
	                     String author = credentials.getOwner(); //this.authorName2PSI(getAttribute(this.AUTHOR,attrs));
	                    // System.out.println("NODE author "+author);

	                     theNode = nodes.get(noderef);
	                     smp.logDebug("NODE noderef "+noderef+" "+theNode);
	                     //NOTE: should not be null
                         
	                     theNode.author = author;
	                     theNode.created = created;
	                     theNode.lastUpdate = lastmodified;
	                     theNode.title = getAttribute(this.LABEL,attrs);
	                     theNode.type = type;
	                     if (noderef.equals(rootId)) {
	                    	 if (!theNode.title.equals("Home Window"))
	                    		 throw new IBISException("Compendium not exported properly--missing \"Home Window\".");
	                     }
	                     smp.logDebug("NODE noderef 2 "+noderef+" "+rootId);
	                 } else if (tagName.equals("details")) {
	                 } else if (tagName.equals("page")) {
	                	 //TODO we are ignoring pages for now
	                 } else if (tagName.equals("source")) {
	                 } else if (tagName.equals("image")) {
	                 } else if (tagName.equals("background")) {
	                 } else if (tagName.equals("coderefs")) {
	                 } else if (tagName.equals("shortcutrefs")) {
	                 } else if (tagName.equals("shortcutref")) {
	                 } else if (tagName.equals("codes")) {
	                 } else if (tagName.equals("links")) {
	                     isLink = true;
	                 } else if (tagName.equals("link")) {
	                	 //<link id="13010765211225144365225" created="1225144365225" lastModified="1225144365225" 
	                	 //author="Jack Park" type="45" originalid="" 
	                	 //from="13010765211225144354569" to="13010765211225144336854" label="" arrow="1">
	                     from = getAttribute(this.FROM,attrs);
	                     to = getAttribute(this.TO,attrs);
	                	 theNode = nodes.get(to);
	                	 theNode.addChild(from);
	                	 theNode = nodes.get(from);
	                	 theNode.addParent(to);
	                	 smp.logDebug("Edge from "+from+" to "+to);
	                 } else if (tagName.equals("linkviews")) {
	                 } else if (tagName.equals("linkview")) {
	                     isLinkView = true;
	                 } else // must trap for tags we don't know about
	                     throw new IOException("Missing tag "+tagName);
	             } else if(eventType == XmlPullParser.END_TAG) {
	            	 smp.logDebug("End tag "+tagName+" // "+text);
	                 if (tagName.equals("model")) {
	 	            	importMap();
	 	            	smp.logDebug("Game over");
	                 } else if (tagName.equals("views")) {
	                     isView = false;
	                 } else if (tagName.equals("view")) {
	                 } else if (tagName.equals("nodes")) {
	                 } else if (tagName.equals("node")) {
	                     isNode = false;
	                 } else if (tagName.equals("details")) {
	                	 //details looks like this:
	                	 //	<details>
	     				 //	 <page nodeid="13010765211228424057989" author="Jack Park" created="1228424057989" lastModified="1228424061614" pageno="1">some text</page>
	                 } else if (tagName.equals("page")) {
	                	 smp.logDebug("DETAILS 1 "+theNode);
	                     theNode.details = text;
	                 } else if (tagName.equals("source")) {
	                 } else if (tagName.equals("image")) {
	                 } else if (tagName.equals("background")) {
	                 } else if (tagName.equals("coderefs")) {
	                 } else if (tagName.equals("shortcutrefs")) {
	                 } else if (tagName.equals("shortcutref")) {
	                 } else if (tagName.equals("codes")) {
	                 } else if (tagName.equals("links")) {
	                     isLink = false;
	                 } else if (tagName.equals("link")) {	                	 
	                 } else if (tagName.equals("linkviews")) {
	                 } else if (tagName.equals("linkview")) {
	                 }
	            } else if(eventType == XmlPullParser.TEXT) {
	                text = xpp.getText().trim();
	            } else if(eventType == XmlPullParser.CDSECT) {
	                text = xpp.getText().trim();
	            }
	            eventType = xpp.next();
	          }
	        } catch (Exception e) {
	        	smp.logError("CommonImportPullParser error "+e.getMessage(),e);
	        	System.out.println("CommonImportPullParser parser failed "+e.getMessage());
	        } 
		}
		
	/**
	 * This starts the whole graph building activity
	 * @throws IBISException
	 */
	void importMap() throws IBISException {
			smp.logDebug("CommonImportPullParser.importMap- "+rootNodeId);
			IBISNode theNode = nodes.get(rootNodeId);
			IBISNode clone = theNode.clone();
       	 	model.newIbisMap(clone.noderef, clone.title,"Imported Compendium Map", credentials);
			smp.logDebug("CommonImportPullParser.importMap 1 "+theNode+" "+nodes);
			importNode(theNode,true);
			smp.logDebug("CommonImportPullParser.importMap+");
	}
	/**
	 * Recursive descent along the IBIS tree
	 * @param node
	 * @param isRoot
	 * @throws IBISException
	 */
	void importNode(IBISNode node, boolean isRoot) throws IBISException {
		//don't do a node again
			if (displayed.contains(node)) return;
			displayed.add(node);
			smp.logDebug("CommonImportPullParser.importNode- "+node+" "+displayed);
			int which = Integer.parseInt(node.type);
			if (which > 10) which -= 10;
			if (!isRoot) {
				IBISNode clone = node.clone();
				smp.logDebug("CommonImportPullParser.importNode 1 "+clone);
				smp.logDebug("CommonImportPullParser.importNode 2 "+which);
				//first, import this node
	   	 		updateNoderef(clone);
	   	 		smp.logDebug("CommonImportPullParser.importNode 3 "+clone);
	   	 		updateParentNodeLocator(clone);
	   	 		smp.logDebug("CommonImportPullParser.importNode 4 "+clone);
	   	 		createNode(clone);		
			}
   	 		int len = 0;
			//see if it is a map
			if (which == ICompendiumConstants.MAPVIEW) {
	   	 		System.out.println("CommonImportPullParser.importNode 5 ismap ");
				List<IBISNode>roots = findRootNodes(node.noderef);
				len = roots.size();
				for (int i=0;i<len;i++)
					importNode(roots.get(i), false);
			}
			//deal with children
			len = node.children.size();
			IBISNode n;
			for (int i=0;i<len;i++) {
				n = nodes.get(node.children.get(i));
				importNode(n,false);
			}
	}

		/**
		 * <p>Find the first node in this <code>node</code>'s viewid</p>
		 * <p>This is appropriate to <em>Map</em> nodes, each of which is
		 * a container of other nodes, one node serving as the root of a tree</p>
		 * <p>Not entirely correct. A <em>Map</em> may happen to have several 
		 * tree roots. This is a complex issue: How to identify nodes that
		 * happen to have no "to" links, but which have 0 or more "from" links?</p>
		 * <p>In the present algorithm, we say that a node's "to" link points to a
		 * <em>parent</em> and it's "from" links are <em>children</em></p>
		 * <p>If any node has no parents or children, it is a singleton root node in that map.</p>
		 * <p>All rootNodes in a given <em>Map</em>automatically get that map as their parent node</p>
		 * 
		 * @param noderef for a <em>Map</em> node, which is the viewid for its contained nodes
		 * @return does not return <code>null</code>
		 */
		List<IBISNode> findRootNodes(String noderef) {
			smp.logDebug("CompendiumImport.findRootNodes- "+noderef);
			String nref = noderef;
			if (nref.endsWith(IConversationModel.DM_SUFFIX))
				nref = nref.substring(0,(nref.length()-IConversationModel.DM_SUFFIX.length()));
			List<IBISNode> result = new ArrayList<IBISNode>();
			Iterator itr = nodes.keySet().iterator();
			IBISNode n;
			//A node that has no parents means that node links to nobody
			//  this might be a root, but perhaps not.
			//
			List<IBISNode>nodesWithoutParents = new ArrayList<IBISNode>();
			List<IBISNode>nodesWithParents = new ArrayList<IBISNode>();
			//A node that has no children means that nobody links to it
			//  this might be a root if it links to other nodes, but it could be a leaf
			List<IBISNode>nodesWithoutChildren = new ArrayList<IBISNode>();
			while (itr.hasNext()) {
				n = nodes.get(itr.next());
				smp.logDebug("CompendiumImport.findRootNode 1 "+n.noderef+" "+n.viewid+" "+n.parents+" "+n.children);
				//a root node has no parentNodeLocator but it will be a TO to 1 or more other nodes
				//in this view
				if (n.viewid != null && n.viewid.equals(nref)) {
					
					if (!n.hasParent())
						nodesWithoutParents.add(n);
					else
						nodesWithParents.add(n);
					if (!n.hasChildren())
						nodesWithoutChildren.add(n);
				}
			}
			smp.logDebug("CompendiumImport.findRootNode noParents 2 "+nodesWithoutParents);
			smp.logDebug("CompendiumImport.findRootNode parents 3 "+nodesWithParents);
			smp.logDebug("CompendiumImport.findRootNode noChildren 4 "+nodesWithoutChildren);
			Iterator<IBISNode>xtr = null;
			//Trim obvious singletons
			if (nodesWithoutChildren.size() > 0) {
				xtr = nodesWithoutParents.iterator();
				while (itr.hasNext()) {
					n = xtr.next();
					if (!n.hasChildren()) {
						result.add(n);
						nodesWithoutChildren.remove(n);
						nodesWithParents.remove(n);
					}
				}
				xtr = result.iterator();
				while (xtr.hasNext())
					nodesWithoutParents.remove(xtr.next());
			}
			//If there were singletons in the lists, they're gone now.
			//At this point, a root will have no parent, must have a child
			//TODO now sort out which one is first.
			xtr = nodesWithoutParents.iterator();
			//List<IBISNode>temp = new ArrayList<IBISNode>();
			//collect all that have root-like characteristics (not leaf nodes)
			while (xtr.hasNext()) {
				n = xtr.next();
				if (n.hasChildren()) {
					n.addParent(noderef);
					result.add(n);
				}
			}
			smp.logDebug("CompendiumImport.findRootNode+ "+result);
			return result;
		}
		
		/**
		 * Create an IBIS node
		 * @param node
		 * @throws IBISException
		 * @TODO MUST FINISH EACH LINE FOR MULTIPLE PARENTS
		 */
		void createNode(IBISNode node) throws IBISException {
			String result = null;
			smp.logDebug("CommonImportPullParser.createNode "+node.noderef+" "+node.parents+" "+node.type);
			int which = Integer.parseInt(node.type);
			if (which > 10) which -= 10;
			String parentLocator = null;
			if (node.hasParent())
				parentLocator = node.parents.get(0);
			if (which == ICompendiumConstants.ISSUE)
				result = model.newQuestion(node.title, node.details, node.x, node.y, node.type, parentLocator, node.author, credentials, node.noderef);
			if (which == ICompendiumConstants.POSITION)
				result = model.newResponse(node.title, node.details, node.x, node.y, node.type, parentLocator, node.author, credentials, node.noderef);
//			if (which == ICompendiumConstants.ARGUMENT)
//				result = model.newArgument(node.title, node.details, node.x, node.y, node.type, parentLocator, node.author, credentials, node.noderef);
			if (which == ICompendiumConstants.CON)
				result = model.newCon(node.title, node.details, node.x, node.y, node.type, parentLocator, node.author, credentials, node.noderef);
			if (which == ICompendiumConstants.PRO)
				result = model.newPro(node.title, node.details, node.x, node.y, node.type, parentLocator, node.author, credentials, node.noderef);
			if (which == ICompendiumConstants.MAPVIEW)
				result = model.newMap(node.title, node.details, node.x, node.y, node.type, parentLocator, 
									    node.author, credentials, node.noderef);
//			if (which == ICompendiumConstants.DECISION)
//				result = model.newConclusion(node.title, node.details, node.x, node.y, node.type, parentLocator, node.author, credentials, node.noderef);
//			if (which == ICompendiumConstants.NOTE)
//				result = model.newNote(node.title, node.details, node.x, node.y, node.type, parentLocator, node.author, credentials, node.noderef);
//			if (which == ICompendiumConstants.REFERENCE)
//				result = model.newReference(node.title, node.details, node.x, node.y, node.type, parentLocator, node.author, credentials, node.noderef);
		}
		
		/**
		 * <p>When we create a node, its locator gets a <em>postfix</em> string
		 * for node typing. Here, we are starting from raw locators.</p>
		 * @param node
		 */
		void updateParentNodeLocator(IBISNode node) throws IBISException {
			if (!node.hasParent()) return;
			smp.logDebug("CommonImportPullParser.updateParentNodeLocator- "+node.noderef);
			String loc = null;
			IBISNode parent = null;
			int len = node.parents.size();
			String suffix = null;
			for (int i=0;i<len;i++) {
				loc = node.parents.get(i);
				parent = nodes.get(loc);
				if (loc.indexOf('_') == -1) {
					suffix = type2PostFix(parent.type);
					loc = loc + suffix;
					node.parents.set(i, loc);
				}
				smp.logDebug("CommonImportPullParser.updateParentNodeLocator "+loc);
			}
		}
		/**
		 * Change <code>noderef</code> to reflect this node's <code>type</code>
		 * @param node
		 * @throws IBISException
		 */
		void updateNoderef(IBISNode node) throws IBISException {
			String loc = node.noderef;
			//IBISNode parent = nodes.get(loc);
			loc = loc + type2PostFix(node.type);
			node.noderef = loc;
		}
		String type2PostFix(String type) throws IBISException {
			int which = Integer.parseInt(type);
			if (which > 10) which -= 10;
			if (which == ICompendiumConstants.ISSUE)
				return IConversationModel.Q_SUFFIX;
			if (which == ICompendiumConstants.POSITION)
				return IConversationModel.R_SUFFIX;
			if (which == ICompendiumConstants.ARGUMENT)
				return IConversationModel.ARG_SUFFIX;
			if (which == ICompendiumConstants.CON)
				return IConversationModel.CON_SUFFIX;
			if (which == ICompendiumConstants.PRO)
				return IConversationModel.PRO_SUFFIX;
			if (which == ICompendiumConstants.MAPVIEW)
				return IConversationModel.MAP_SUFFIX;
			if (which == ICompendiumConstants.DECISION)
				return IConversationModel.CNC_SUFFIX;
			if (which == ICompendiumConstants.NOTE)
				return IConversationModel.NOTE_SUFFIX;
			if (which == ICompendiumConstants.REFERENCE)
				return IConversationModel.REF_SUFFIX;
			throw new IBISException("CommonImportPullParser.type2PostFix bad type: "+type);
		}

    	/**
    	 * Can return <code>null</code>
    	 * @param key String
    	 * @param map HashMap
    	 * @return String
    	 */
    	String getAttribute(String key, HashMap map) {
    		if (map == null) return null;
    		return (String)map.get(key);
    	}
    
	    /**
	     * Return null if no attributes
	     */
	    HashMap getAttributes(XmlPullParser p) {
	      HashMap <String,String>result = null;
	      int count = p.getAttributeCount();
	      if (count > 0) {
	        result = new HashMap<String,String>();
	        String name = null;
	        for (int i = 0; i < count; i++) {
	          name = p.getAttributeName(i);
	          result.put(name,p.getAttributeValue(i));
	        }
	      }
	      return result;
	    }
	    
        /**
         * <p>If this is a UTF-16 String, then burp the gas from it.
         * This is some really butt-ugly code, done as a pure hack
         * @param xml String  looks like utf-16, but it's really just full of spaces
         * e.g. < m o d e l   r o o t v i e w = " 1 9 2 1 6 8 1 1 0 7 1 2 4 3 7 1 4 1 3 1 3 1 3 " >
         * @return String closer to utf-8-like no spaces
         * e.g. <model rootview="19216811071243714131313">
         * </p>
         * <p>There may be a better way to handle UTF-16 with the pull parser, but
         * that's not known to me at this time.</p>
         */
        String cleanString(String xml) throws Exception {
          String result = xml.trim();
          //host.tell(xml);
          if (xml.indexOf("U T F - 1 6") > 0) {
           // byte[] bRay = result.getBytes("UTF-16");
            //result = new String(result.getBytes("UTF-8"), "UTF-8");
           // result = new String(bRay, "UTF-8");
           int where = result.indexOf("< m o d e l");
          if (where > -1)
            result = result.substring(where);

            StringBuilder buf = new StringBuilder();
            int len = result.length();
            char c1, c2;
            String x;
            for (int i=0;i<len;i++) {
              c1 = result.charAt(i);
              if ( checkChar(c1) ) {
                if (i < (len-1)) {
                  c2 = result.charAt(++i);
                  if (checkChar(c2)) {
                    if (spaces(c1,c2))
                      buf.append(c1);
                    else {
                      if (c1 == ' ')
                        buf.append(c2);
                      else {
                        buf.append(c1);
                        buf.append(c2);
                      }
                    }
                  } else
                    buf.append(c1);
                }
              }
            }

            result = buf.toString();
            result = insertBreaks(result);
          }
          return result;
        }
        boolean spaces(char c1, char c2) {
            return (c1==' ' && c2 == ' ');
          }
          boolean checkChar(char c) {
            int x = (int)c;
            return ((x != 0) && (x != 9));
          }
          /**
           * Some really dirty hacks to clean up a messy string
           * @param inString String
           * @return String
           */
          String insertBreaks(String inString) {
            String x = inString;
            int where = x.indexOf("<views");
            String model = x.substring(0,where-1);
            x = x.substring(where);
            StringBuffer buf = new StringBuffer("<model rootview=\"");
            where = model.indexOf("\"");
            model = model.substring(where+1);
            int len = model.length();
            char c;
            for (int i=0;i<len;i++) {
              c = model.charAt(i);
              if (c != ' ') {
                if (c == '"') {
                  buf.append(c);
                  break;
                } else
                  buf.append(c);
              }
            }
            buf.append(">\n");
            where = x.indexOf("</views>");
            where += "</views>".length();
            System.out.println("INSERT BREAKS "+where);
            buf.append(x.substring(0,where)+"\n");
            x = x.substring(where);
            buf.append(x);
            String r = buf.toString();
            if (!r.endsWith(">"))
                r += ">";
            return r;
          }
}
