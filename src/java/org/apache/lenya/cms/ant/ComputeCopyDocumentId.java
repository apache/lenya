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

/* $Id: ComputeCopyDocumentId.java,v 1.7 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import java.util.StringTokenizer;


/**
 * Sets the property "newdocumentid" in the project to the value of the computed 
 * unique document id (document id of the parent + last token of the id of 
 * the source)
 * Used by Copy and Move
 */
public class ComputeCopyDocumentId extends ComputeNewDocumentId {

    /**
     * Creates a new instance of ComputeCopyDocumentId
     */
    public ComputeCopyDocumentId() {
        super();
    }

    /**
     * Computes the document id for the destination:
     * new documentid = document id of the parent + last token of the id
     * of the source
     * @param firstdocumentid  The document id of the source.
     * @param secdocumentid  The document id of the parent of the destination.
     * @return String The document id of the destination.
     */
	protected String compute(String firstdocumentid, String secdocumentid) {
		StringTokenizer st = new StringTokenizer(firstdocumentid, "/");
		int l = st.countTokens();

		for (int i = 1; i < l; i++) {
			st.nextToken();
		}
        if (secdocumentid.endsWith("/")) {
            secdocumentid = secdocumentid + st.nextToken();
        } else {
    		secdocumentid = secdocumentid + "/"  + st.nextToken();
        }
        
		return secdocumentid;
	}

}
