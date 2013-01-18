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
package org.nex.ibis.view.admin;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nex.ibis.model.Environment;
import org.nex.ibis.model.Ticket;
import org.nex.ibis.IBISException;
import org.nex.ibis.model.admin.AdminModel;
import org.nex.ibis.view.BaseViewHandler;
import org.nex.ibis.view.VelocityHandler;


/**
 * 
 * @author Park
 *
 */
public class AdminViewHandler extends BaseViewHandler {
	private AdminModel model;
	
	public AdminViewHandler(Environment e, String velocityPath) throws IBISException {
		super(e, velocityPath);
		model = new AdminModel(environment);
		environment.logDebug("AdminViewHandler started");
	}
    public void doGet(String path, VelocityHandler handler, HttpServletRequest request, HttpServletResponse response, Ticket credentials) throws ServletException, IOException {
    	if (!isAdmin(credentials)) {
    		setContent(handler,"Administrative credentials required");
    		paintFile(handler,response);
    		return;
    	}
 		String html = "Admin Hello:"+path+" | "+getQueryString(request);
 		try {
	 		if (!isNewAccountRequest(request,path,handler,credentials)) {
	 			if (path.equals("ListMembers")) {
	 				handler.put("memberlist", model.listMembers());
	 				html = handler.processPage("", "", "member-list.vm");
	 			}
	 			setContent(handler,html);
	 		} 
 		} catch (IBISException e) {
 			environment.logError("AdminViewHandler.doGet error "+e.getMessage(),e);
 			throw new ServletException(e);
 		}
		paintAdmin(handler,response);
   }
    
    public void doPost(String path, VelocityHandler handler, HttpServletRequest request, HttpServletResponse response, Ticket credentials) throws ServletException, IOException {
    	System.out.println("AdminViewHandler.doPost "+path);
    	if (!isAdmin(credentials)) {
    		setContent(handler,"Administrative credentials required");
    		paintFile(handler,response);
    		return;
    	}
    	try {
	    	if (path.equals("StartNewAccount")) {
				startNewAccount(request);
				sendRedirect("/","",response);
				return;
	    	} else {
	    		setContent(handler, "AdminViewHandler.doPost not sure what to do with path= "+path);
	    		paintAdmin(handler,response);	    		
	    	}
    	} catch (IBISException e) {
 			environment.logError("AdminViewHandler.doPost error "+e.getMessage(),e);
 			throw new ServletException(e);
    	}
    }

	boolean isNewAccountRequest(HttpServletRequest request, String path, VelocityHandler handler, Ticket credentials) throws IBISException {
		environment.logDebug("PortalViewHandler.isNewAccountRequest- |"+path+"|");
		if (path.endsWith("NewAccount") || path.equalsIgnoreCase("NewAccount") || path.indexOf("NewAccount") > -1 ) { //NewAccount
			environment.logDebug("AdminViewHandler.isNewAccountRequest 1");
			String content = handler.processPage("", "", "new-account-form.vm");
			//	smp.logDebug("PortalViewHandler.isNewAccountRequest 5 "+content);
				handler.clearContext();
				//now setup to paint
				setContent(handler,content);
				setHeader(handler,"New Account");

			return true;
		}
		return false;
	}
	void startNewAccount(HttpServletRequest request)
			throws IBISException {
		String login = request.getParameter("login");
		String npwd = request.getParameter("newPassword");
		String cpwd = request.getParameter("confirmPassword");
		if (!npwd.equals(cpwd)) {
			//paintError(handler, "Bad Password");
			throw new IBISException("Bad Password");
		} else {
			model.newUser(login, npwd);
			System.out.println("PortalViewHandler.startNewAccount+");
			//TODO what to paint now?
	}	

}

}
