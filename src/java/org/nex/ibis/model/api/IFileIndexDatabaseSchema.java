package org.nex.ibis.model.api;

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
public interface IFileIndexDatabaseSchema {
	/**
	 * When used with Derby, "drop if exists" is not valid.
	 * User must delete the tables first; Derby doesn't close with a ";"
	 */
	public static final String [] FILE_INDEX_SCHEMA = { 
		/* "drop table if exists FILE_INDEX;", */
        "create table FILE_INDEX ("+
        	"filePath varchar(255) not null,"+
        	"platform varchar(32),"+
        	"description varchar(255) default '',"+
        	//userLocator ensures safety for removing files
        	"userLocator varchar(255) not null,"+
        	"unique(filePath))",
        	"create index MyFilePathIndex on FILE_INDEX(filePath)",
        	"create index MyFileDescriptionIndex on FILE_INDEX(description)"
    };
	
	public static final String getFilePath = 
		"select filePath from FILE_INDEX where platform=? and description=?";
	public static final String listAllFiles =
		"select * from FILE_INDEX";
	public static final String listSomeFiles =
		"select * from FILE_INDEX where platform=?";
	public static final String removeFile =
		"delete from FILE_INDEX where filePath=? and platform=? and userLocator=?";
	public static final String insertFile =
		"insert into FILE_INDEX (filePath,platform,description,userLocator) values(?,?,?,?)";
}
