/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id: TwoDocumentsOperationTask.java,v 1.2 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import org.apache.tools.ant.BuildException;

public abstract class TwoDocumentsOperationTask extends DocumentOperationTask {

	private String secarea;
	private String secdocumentid;

	/**
	 * 
	 */
	public TwoDocumentsOperationTask() {
		super();
	}

   /**
	 * @return String The area of the destination.
	 */
	public String getSecarea() {
		return secarea;
	}

	/**
	 * @return String The document-id corresponding to the destination.
	 */
	public String getSecdocumentid() {
		return secdocumentid;
	}

	/**
	 * @param string The area of the destination.
	 */
	public void setSecarea(String string) {
		secarea = string;
	}

	/**
	 * @param string The document-id corresponding to the destination.
	 */
	public void setSecdocumentid(String string) {
		secdocumentid = string;
	}

	/** 
	 * @see org.apache.tools.ant.Task#execute()
	 **/

	public void execute() throws BuildException {
		log("document-id for the destination" + this.getSecdocumentid());
		log("area for the destination" + this.getSecarea());
		super.execute();
	}

}
