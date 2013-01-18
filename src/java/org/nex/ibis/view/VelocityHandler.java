/*
 *  Copyright (C) 2006  Jack Park,
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
package org.nex.ibis.view;
import java.io.StringWriter;
import java.util.*;

import org.nex.ibis.IBISException;

// velocity.jar
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import org.apache.velocity.runtime.RuntimeConstants;


/**
 * <p>Title: Topic Spaces Subject Map Engine</p>
 * <p>Description: Implementation of the TMRM</p>
 * <p>Copyright: Copyright (c) 2005, Jack Park</p>
 * <p>Company: NexistGroup</p>
 * @author Jack Park
 * @version 1.0
 */

public class VelocityHandler {
  private VelocityEngine engine = null;
  private VelocityContext context = null;

  /**
   * 
   * @param path  basePath to templates
   * @throws IBISException
   */
  public VelocityHandler(String path) throws IBISException {
     super();
     try {
        this.engine = new VelocityEngine();
        engine.setProperty(VelocityEngine.RESOURCE_LOADER, "file");
        engine.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS,
                           "org.apache.velocity.runtime.log.NullLogSystem");
        engine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
                path);
        engine.init();
        clearContext();
    } catch (Exception e) {
        throw new IBISException(e);
    }
  }

  /**
   * Clear the context
   */
  public void clearContext() {
	  if (context == null)
		  context = new VelocityContext();
	  else {
	    synchronized(context) {
	    	context = new VelocityContext();
	    }
	  }
  }

  /**
   * <p>
   * Caller can configure {@link VelocityContext}
   * with appropriate values.
   * </p>
   * @param key
   * @param value
   */
  public void put(String key, Object value) {
    if (context==null)
    	clearContext();
    synchronized(context) {
    	context.put(key,value);
    }
  }

  public Object get(String key) {
    return context.get(key);
  }

  /**
   * <p>
   * This allows us to dump content directly from an application
   * into a {@link VelocityContext}
   * </p>
   * @param keyValuePairs
   * @throws IBISException
   */
  public void add(Map keyValuePairs) throws IBISException {
	  Iterator itr = keyValuePairs.keySet().iterator();
	  String s;
	  while (itr.hasNext()) {
		  s = (String) itr.next();
		  put (s, keyValuePairs.get(s));
	  }

  }
  /**
   * <p>
   * This method requires user to call:<br>
   * <code>clearContext</code> followed by as many<br>
   * <code>put</code> values as required.
   * </p>
 * @param basePath e.g. wiki
 * @param locale e.g en or ""
 * @param template e.g mytemplate.vm
   * @return
   * @throws IBISException
   */
  public String processPage(String basePath, String locale, String template) throws IBISException {
     synchronized(context) {
    	 String loc = locale;
    	 if (!loc.equals(""))
    		 loc = loc+"/";
    	 String path = basePath+loc+template;
	    try {
	    	System.out.println("%%%% Template file "+path);
            // load templates
	    	Template tplt = null;
	    	try {
	    		tplt = engine.getTemplate(path);
	    	} catch (Exception x1) {
	    		path = basePath+"en/"+template;
	    		try {
	    			tplt = engine.getTemplate(path);
	    		} catch (Exception x2) {
		    		path = basePath+template;
		    		try {
		    			tplt = engine.getTemplate(path);
		    		} catch (Exception x3) {
		    			throw new IBISException("VelocityHandler.processPage missing template for "+basePath+" "+template);
		    		}
	    		}
	    	}
            tplt.setModificationCheckInterval(5000L);
            StringWriter writer = new StringWriter();
            //merge content
            tplt.merge(context,writer);
            writer.close();
            return writer.toString();
	    } catch (Exception e) {
	        throw new IBISException(e);
	    }
     }
  }

}