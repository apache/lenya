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
package org.apache.lenya.cms.publication;

import org.apache.log4j.Category;

/**
 * class to compute an unique document id for a document, if there is 
 * already a node in the sitetree for a document with this id. It will
 * documentid_"number of version" 
 * @author edith
 *  
 */
public class UniqueDocumentId {
  private static Category log = Category.getInstance(UniqueDocumentId.class);
  
/** compute an unique document id
 * @param absolutetreepath The absolute path of the tree.
 * @param documentid The documentid .
 * @return the unique documentid
 */
public String computeUniqueDocumentId(String absolutetreepath,String documentid){
	DefaultSiteTree tree = null;
	try {
	  tree = new DefaultSiteTree(absolutetreepath);
	  SiteTreeNode node = tree.getNode(documentid);
	  String suffix = null;
      int version = 0;
	  String idwithoutsuffix = null;
	  if (node != null) {
		int l = documentid.length();
		int index=documentid.lastIndexOf("_");
		if ((index < l) & (index > 0)) {
		  suffix = documentid.substring(index);       
		  idwithoutsuffix= documentid.substring(0,index);
		  version = Integer.parseInt(suffix);
		} else { 
			idwithoutsuffix= documentid;
		}
        while (node!=null) {  
          version= version + 1;
		  suffix= (new Integer(version)).toString();
		  documentid= idwithoutsuffix+"_"+suffix;
		  log.debug("version: "+version);  
		  node = tree.getNode(documentid);
        }
	  }
	} catch (Exception e) {
		e.printStackTrace();
	}
	return documentid;	
  }
}
