/*
 * $Id: DefaultCreator.java,v 1.14 2003/03/06 20:45:41 gregor Exp $
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
package org.lenya.cms.authoring;

import org.w3c.dom.Document;

import org.apache.avalon.framework.configuration.Configuration;

import org.apache.log4j.Category;

import org.lenya.xml.DocumentHelper;
import org.lenya.xml.DOMWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author <a href="mailto:juergen.bigalke@lenya.org">Juergen Bigalke</a>
 */
public class DefaultCreator implements ParentChildCreatorInterface {
	static Category log = Category.getInstance(DefaultCreator.class);

    protected String resourceName;
    protected String resourceMetaName;
    protected String sampleResourceName;
    protected String sampleMetaName;

    /**
     * DOCUMENT ME!
     *
     * @param creatorNode DOCUMENT ME!
     */
    public void init(Configuration conf) {
        if (conf == null) {
            return;
        }

	resourceName = conf.getChild("resource-name").getValue("index.xml");
	resourceMetaName = conf.getChild("resource-meta-name").getValue("index-meta.xml");
	sampleResourceName = conf.getChild("sample-name").getValue("sampleindex.xml");
	sampleMetaName = conf.getChild("sample-meta-name").getValue("samplemeta.xml");
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
        String filenameMeta = getCildMetaFileName(parentDir, id);

        String doctypeSample = samplesDir + File.separator + sampleResourceName;
        String doctypeMeta = samplesDir + File.separator + sampleMetaName;

	// Read sample file
        log.debug("Read sample file: " + doctypeSample);
        Document doc = DocumentHelper.readDocument(new URL("file:" + doctypeSample));

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
        FileOutputStream out = new FileOutputStream(filename);
        new DOMWriter(out).printWithoutFormatting(doc);
        out.close();

	// now do the same thing for the meta document if the
	// sampleMetaName is specified
	if (sampleMetaName != null) {
	    doc = DocumentHelper.readDocument(new URL("file:" + doctypeMeta));

	    transformMetaXML(doc, id, childType, childName, parameters);
	    
	    parent = new File(new File(filenameMeta).getParent());
	    if (!parent.exists()) {
		parent.mkdirs();
	    }
	    
	    out = new FileOutputStream(filenameMeta);
	    new DOMWriter(out).printWithoutFormatting(doc);
	    out.close();
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

    protected String getCildMetaFileName(File parentDir, String childId) {
        return null;
    }
}
