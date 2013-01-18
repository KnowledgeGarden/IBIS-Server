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
package org.nex.ibis.model.api;

/**
 * 
 * @author park
 * <p>Plagarized from
 *  <code>com.compendium.core.ICoreConstants</code>
 * </p>
 */
public interface ICompendiumConstants {
	
    ////////////////////////////////////
    // Copied directly from Compendium source com.compendium.core.ICoreConstants
    // NODE TYPES
    /** This node type is not currently used */
    public final static int GENERAL = 0;
    /**
     * This represents a list, which is a container for other nodes.
     * This can be used to create a sortable list of nodes,
     * which will usually be a collection of nodes that don't need to be
     * linked with each other (associative links).
     */
    public final static int LISTVIEW = 1;
    /**
     * This represents a map, which is a container for other nodes and links.
     * This can be used to:
     * - create a 'picture'of the relationships between ideas;
     * - group questions and ideas together in meaningful clusters;
     * - create associative links between nodes.
     */
    public final static int MAPVIEW = 2;
    /** This represents a Question or Issue for discussion. */
    public final static int ISSUE = 3;
    /** The represents an Answer or Position, often in response to a question or issue.*/
    public final static int POSITION = 4;
    /** This represents a general argument, usually in response to an answer or position.*/
    public final static int ARGUMENT = 5;
    /** This represents a response in favour of an answer or position. */
    public final static int PRO = 6;
    /** This represents a response against an answer or position. */
    public final static int CON = 7;
    /** This represents a decision reached, usually from an answer or position about a question or issue.*/
    public final static int DECISION = 8;
    /**
     * This represents a link to some additional, external reference material,
     * which can be in the form of a web link or Word document etc.
     * These nodes can also be linked to images, which will then be scaled
     * and used instead of the usual reference node icon.
     */
    public final static int REFERENCE = 9;
    /**
     * This represents some non-specific, additional comment or notation,
     * often about a node or the current view.
     */
    public final static int NOTE = 10;
    // SHORTCUT NODE TYPES ARE NODES THAT HAVE A REFERENCE TO A PARENT STANDARD NODE TYPE.
    // THESE ARE USED FOR LARGE MAPS WHERE A NODE MAY BE TOO FAR AWAY FOR PRACTICAL LINKING
    // SO A SHORTCUT OF IT IS CREATED TO ALLOW FOR EASE OF MAPPING
    // THERE USE IS UNDER REVIEW.
    /** This represents a shortcut to a List Node */
    public static final int LIST_SHORTCUT = 11;
    /** This represents a shortcut to a Map Node */
    public static final int MAP_SHORTCUT = 12;
    /** This represents a shortcut to a Issue Node */
    public static final int ISSUE_SHORTCUT = 13;
    /** This represents a shortcut to a Position Node */
    public static final int POSITION_SHORTCUT = 14;
    /** This represents a shortcut to a Argument Node */
    public static final int ARGUMENT_SHORTCUT = 15;
    /** This represents a shortcut to a Pro Node */
    public static final int PRO_SHORTCUT = 16;
    /** This represents a shortcut to a Con Node */
    public static final int CON_SHORTCUT = 17;
    /** This represents a shortcut to a Decision Node */
    public static final int DECISION_SHORTCUT = 18;
    /** This represents a shortcut to a Reference Node */
    public static final int REFERENCE_SHORTCUT = 19;
    /** This represents a shortcut to a Note Node */
    public static final int NOTE_SHORTCUT = 20;
   // ARROW TYPES
    /** Link with no arrow heads */
    public final static int 	NO_ARROW = 0;
    /** Link with arrow head at the to Node end */
    public final static int 	ARROW_TO = 1;
    /** Link with arrow head at the from Node end */
    public final static int 	ARROW_FROM = 2;
    /** Link with arrow heads at both ends of the link */
    public final static int 	ARROW_TO_AND_FROM = 3;
    //LINK TYPES
    /** Indicates the Node at the FROM end of the link responds to the Node at the TO end of the link */
    public final static String	RESPONDS_TO_LINK = "39";
    /** Indicates the Node at the FROM end of the link supports the Node at the TO end of the link */
    public final static String	SUPPORTS_LINK	= "40";
    /** Indicates the Node at the FROM end of the link objects to the Node at the TO end of the link */
    public final static String	OBJECTS_TO_LINK	 = "41";
    /** Indicates the Node at the FROM end of the link challenges the Node at the TO end of the link */
    public final static String	CHALLENGES_LINK	 = "42";
    /** Indicates the Node at the FROM end of the link is a specialization of the Node at the TO end of the link */
    public final static String	SPECIALIZES_LINK = "43";
    /** Indicates the Node at the FROM end of the link expans on the Node at the TO end of the link */
    public final static String	EXPANDS_ON_LINK	= "44";
    /** Indicates the Node at the FROM end of the link is related to the Node at the TO end of the link */
    public final static String	RELATED_TO_LINK	= "45";
    /** Indicates the Node at the FROM end of the link is about the Node at the TO end of the link */
    public final static String	ABOUT_LINK	= "46";
    /** Indicates the Node at the FROM end of the link resolves the Node at the TO end of the link */
    public final static String	RESOLVES_LINK	= "47";
    /** Indicates which link type is the default link type - Currently the 'Related To Link' */
    public final static String	DEFAULT_LINK	= "45";
    // LINK NAMES
    /** Holds a string representation of the RESPONDS_TO_LINK type*/
    public final static String	sRESPONDSTOLINK	= "Responds To Link";
    /** Holds a string representation of the SUPPORTS_LINK type*/
    public final static String	sSUPPORTSLINK	= "Supports Link";
    /** Holds a string representation of the OBJECTS_TO_LINK type*/
    public final static String	sOBJECTSTOLINK	= "Objects To Link";
    /** Holds a string representation of the CHALLENGES_LINK type*/
    public final static String	sCHALLENGESLINK	= "Challenges Link";
    /** Holds a string representation of the SPECIALIZES_LINK type*/
    public final static String	sSPECIALIZESLINK = "Specializes Link";
    /** Holds a string representation of the EXPANDS_ON_LINK type*/
    public final static String	sEXPANDSONLINK	= "Expands On Link";
    /** Holds a string representation of the RELATED_TO_LINK type*/
    public final static String	sRELATEDTOLINK	= "Related To Link";
    /** Holds a string representation of the ABOUT_LINK type*/
    public final static String	sABOUTLINK	= "About Link";
    /** Holds a string representation of the RESOLVES_LINK type*/
    public final static String	sRESOLVESLINK	= "Resolves Link";
    /** Holds a string representation of the default link type - currently the RELATED_TO_LINK.*/
    public final static String	sDEFAULTLINK	= "Related To Link";
    // LINK LABELS
    /** Holds a string representation of the RESPONDS_TO_LINK type*/
    public final static String	sRESPONDSTOLINKLABEL	= "Responds To";
    /** Holds a string representation of the SUPPORTS_LINK type*/
    public final static String	sSUPPORTSLINKLABEL		= "Supports";
    /** Holds a string representation of the OBJECTS_TO_LINK type*/
    public final static String	sOBJECTSTOLINKLABEL		= "Objects To";
    /** Holds a string representation of the CHALLENGES_LINK type*/
    public final static String	sCHALLENGESLINKLABEL	= "Challenges";
    /** Holds a string representation of the SPECIALIZES_LINK type*/
    public final static String	sSPECIALIZESLINKLABEL	= "Specializes";
    /** Holds a string representation of the EXPANDS_ON_LINK type*/
    public final static String	sEXPANDSONLINKLABEL	= "Expands On";
    /** Holds a string representation of the RELATED_TO_LINK type*/
    public final static String	sRELATEDTOLINKLABEL	= "Related To";
    /** Holds a string representation of the ABOUT_LINK type*/
    public final static String	sABOUTLINKLABEL		= "About";
    /** Holds a string representation of the RESOLVES_LINK type*/
    public final static String	sRESOLVESLINKLABEL	= "Resolves";
    //
    ////////////////////////////////////
}
