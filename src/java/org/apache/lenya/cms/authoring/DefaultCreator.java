/*
 * $Id: DefaultCreator.java,v 1.18 2003/05/08 15:27:26 egli Exp $
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
package org.apache.lenya.cms.authoring;

import org.w3c.dom.Document;

import org.apache.avalon.framework.configuration.Configuration;

import org.apache.log4j.Category;

import org.apache.lenya.xml.DocumentHelper;

import java.io.File;
import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author <a href="mailto:juergen.bigalke@lenya.org">Juergen Bigalke</a>
 */
public class DefaultCreator implements ParentChildCreatorInterface {
	static Category log = Category.getInstance(DefaultCreator.class);

    protected final String RESOURCE_NAME = "resource-name";
    protected final String RESOURCE_META_NAME = "resource-meta-name";
    protected final String SAMPLE_NAME = "sample-name";
    protected final String SAMPLE_META_NAME = "sample-meta-name";

    protected String resourceName = null;
    protected String resourceMetaName = null;
    protected String sampleResourceName = null;
    protected String sampleMetaName = null;

    /**
     * DOCUMENT ME!
     *
     * @param creatorNode DOCUMENT ME!
     */
    public void init(Configuration conf) {
        if (conf == null) {
            return;
        }

	if (conf.getChild(RESOURCE_NAME, false) != null) {
	    resourceName = conf.getChild(RESOURCE_NAME).getValue("index.xml");
	}
	if (conf.getChild(RESOURCE_META_NAME, false) != null) {
	    resourceMetaName  = conf.getChild(RESOURCE_META_NAME).getValue("index-meta.xml");
	}
	if (conf.getChild(SAMPLE_NAME, false) != null) {
	    sampleResourceName  = conf.getChild(SAMPLE_NAME).getValue("sampleindex.xml");
	}
	if (conf.getChild(SAMPLE_META_NAME, false) != null) {
	    sampleMetaName = conf.getChild(SAMPLE_META_NAME).getValue("samplemeta.xml");
	}
    }

	/**
	 * Generate a three id.
	 *
	 * @param childId a <code>String</code> value
	 * @param childType a <code>short</code> value
	 *
	 * @return a <code>String</code> value
	 *
	 * @exception Exception if an error occurs
	 */
	public String generateTreeId(String childId, short childType)
		throws Exception {
		return childId;
	}

	/**
	 * Return the child type.
	 *
	 * @param childType a <code>short</code> value
	 *
	 * @return a <code>short</code> value
	 *
	 * @exception Exception if an error occurs
	 */
	public short getChildType(short childType) throws Exception {
		return childType;
	}

	/**
	 * Create Child Name for tree entry
	 *
	 * @param childname a <code>String</code> value
	 *
	 * @return a <code>String</code> for Child Name for tree entry
	 *
	 * @exception Exception if an error occurs
	 */
	public String getChildName(String childname) throws Exception {
		if (childname.length() != 0) {
			return childname;
		} else {
			return "abstract_default";
		}
	}
	

   /**
     * DOCUMENT ME!
     *
     * @param samplesDir DOCUMENT ME!
     * @param parentDir DOCUMENT ME!
     * @param childId DOCUMENT ME!
     * @param childType DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void create(File samplesDir, File parentDir,
		       String childId, short childType, String childName,
		       Map parameters)
        throws Exception {

        // Set filenames
	String id = generateTreeId(childId, childType);
        String filename = getChildFileName(parentDir, id);
        String filenameMeta = getChildMetaFileName(parentDir, id);

        String doctypeSample = samplesDir + File.separator + sampleResourceName;
        String doctypeMeta = samplesDir + File.separator + sampleMetaName;

	// Read sample file
        log.debug("Read sample file: " + doctypeSample);
        Document doc = DocumentHelper.readDocument(new File(doctypeSample));

        log.debug("sample document: " + doc);
	// transform the xml if needed
        log.debug("transform sample file: ");
	transformXML(doc, id, childType, childName, parameters);
	// write the document (create the path, i.e. the parent
        // directory first if needed)
	File parent = new File(new File(filename).getParent());
	if (!parent.exists()) {
            parent.mkdirs();
        }
	
        // Write file
        log.debug("write file: " + filename);
	DocumentHelper.writeDocument(doc, new File(filename));

	// now do the same thing for the meta document if the
	// sampleMetaName is specified
	if (sampleMetaName != null) {
	    doc = DocumentHelper.readDocument(new File(doctypeMeta));

	    transformMetaXML(doc, id, childType, childName, parameters);
	    
	    parent = new File(new File(filenameMeta).getParent());
	    if (!parent.exists()) {
		parent.mkdirs();
	    }
	    
	    DocumentHelper.writeDocument(doc, new File(filenameMeta));
	}
    }

    protected void transformXML (Document doc,
				 String childId, short childType, String childName,
				 Map parameters) 
    	throws Exception {
    }

    protected void transformMetaXML (Document doc,
				     String childId, short childType, String childName,
				     Map parameters) 
    	throws Exception {
    }

    protected String getChildFileName(File parentDir, String childId) {
		return null;
    }

    protected String getChildMetaFileName(File parentDir, String childId) {
        return null;
    }
}
