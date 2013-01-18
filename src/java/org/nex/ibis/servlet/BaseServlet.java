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
package org.nex.ibis.servlet;

import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.nex.ibis.IBISException;
import org.nex.ibis.model.Environment;

/**
 * 
 * @author Owner
 *
 */
public class BaseServlet extends HttpServlet {
	protected Environment environment;
    private ServletContext m_context;
    /**
     * Home file path for this servlet
     */
     protected File m_homeDir;
     private String m_homePath;
     protected String velocityPath;
     
     public void init(ServletConfig config) throws ServletException {
    	 System.out.println("BaseServlet-1");
     	environment = Environment.getInstance();
     	try {
	    		m_context = config.getServletContext();
 	           	m_homeDir = new File(m_context.getRealPath("/"));  
 	           	m_homePath = m_homeDir.getAbsolutePath();
 	           	File webInfDir = new File(m_homeDir, "WEB-INF");
 	           //velocity
 	           File files = new File(webInfDir, "templates");
 	           velocityPath = files.getAbsolutePath();  
 	    	if (environment == null) {
 		        File conf = new File(webInfDir,"core-portal-props.xml");
 	   			environment = new Environment(conf.getAbsolutePath());
 	    	}
       	} catch (IBISException e) {
     		environment.logError("FileServlet error "+e.getMessage(),e);
     		throw new ServletException(e);
     	}
     }
     public void destroy() {
     	environment.shutDown();
     }

}
