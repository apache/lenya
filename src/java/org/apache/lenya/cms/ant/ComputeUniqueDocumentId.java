/*
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.UniqueDocumentId;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

/**
 * ant task to compute a new id for a document 
 * if there is already a node for a document with this id
 * @author edith
 *
 */
public class ComputeUniqueDocumentId extends Task {
	private String absolutetreepath;
	private String documentid;

	/**
	 * Creates a new instance of ComputeUniqueDocumentId
	 */
	public ComputeUniqueDocumentId() {
		super();
	}
	/**
	 * @return absolutetreepath, the absolute path of the tree
	 */
	protected String getAbsolutetreepath() {
		return absolutetreepath;
	}

	/**
	 * set the value of the absolute path of the tree
	 * @param string 
	 */
	public void setAbsolutetreepath(String string) {
		absolutetreepath = string;
	}
	/**
	 * return the document-id corresponding   
	 * @return documentid,
	 */
	protected String getDocumentid() {
		return documentid;
	}

	/**
	 * set the value of the document-id 
	 * @param string
	 */
	public void setDocumentid(String string) {
		documentid = string;
	}

	/**
	 * copy a node of a tree and insert this in the same tree
	 * @param documentid : id of the copied document
	 * @param absolutetreepath : absolute path of the tree
	 */
	public void compute(String documentid, String absolutetreepath) { 

      UniqueDocumentId uniqueDocumentId = new UniqueDocumentId(); 
      documentid=uniqueDocumentId.computeUniqueDocumentId(absolutetreepath, documentid);
         
      Target target=getOwningTarget();
      Project project=target.getProject();
      project.setProperty("node.newdocumentid", documentid);
	}

	/** 
	 * execute the task
	 */
	public void execute() throws BuildException {
		try {
			log("document-id " + getDocumentid());
			log("Absolute Tree Path: " +getAbsolutetreepath());
			compute(getDocumentid(), getAbsolutetreepath());
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

}
