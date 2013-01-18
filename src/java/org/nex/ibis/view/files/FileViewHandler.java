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
package org.nex.ibis.view.files;

import java.io.*;
import java.util.*;
import java.net.HttpURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nex.ibis.model.Environment;
import org.nex.ibis.model.Ticket;
import org.nex.ibis.model.files.FileModel;
import org.nex.ibis.IBISException;
import org.nex.ibis.view.BaseViewHandler;
import org.nex.ibis.view.VelocityHandler;

/**
 * 
 * @author Park
 *
 */
public class FileViewHandler extends BaseViewHandler {
	private FileModel model;
	private String [] platforms;
	
	public FileViewHandler(Environment e, String velocityPath) throws IBISException {
		super(e, velocityPath);
		model = environment.getFileModel();
		List p = (List)environment.getProperty("DocumentTypes");
		List x;
		int len = p.size();
		platforms = new String[len];
		for (int i=0;i<len;i++) {
			x = (List)p.get(i);
			platforms[i] = (String)x.get(1);
		}
		environment.logDebug("FileViewHandler started");
	}

    public void doGet(String path, VelocityHandler handler, 
    					HttpServletRequest request, HttpServletResponse response, 
    					Ticket credentials) throws ServletException, IOException {
 		environment.logDebug("FileViewHandler.doGet- "+path);
    	System.out.println("DoGet "+path);
    	System.out.println("XXX "+request.getServletPath());
    	if (path.startsWith("ws")) {
    		doWebServices(path,handler,request,response,credentials);
    	} else {
    		doBrowser(path,handler,request,response,credentials);
    	}
    }
    
    public void doPost(String path, VelocityHandler handler, 
    					HttpServletRequest request, HttpServletResponse response, 
    					Ticket credentials) throws ServletException, IOException {
    	System.out.println("DoPost "+path);
    	
    	String html = "";
  //  	if (path.startsWith("load/"))
  //  		path = path.substring("load/".length());
  //  	else if (path.startsWith("load"))
  // 		path = path.substring("load".length());
    	try {
    		// /AcceptFile?fileName=foo?platform=cm?description=bar?cargo=<xml.....
	//    	if (path.equals("AcceptFile")) { //@see file-form.vm
	//    		doAddFile(request,credentials);
	//		    sendRedirect("","",response);
	    	 if (path.equals("authenticate")) {
				String name = request.getParameter("username");
				String pwd = request.getParameter("password");
				Ticket user = model.authenticate(name, pwd);
	    		if (user==null) {
	    			html = "Authentication failed";
	    			setContent(handler,html);
	    			paintFile(handler,response);
	    		} else {
				    HttpSession session = request.getSession(true);
				    session.setAttribute(BaseViewHandler.USER_SESSION_OBJECT, name);
				    sendRedirect("","",response);
	    		}
	    	} else if (path.equals("ws/authenticate")) {
				String name = request.getParameter("username");
				String pwd = request.getParameter("password");
				String username = request.getHeader(BaseViewHandler.USER_SESSION_OBJECT);
				environment.logDebug("FileViewHandler.doPost ws/authenticate "+username);
				Ticket user = model.authenticate(name, pwd);
	    		if (user==null) {
	    			html = "Authentication failed";
	    			sendHTML(html+" "+name+" "+pwd,response);
	    		} else {
				    HttpSession session = request.getSession(true);
				    session.setAttribute(BaseViewHandler.USER_SESSION_OBJECT, name);
				    //sendRedirect("","",response);
				    sendHTML((String)session.getAttribute(BaseViewHandler.USER_SESSION_OBJECT),response);
	    		}
	    	} else if (path.equals("ws/put")) {
	    		//Webservices call
	    		// /put?fileName=foo?platform=cm?description=bar?cargo=<xml...
	    		try {
	    			doWsAddFile(request,credentials);
	    			response.setStatus(HttpURLConnection.HTTP_ACCEPTED);
	    		} catch (IBISException x) {
	    			response.sendError(HttpURLConnection.HTTP_BAD_REQUEST,x.getMessage());
	    			return;
	    		}
	    		sendHTML("",response);
	    	} else if (path.startsWith("ws/remove")) {
	 			String name = request.getParameter("username");
	 			String pwd = request.getParameter("password");
	 			String pwd64 = request.getParameter("password64");
	 			if (pwd == null&& pwd64 == null)
	 				throw new IBISException("FileViewHandler.doPost missing password");
	 			Ticket user = null;
	 			if (pwd != null) 
	 				user = model.authenticate(name, pwd);
	 			else
	 				user = model.authenticate64(name, pwd64);
	 			environment.logDebug("FileViewHandler.doWebServices 3 "+name+" "+user+" "+pwd64+" "+pwd);
	 			if (user == null) {
	    			response.sendError(HttpURLConnection.HTTP_BAD_REQUEST,"Can't authenticate "+name);
	    			return;
	 			}
	 			String filename = request.getParameter("fileName");
	 			String platform = request.getParameter("platform");
	 			//we are ignoring the boolean result from remove. It seems to work
	 			//users can tell by checking back to see if the file was removed
	 			model.removeFile(platform, filename, user.getOwner());
	 			sendHTML("",response);
	 		}
    	} catch (IBISException e) {
    		environment.logError("FileViewHandler.doPost error "+e.getMessage(),e);
    		throw new ServletException(e);
    	}
    }
    
    /**
     * <p>First, authenticate user. If no authenticate, fail.</p>
     * <p>This method grants two options on password: plain or Base64 encoded</p>
     * @param request
     * @param credentials
     * @throws IBISException
     */
    void doWsAddFile(HttpServletRequest request, Ticket credentials)
			throws IBISException {
		String name = request.getParameter("username");
		String pwd = request.getParameter("password");
		String pwd64 = request.getParameter("password64");
		if (pwd == null&& pwd64 == null)
			throw new IBISException("FileViewHandler.doWsAddFile missing password");
		Ticket user = null;
		if (pwd != null) 
			user = model.authenticate(name, pwd);
		else
			user = model.authenticate64(name, pwd64);
		environment.logDebug("FileViewHandler.doWsAddFile- "+name+" "+user+" "+pwd64+" "+pwd);
		if (user == null)
			throw new IBISException("FileViewHandler.doWsAddFile can't authenticate "+name);
		String fileName = request.getParameter("fileName");
		String platform = request.getParameter("platform");
		String description = request.getParameter("description");
		description = description.replaceAll("%20"," ");
		environment.logDebug("FileViewHandler.doWsAddFile 1 "+fileName+" | "+platform+" | "+description);
		String cargo = "";
		try {
			InputStream is = request.getInputStream();
			int c;
			StringBuilder buf = new StringBuilder();
			while ((c = is.read()) != -1) {
				buf.append((char)c);
			}
			cargo = buf.toString();
			environment.logDebug("FileViewHandler.doWsAddFile 2 "+cargo);
		} catch(IOException e) {
			environment.logError("FileViewHandler.doWsAddFile error "+e.getMessage(),e);
			throw new IBISException(e);
		}
		model.addFile(fileName, platform, description, cargo, user.getOwner());
		environment.logDebug("FileViewHandler.doWsAddFile+");
	}
   /** 
    void doAddFile(HttpServletRequest request, Ticket credentials)
    		throws IBISException {
		String fileName = request.getParameter("fileName");
		String platform = request.getParameter("platform");
		String description = request.getParameter("description");
		String cargo = request.getParameter("cargo");
		model.addFile(fileName, platform, description, cargo, credentials.getOwner());
    }
    /**
     * <p>Here because we are not painting HTML to a browser, instead, responding
     * with these use cases:
     * <ul>
     * <li>Returning a list of all files in the form {filename, platform, description}</li>
     * <li>Returning a list of files associated with a platform in the form {filename, description}</li>
     * <li>Returning a particular file associated with {filename, platform}</li>
     * </ul></p>
      * @param path
     * @param handler
     * @param request
     * @param response
     * @param credentials
     * @throws ServletException
     * @throws IOException
     */
    private void doWebServices(String path, VelocityHandler handler, 
								HttpServletRequest request, HttpServletResponse response, 
								Ticket credentials) throws ServletException, IOException {
    	String html="";
    	String platform;
    	String filename;
 		environment.logDebug("FileViewHandler.doWebServices- "+path);
    	try {
    		if (path.startsWith("ws/"))
    			path = path.substring("ws/".length());
    		else 
    			path = path.substring("ws".length());
	    	if (path.equals("")) {
	    		//SEND JSON
	 			html =  model.listAllFilesAsJSON();
	 			environment.logDebug("FileViewHandler.doWebServices 1 "+html);
	 			sendHTML(html,response);
	 		} else if (path.startsWith("get")) {
	 			//get/<platform>/<filename>
	 			//e.g. http://localhost:8080/get/cm/ggw-3.xml
	 			//SEND XML
	 			path = path.substring("get/".length());
	 			platform = getPlatform(path);
	 			path = path.substring((platform+"/").length());
	 			filename = getFilename(path);
	 			environment.logDebug("FileViewHandler.doWebServices 2 "+platform+" "+filename);
	 			html = model.getFile(platform, filename);
	 			sendXML(html,response);
	 		} 
 		} catch (IBISException e) {
 			environment.logError("FileViewHandler.doWebServices error "+e.getMessage(),e);
 			throw new ServletException(e);
 		}
 		
    }
    
    /**
     * <p>Here because we are servicing a browser request with the following use cases:
     * <ul>
     * <li>Return a list of all files starting with {filename, platform, description} painted
     * as HTML</li>
     * <li>Return a selected file in an HTML tree form</li>
     * </ul></p>
     * @param path
     * @param handler
     * @param request
     * @param response
     * @param credentials
     * @throws ServletException
     * @throws IOException
     */
    private void doBrowser(String path, VelocityHandler handler, 
							HttpServletRequest request, HttpServletResponse response, 
							Ticket credentials) throws ServletException, IOException {
 		environment.logDebug("FileViewHandler.doBrowser- "+path);
		String html="";
 		String platform;
 		String filename;
 		if (isAuthenticated(credentials))
 			handler.put("IsAuthenticated", "true");
 		try {
	 		if (path.equals("")) {
	 			List<List<String>> files = model.listAllFiles();
	 			environment.logDebug("FileViewHandler.doBrowser-1 "+files);
	 			handler.put("filelist", files);
	 			html = handler.processPage("", "", "file-list.vm");
			} else if (path.endsWith("Logout")) {
				String name = credentials.getOwner();
		      	credentialCache.removeCredential(name);
		        HttpSession session = request.getSession(true);
		        session.removeAttribute(BaseViewHandler.USER_SESSION_OBJECT);
				sendRedirect("","",response);
				return;
			} else if (path.equals("IBISServerClient.zip")) {
				//TODO: this is a specialized path; could be generalized
				File f = model.getBinaryFile("IBISServerClient.zip");
				environment.logDebug("FileViewHandler.doBrowser-2 "+f);
				sendBinary(f,response);
				return;
			} else if (path.equals("IBIS-Format.pdf")) {
				//TODO: this is a specialized path; could be generalized
				File f = model.getBinaryFile("IBIS-Format.pdf");
				environment.logDebug("FileViewHandler.doBrowser-3 "+f);
				sendBinary(f,response);
				return;
	 		} else if (path.startsWith("get")) {
	 			//get/<platform>/<filename>
	 			//e.g. http://localhost:8080/get/cm/ggw-3.xml
	 			path = path.substring("get/".length());
				environment.logDebug("FileViewHandler.doBrowser-3 "+path);
	 			platform = getPlatform(path);
	 			path = path.substring((platform+"/").length());
	 			filename = getFilename(path);
	 			environment.logDebug("FileViewHandler.doBrowser-4 "+platform+" "+filename);
	 			html = model.getFile(platform, filename);
	 		} else if (path.startsWith("load")) {
	 			html = handler.processPage("", "", "file-form.vm");
	 		} else if (path.endsWith("Login") || path.equalsIgnoreCase("Login")) {
				html = handler.processPage("", "", "login-form.vm");
				handler.clearContext();
				setHeader(handler,"Please login");
			}

 		} catch (Exception e) {
 			environment.logError("FileViewHandler.doBrowser error "+e.getMessage(),e);
 			throw new ServletException(e);
 		}
		setContent(handler,html);
		paintFile(handler,response);
    }
    
    void sendBinary(File f, HttpServletResponse resp) throws Exception {
	    OutputStream output = resp.getOutputStream();
	    InputStream input = new BufferedInputStream(new FileInputStream(f));
	    int c;
	    while ((c = input.read()) > -1)
	    	output.write(c);
	    output.flush();
	    output.close();
    }
    
    /**
     * <platform>/<filename>
     * @param in
     * @return
     */
    private String getPlatform(String in) throws IBISException {
    	String result = in;
    	int len = platforms.length;
    	for (int i=0;i<len;i++)
    		if (in.startsWith(platforms[i]))
    			return platforms[i];
    	throw new IBISException("FileViewHandler.getPlatform missing platform: "+in);
    }
    private String getFilename(String in) {
    	String result = in;
    	if (result.endsWith("/"))
    		result = result.substring(0,result.lastIndexOf('/'));
    	return result;
    }
}
