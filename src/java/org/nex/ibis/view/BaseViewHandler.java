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
package org.nex.ibis.view;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nex.ibis.model.api.ISecurity;
import org.nex.ibis.model.Environment;
import org.nex.ibis.model.Ticket;
import org.nex.ibis.model.CredentialCache;
import org.nex.ibis.IBISException;


/**
 * 
 * @author park
 * <p>Common methods and utilities</p>
 */
public abstract class BaseViewHandler {
	public static final String CONTENT_KEY = "content";
	public static final String HEADER_KEY = "header";
	public static final String SUBHEADER_KEY = "subheader";
	public static final String HASRELATIONS_KEY = "hasrelations";
	/**
	 * The User Session Object is the bit of information needed
	 * when a user is authenticated
	 */
	public static final String USER_SESSION_OBJECT = "usrsesobj";

	/**
	 * 200
	 */
	public static final int RESPONSE_OK = HttpServletResponse.SC_OK;
	/**
	 * 400
	 */
	public static final int RESPONSE_BAD = HttpServletResponse.SC_BAD_REQUEST;
	/**
	 * 401
	 */
	public static final int RESPONSE_UNAUTHORIZED = HttpServletResponse.SC_UNAUTHORIZED;
	/**
	 * 403
	 */
	public static final int RESPONSE_FORBIDDEN = HttpServletResponse.SC_FORBIDDEN;
	/**
	 * 404
	 */
	public static final int RESPONSE_NOT_FOUND = HttpServletResponse.SC_NOT_FOUND;
	/**
	 * 407
	 */
	public static final int RESPONSE_AUTHENTICATION_REQUIRED = HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED;
	/**
	 * 500
	 */
	public static final int RESPONSE_INTERNAL_SERVER_ERROR = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

	protected Environment environment;
	protected static Ticket guestCredentials = null;
	private String velocityPath;
	protected CredentialCache credentialCache;
	

	public BaseViewHandler(Environment e, String veloPath) {
		environment = e;
		velocityPath = veloPath;
		credentialCache = CredentialCache.getInstance();
		if (guestCredentials == null) {
			guestCredentials = new Ticket();
			guestCredentials.setOwner(ISecurity.GUEST_USER);
		}
		environment.logDebug("BaseViewHandler- "+velocityPath);
	}

    public boolean checkAuthentication(String path, boolean needsAuthentication, Ticket credentials, HttpServletResponse response) 
			throws IOException {
		if (path.endsWith("Login") || path.endsWith("authenticate"))
			return false;
		if (needsAuthentication) {
			if (credentials.getOwner().equals(ISecurity.GUEST_USER)) {
				sendRedirect("","Login",response);
				return true;
			}
		}
		return false;
	}
    
    public Ticket getCredentials(HttpServletRequest request, VelocityHandler handler) throws IBISException {
    	Ticket result = guestCredentials; //default
    	String userLocator = (String)request.getSession().getAttribute(BaseViewHandler.USER_SESSION_OBJECT);
    	if (userLocator != null && !userLocator.equals("")) //TODO should we check for GUEST_USER???
    		result =  credentialCache.getCredential(userLocator);
    	advertiseCredentials(result,handler);
    	return result;
    }

    /**
     * <p>Paths that come in without a trailing '/' can be null</p>
     * <p>e.g. http://localhost:8080/admin/jackpark/foo?x=true<br/>
     * returns Admin Hello:jackpark/foo | x=true when using a simple query:
     * path = jackpark/foo queryString = x=true</p>
     * @param request
     * @return
     */
    public String getPath(HttpServletRequest request) {
    	String path = notNullString(request.getPathInfo()).trim();
    	if (path.startsWith("/"))
    		path = path.substring(1);
    	if (path.endsWith("/"))
    		path = path.substring(0,path.length()-1);
    	return path;
    }

    public String getQueryString(HttpServletRequest request) {
    	return notNullString(request.getQueryString());
    }
	
	public abstract void doGet(String path, VelocityHandler handler, 
								HttpServletRequest request, HttpServletResponse response, 
								Ticket credentials)throws ServletException, IOException;
	
	public abstract void doPost(String path, VelocityHandler handler, 
								HttpServletRequest request, HttpServletResponse response, 
								Ticket credentials)throws ServletException, IOException;
	
	/**
	 * Called by servlet; performs setups, then calls abstract methods implemented in extension classes
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String path = getPath(request);
    	try {
    		VelocityHandler handler = new VelocityHandler(velocityPath);
    		Ticket credentials = getCredentials(request,handler);
    		doGet(path, handler, request, response, credentials);
    	} catch (IBISException e) {
    		environment.logError("BaseViewHandler.handleGet error "+e.getMessage(),e);
    		throw new ServletException(e);
    	}
    }
    
    /**
     * Called by servlet; performs setups, then calls abstract methods implemented in extension classes
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void handlePost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String path = getPath(request);
    	try {
    		VelocityHandler handler = new VelocityHandler(velocityPath);
    		Ticket credentials = getCredentials(request,handler);
    		doPost(path, handler, request, response, credentials);
    	} catch (IBISException e) {
    		environment.logError("BaseViewHandler.handlePost error "+e.getMessage(),e);
    		throw new ServletException(e);
    	}
   }
    /**
     * Return a not <code>null</code> String
     */
    public String notNullString(String in) {
    	if (in == null) return "";
    	return in;
    }
    /**
     * 
     * @param prefix  e.g. "/wiki/"
     * @param url     e.g. "1232123.2321"
     * @param response
     */
    public void sendRedirect(String prefix, String url, HttpServletResponse response) throws IOException {
        String urlWithSessionID = response.encodeRedirectURL(prefix+url);
        response.sendRedirect(urlWithSessionID);
    }
    
	public void sendXML(String html, HttpServletResponse response) throws IOException {
    	response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
    	out.write(html);
    	out.close();
    }
	
	public void sendHTML(String html, HttpServletResponse response) throws IOException {
    	response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
    	out.write(html);
    	out.close();
	}
    public void setContent(VelocityHandler handler, String content) {
    	handler.put(BaseViewHandler.CONTENT_KEY, content);
    }
    public void setHeader(VelocityHandler handler, String content) {
    	handler.put(BaseViewHandler.HEADER_KEY, content);   	
    }
    public void setSubHeader(VelocityHandler handler, String content) {
    	handler.put(BaseViewHandler.SUBHEADER_KEY, content);   	
    }
    
    public void paintFile(VelocityHandler handler, HttpServletResponse response) throws IOException {
    	try {
    		if(handler.get("language")== null)  handler.put("language", "en");
    		String HTML = handler.processPage("", "", "front-page.vm");
    		sendHTML(HTML,response);
    	} catch (IBISException e) {
    		throw new IOException(e.getMessage());
    	}
    }
    public void paintAdmin(VelocityHandler handler, HttpServletResponse response) throws IOException {
    	try {
    		if(handler.get("language")== null)  handler.put("language", "en");
    		String HTML = handler.processPage("", "", "admin-front.vm");
    		sendHTML(HTML,response);
    	} catch (IBISException e) {
    		throw new IOException(e.getMessage());
    	}
    }
	/**
	 * <p>Perform the following steps:
	 * <li>If the user is a guest, exit and do nothing</li>
	 * <li>Add "IsAuthenticated" value to VelocityHandler</li>
	 * <li>If the user is an administrator, add "IsAdmin" to VelocityHandler</li>
	 * <li>If the user is a moderator, add "IsModerator" to VelocityHandler</li>
	 * </p>
	 * <p>These actions let Velocity templates make better decisions on what to paint</p>
	 * @param credentials
	 */
	private void advertiseCredentials(Ticket credentials, VelocityHandler handler) {
		environment.logDebug("BaseViewHandler.advertiseCredentials-");
		if (credentials.getOwner().equals(ISecurity.GUEST_USER) ||
				(credentials.getPrivileges() != null && credentials.getPrivileges().contains(ISecurity.GUEST_USER)))
			return;
		handler.put("IsAuthenticated", "T");
		Set privs = credentials.getPrivileges();
		if (privs != null) {
			if (privs.contains(ISecurity.ADMINISTRATOR_ROLE))
				handler.put("IsAdmin", "T");
			if (privs.contains(ISecurity.MODERATOR_ROLE))
				handler.put("IsModerator", "T");
		}
		environment.logDebug("BaseViewHandler.advertiseCredentials+");
	}
	
	protected boolean isAdmin(Ticket credentials) {
		Set privs = credentials.getPrivileges();
		if (privs != null) {
			if (privs.contains(ISecurity.ADMINISTRATOR_ROLE))
				return true;
		}
		return false;
	}
	
	protected boolean isAuthenticated(Ticket credentials) {
		if (credentials.getOwner().equals(ISecurity.GUEST_USER) ||
				(credentials.getPrivileges() != null && credentials.getPrivileges().contains(ISecurity.GUEST_USER)))
			return false;
		return true;

	}
}
