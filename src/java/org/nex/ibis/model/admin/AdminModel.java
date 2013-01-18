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
package org.nex.ibis.model.admin;
import java.util.*;

import org.nex.ibis.IBISException;
import org.nex.ibis.model.Environment;
import org.nex.ibis.model.user.UserModel;
import org.nex.ibis.model.api.ISecurity;
/**
 * 
 * @author Park
 *
 */
public class AdminModel {
	private Environment environment;
	private UserModel userModel;

	public AdminModel(Environment e) throws IBISException{
		environment = e;
		userModel = environment.getUserModel();
		environment.logDebug("AdminModel started");
	}

	public void newUser(String login, String password) throws IBISException {
		userModel.createUser(login, password, ISecurity.MEMBER_USER_ROLE);
		environment.logDebug("AdminModel.newUser new User: "+login);
	}
	
	public List<String> listMembers() throws IBISException {
		return userModel.listUserLocators();
	}
}
