/*
$Id: DefaultCreator.java,v 1.22 2003/07/30 15:30:06 egli Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.authoring;

import org.apache.avalon.framework.configuration.Configuration;

import org.apache.lenya.xml.DocumentHelper;

import org.apache.log4j.Category;

import org.w3c.dom.Document;

import java.io.File;

import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author <a href="mailto:bigalke@geophysik.uni-frankfurt.de">Juergen Bigalke</a>
 */
public class DefaultCreator implements ParentChildCreatorInterface {
    private static Category log = Category.getInstance(DefaultCreator.class);
    public static final String RESOURCE_NAME = "resource-name";
    public static final String RESOURCE_META_NAME = "resource-meta-name";
    public static final String SAMPLE_NAME = "sample-name";
    public static final String SAMPLE_META_NAME = "sample-meta-name";

    private String resourceName = null;
    private String resourceMetaName = null;
    private String sampleResourceName = null;
    private String sampleMetaName = null;

    /**
     * DOCUMENT ME!
     *
     * @param conf DOCUMENT ME!
     */
    public void init(Configuration conf) {
        if (conf == null) {
            return;
        }

        if (conf.getChild(RESOURCE_NAME, false) != null) {
            resourceName = conf.getChild(RESOURCE_NAME).getValue("index.xml");
        }

        if (conf.getChild(RESOURCE_META_NAME, false) != null) {
            resourceMetaName =
                conf.getChild(RESOURCE_META_NAME).getValue("index-meta.xml");
        }

        if (conf.getChild(SAMPLE_NAME, false) != null) {
            sampleResourceName =
                conf.getChild(SAMPLE_NAME).getValue("sampleindex.xml");
        }

        if (conf.getChild(SAMPLE_META_NAME, false) != null) {
            sampleMetaName =
                conf.getChild(SAMPLE_META_NAME).getValue("samplemeta.xml");
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
      * @param childName the name of the child
      * @param language for which the document is created
      * @param parameters additional parameters that can be considered when 
      *  creating the child
      *
      * @throws Exception DOCUMENT ME!
      */
    public void create(
        File samplesDir,
        File parentDir,
        String childId,
        short childType,
        String childName,
        String language,
        Map parameters)
        throws Exception {
        // Set filenames
        String id = generateTreeId(childId, childType);
        String filename = getChildFileName(parentDir, id, language);
        String filenameMeta = getChildMetaFileName(parentDir, id, language);

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

    /**
     * Apply some transformation on the newly created child.
     * 
     * @param doc the xml document
     * @param childId the id of the child
     * @param childType the type of child
     * @param childName the name of the child
     * @param parameters additional parameters that can be used in the transformation
     * 
     * @throws Exception if the transformation fails
     */
    protected void transformXML(
        Document doc,
        String childId,
        short childType,
        String childName,
        Map parameters)
        throws Exception {}

    /**
     * Apply some transformation on the meta file of newly created child.
     * 
     * @param doc the xml document
     * @param childId the id of the child
     * @param childType the type of child
     * @param childName the name of the child
     * @param parameters additional parameters that can be used in the transformation
     * 
     * @throws Exception if the transformation fails
     */
    protected void transformMetaXML(
        Document doc,
        String childId,
        short childType,
        String childName,
        Map parameters)
        throws Exception {}

    /**
     * Get the file name of the child
     * 
     * @param parentDir the parent directory
     * @param childId the id of the child
     * @param language for which the document is created
     * 
     * @return the file name of the child
     */
    protected String getChildFileName(
        File parentDir,
        String childId,
        String language) {
        return null;
    }

    /**
     * Get the file name of the meta file
     * 
     * @param parentDir the parent directory
     * @param childId the id of the child
     * @param language for which the document is created
     * 
     * @return the name of the meta file
     */
    protected String getChildMetaFileName(
        File parentDir,
        String childId,
        String language) {
        return null;
    }
}
