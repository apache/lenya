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

/* $Id: ComputeArchiveDocumentId.java,v 1.4 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import java.util.StringTokenizer;

/**
 * Sets the property "newdocumentid" in the project to the value of the computed 
 * unique document id (document id of the source with the latest token 
 * replaced by the new name 
 * Used by ArchivDocument
 */
public class ComputeArchiveDocumentId extends ComputeNewDocumentId {

	/**
	 * Creates a new instance of ComputeArchiveDocumentId
	 */
	public ComputeArchiveDocumentId() {
		super();
	}

	/**
	 * Computes the document id for the destination (renamed file):
	 * new documentid = document id of the source, with the latest token 
	 * replaced by the new name
	 * @param firstdocumentid  The document id of the source.
	 * @param secdocumentid  The new name.
	 * @return String The document id of the destination.
	 */
	protected String compute(String firstdocumentid, String secdocumentid) {
		StringTokenizer st = new StringTokenizer(firstdocumentid, "/");
		int l = st.countTokens();
		for (int i = 0; i < l-1; i++) {
			st.nextToken();
		}
		secdocumentid = "/" + st.nextToken();
		return secdocumentid;
	}

}
