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

/* $Id$  */

package org.apache.lenya.cms.authoring;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 * The default creator
 */
public class DefaultCreator implements ParentChildCreatorInterface {
    private static Logger log = Logger.getLogger(DefaultCreator.class);
    /**
     * <code>RESOURCE_NAME</code> The resource name parameter
     */
    public static final String RESOURCE_NAME = "resource-name";
    /**
     * <code>RESOURCE_META_NAME</code> The resource meta name parameter
     */
    public static final String RESOURCE_META_NAME = "resource-meta-name";
    /**
     * <code>SAMPLE_NAME</code> The sample name parameter
     */
    public static final String SAMPLE_NAME = "sample-name";
    /**
     * <code>SAMPLE_META_NAME</code> The sample meta name parameter
     */
    public static final String SAMPLE_META_NAME = "sample-meta-name";

    private String resourceName = null;
    private String resourceMetaName = null;
    private String sampleResourceName = null;
    private String sampleMetaName = null;

    /**
     * @see org.apache.lenya.cms.authoring.ParentChildCreatorInterface#init(Configuration)
     */
    public void init(Configuration conf) {
        if (conf == null) {
            return;
        }

        if (conf.getChild(RESOURCE_NAME, false) != null) {
            this.resourceName = conf.getChild(RESOURCE_NAME).getValue("index.xml");
        }

        if (conf.getChild(RESOURCE_META_NAME, false) != null) {
            this.resourceMetaName =
                conf.getChild(RESOURCE_META_NAME).getValue("index-meta.xml");
        }

        if (conf.getChild(SAMPLE_NAME, false) != null) {
            this.sampleResourceName =
                conf.getChild(SAMPLE_NAME).getValue("sampleindex.xml");
        }

        if (conf.getChild(SAMPLE_META_NAME, false) != null) {
            this.sampleMetaName =
                conf.getChild(SAMPLE_META_NAME).getValue("samplemeta.xml");
        }
    }

    /**
     * Generate a tree id by returning the child ID.
     * @param childId a <code>String</code> value
     * @param childType a <code>short</code> value
     * @return a <code>String</code> value
     * @exception Exception if an error occurs
     */
    public String generateTreeId(String childId, short childType)
        throws Exception {
        return childId;
    }

    /**
     * Return the child type by simply returning the child type.
     * @param childType a <code>short</code> value
     * @return a <code>short</code> value
     * @exception Exception if an error occurs
     */
    public short getChildType(short childType) throws Exception {
        return childType;
    }

    /**
     * Create Child Name for tree entry
     * @param childname a <code>String</code> value
     * @return a <code>String</code> for Child Name for tree entry
     * @exception Exception if an error occurs
     */
    public String getChildName(String childname) throws Exception {
        if (childname.length() != 0) {
            return childname;
        }
        return "abstract_default";
    }

    /**
     * @see org.apache.lenya.cms.authoring.ParentChildCreatorInterface#create(File, File,
     * String, short, String, String, Map)
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

        String doctypeSample = samplesDir + File.separator + this.sampleResourceName;
        String doctypeMeta = samplesDir + File.separator + this.sampleMetaName;

        File sampleFile = new File(doctypeSample);
        if (!sampleFile.exists()) {
            log.error("No such sample file: " + sampleFile + " Have you configured the sample within doctypes.xconf?");
            throw new FileNotFoundException("" + sampleFile);
        }

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
        if (this.sampleMetaName != null) {
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
     * @param doc the xml document
     * @param childId the id of the child
     * @param childType the type of child
     * @param childName the name of the child
     * @param parameters additional parameters that can be used in the transformation
     * @throws Exception if the transformation fails
     */
    protected void transformXML(
        Document doc,
        String childId,
        short childType,
        String childName,
        Map parameters)
        throws Exception {
	    // do nothing
    }

    /**
     * Apply some transformation on the meta file of newly created child.
     * @param doc the xml document
     * @param childId the id of the child
     * @param childType the type of child
     * @param childName the name of the child
     * @param parameters additional parameters that can be used in the transformation
     * @throws Exception if the transformation fails
     */
    protected void transformMetaXML(
        Document doc,
        String childId,
        short childType,
        String childName,
        Map parameters)
        throws Exception {
	    // do nothing
        }

    /**
     * Get the file name of the child
     * @param parentDir the parent directory
     * @param childId the id of the child
     * @param language for which the document is created
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
     * @param parentDir the parent directory
     * @param childId the id of the child
     * @param language for which the document is created
     * @return the name of the meta file
     */
    protected String getChildMetaFileName(
        File parentDir,
        String childId,
        String language) {
        return null;
    }

    /**
     * Create the language suffix for a file name given a language string
     * @param language the language
     * @return the suffix for the language dependant file name
     */
    protected String getLanguageSuffix(String language) {
        return (language != null) ? "_" + language : "";
    }
}
