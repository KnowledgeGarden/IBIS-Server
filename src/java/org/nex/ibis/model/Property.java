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
package org.nex.ibis.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.nex.ibis.IBISException;

/**
 * 
 * @author Park
 *
 */
public class Property {
	/**
	 * Part of a state machine that determins what
	 * the database does with this Property
	 */
	private boolean isChanged = false;
	private boolean isUpdate = false;
	/**
	 * When <code>true</code>, the database treats <code>newValues</code>
	 * as inserts, and looks for updates
	 */
	private boolean isPersisted = false;
	
	/**
	 * <code>locator</code> is unique
	 */
	private String locator;
	private int type = 0;
	private long created,modified;
	
	/**
	 * <p>This allows us to track the origin of a <code>Property</code>
	 * particularly when the Property is brought in through a <em>merge</em>
	 * process. There, we define an importedLegend, and use that locator</p>
	 */
	private String definingLegendLocator = null;
	
	/**
	 * <code>key</code> is the <code>name</code> of a proxy
	 * that defines this <code>property</code>
	 */
	private String key;
	private List<String> values = new ArrayList<String>();
	private List<String> newValues = new ArrayList<String>();
	private List<String> removedValues = null;
	/**
	 * Special cache value
	 */
//	private List<SubjectProxy> proxyValues = null;
	/**
	 * <p>If <code>isUpdate</code> is <code>true</code>
	 * then do a database update to replace <code>oldValue</code>
	 * with <code>newValue</code></p>
	 * <p>Note: this is designed to do one update at a time between
	 * storing property</p>
	 */
	private String oldValue,newValue;
	
//	public void setProxyValues(List<SubjectProxy>vals) {
//		proxyValues = vals;
//	}
	
	/**
	 * Clears and sets one value
	 * @param value
	 */
	public void clearAndSetValue(String value) {
		values.clear();
		values.add(value);
	}
	/**
	 * Can return <code>null</code>
	 * @return
	 */
//	public List<SubjectProxy> getProxyValues() {
//		return proxyValues;
//	}
	public Property(String uid) {
		locator = uid;
	}
	
	public String getLocator() {
		return locator;
	}
	public String getKey() {
		return key;
	}
	public void setLocator(String n) {
		locator = n;
	}
	public int getType() {
		return type;
	}
	public void setType(int t) {
		type = t;
	}
	
	/**
	 * Return a full-depth clone of this property
	 * @param session
	 * @return
	 * @throws TopicSpacesException
	 * /
	public Property clone(ISubjectProxySession session) throws TopicSpacesException {
		Property result = new Property(session.getNewUID());
		result.setKey(this.key);
		result.setDefiningLegendLocator(this.definingLegendLocator);
		Iterator<String>itr = this.values.iterator();
		while (itr.hasNext())
			result.addValue(itr.next());
		return result;
	}
	/**
	 * All properties are defined by a particular legend
	 * @param loc
	 */
	public void setDefiningLegendLocator(String loc) {
		this.definingLegendLocator = loc;
	}
	public String getDefiningLegendLocator() {
		return this.definingLegendLocator;
	}
	public void setValue(List<String> newVal) {
		this.values = newVal;
	}
	/**
	 * <code>val</code> can either be a List or a String
	 * @param key
	 * @param val
	 */
	public void put(String key, Object val) throws IBISException {
		this.key = key;
//		System.out.println("Property.put "+val);
		if (val instanceof List)
			values = (List<String>)val;
		else if (val instanceof String) {
			values.add((String)val);
			if (isPersisted)
				addNewValue((String)val);
		} else
			throw new IBISException("Property.put bad value type");
	}
	public void setKey(String k) {
		key = k;
	}
	public void addValue(String val) {
		if (isPersisted)
			addNewValue(val);
		//possible bug in Derby not deleting rows
		if (!values.contains(val))
			values.add(val);
	}
	
	public void addBagValue(String val) {
		values.add(val);
		if (isPersisted)
			addNewValue(val);
	}
	
	public void replaceValue(String oldValue, String newValue) {
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.isUpdate = true;
		this.isChanged = true;
		//go ahead and change the damn thing!
		int len = values.size();
		int i=0;
		for (i=0;i<len;i++) {
			if (oldValue.equals(values.get(i))) {
				values.set(i, newValue);
				return;
			}
		}
	}
	
	public void setIsUpdate(boolean t) {
		isUpdate = t;
		if (!t) {
			oldValue = null;
			newValue = null;
		}
		
	}
	public boolean getIsUpdate() {
		return isUpdate;
	}
	public void removeValue(String val) {
		values.remove(val);
		if (removedValues==null)
			removedValues = new ArrayList<String>();
		removedValues.add(val);
	}
	public void clearRemovedValues() {
		removedValues = null;
	}
	/**
	 * 
	 * @return can return <code>null</code>
	 */
	public List<String> getRemovedValues() {
		return removedValues;
	}
	public void setCreated(long c) {
		created = c;
	}
	public void setModified(long m) {
		modified = m;
	}
	public long getCreated() {
		return created;
	}
	public long getModified() {
		return modified;
	}
	public String[] getStringValues() {
//		System.out.println("Property.getValues "+values);
		int len = values.size();
		if (len==0)
			return new String[0];
		String [] x = new String [len];
		for (int i=0;i<len; i++)
			x[i] = values.get(i);
		return x;
//		return (String[])values.toArray();
	}
	
	/**
	 * Can return <code>null</code>
	 * @return
	 */
	public String getFirstStringValue() {
		if (values.size() == 0) return null;
		return values.get(0);
	}
	
	public boolean addNewValue(String newVal) {
		//This is going to be problematic:
		//won't be able to create bags, only sets
		//This strongly suggests that Property objects
		//should say whether they are sets or bags
		System.out.println("Property.addNewValue- "+newVal+" "+values+" "+newValues);
		if (!values.contains(newVal)) {
			if (!newValues.contains(newVal)) {
				newValues.add(newVal);
				return true;
			}
		}
		return false;
	}
	public List<String> getNewValues() {
		return newValues;
	}
	public void clearNewValues() {
		newValues.clear();
	}
	
	public boolean contains(String val) {
		return values.contains(val);
	}
	
	public boolean hasAllSameValues(Property p) {
		List<String> vals = p.getRawValues();
		int len = vals.size();
		for (int i=0;i<len;i++) {
			if (!this.values.contains(vals.get(i)))
				return false;
		}
		return true;
	}
	/**
	 * Any value that endsWith <code>val</code> will return <code>true</code>
	 * @param val
	 * @return
	 */
	public boolean containsEndsWith(String val) {
		if (values==null || values.size()==0) return false;
		Iterator<String>itr = values.iterator();
		while (itr.hasNext())
			if (itr.next().endsWith(val))
				return true;
		return false;
	}
	public String getOldValue() {
		return oldValue;
	}
	public String getNewValue() {
		return newValue;
	}
	/**
	 * diagnostic
	 * @return
	 */
	public List<String> getRawValues() {
		return values;
	}
	
	public void setRawValues(List<String> vals) {
		values = vals;
	}
	public String getValue() {
		String result = "";
		if (values.size() > 0)
			result = values.get(0);
		return result;
	}
	public int depth() {
		return values.size();
	}
	public String getString() {
		return getValue();
	}
	public boolean getIsChanged() {
		return isChanged;
	}
	public void setIsChanged(boolean t) {
		isChanged= t;
	}
	public boolean getIsPersisted() {
		return isPersisted;
	}
	public void setIsPersisted(boolean t) {
		isPersisted = t;
	}
	public String toXML() {
		StringBuilder buf = new StringBuilder("  <property id=\""+locator+"\">\n");
		//a key is always a proxyLocator
		buf.append("    <key>"+key+"</key>\n");
		Iterator itr = values.iterator();
		while (itr.hasNext()) //value can hold unescaped XML/HTML
			buf.append("    <value><![CDATA["+itr.next()+"]]></value>\n");
		buf.append("  </property>\n");
		return buf.toString();
	}
	
	public int compareTo(Object o) {
		if (o instanceof Property) {
			Property x = (Property)o;
			if (locator.equals(x.getLocator())) {
				if (this.type == x.getType()) {
					if (getRawValues().containsAll(x.getRawValues()))
						return 1;
				}
			}
		}
		return 0;
	}
}
