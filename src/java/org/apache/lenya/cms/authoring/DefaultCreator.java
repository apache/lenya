/*
 * Copyright  1999-2005 The Apache Software Foundation
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

package org.apache.lenya.cms.authoring;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Document;

/**
 * The default creator for documents
 * @version $Id$
 */
public class DefaultCreator extends AbstractLogEnabled implements ParentChildCreatorInterface  {

    private String sampleResourceName = null;

    /**
     * @see org.apache.lenya.cms.authoring.ParentChildCreatorInterface#init(Configuration, Logger)
     */
    public void init(Configuration conf, Logger _logger) {
        ContainerUtil.enableLogging(this, _logger);
        // nothing to configure in current implementation
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
     * @see org.apache.lenya.cms.authoring.ParentChildCreatorInterface#create(String, File,
     * String, short, String, String, Map)
      */
    public void create(
        String samplesLocation,
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

        if (getLogger().isDebugEnabled())
            getLogger().debug("DefaultCreator.create(), ready to read sample contents, samplesLocation [" + samplesLocation + "]");

        File sampleFile = null;
        if (samplesLocation != null) {
            sampleFile = new File(samplesLocation.replace('/', File.separatorChar));
            if (!sampleFile.exists())
               throw new FileNotFoundException("Sample file [" + sampleFile + "] not found, make sure you configured it within doctypes.xconf");
        }
        else 
            throw new Exception("sample configuration setup error, samplesLocation is not set - verify your doctypes.xconf");


        // Read sample file
        Document doc = DocumentHelper.readDocument(sampleFile);

        if (getLogger().isDebugEnabled())
            getLogger().debug("sample document: " + doc);

        if (getLogger().isDebugEnabled())
            getLogger().debug("transform sample file: ");

        // transform the xml if needed
        transformXML(doc, id, childType, childName, parameters);

        // write the document (create the path, i.e. the parent
        // directory first if needed)
        File parent = new File(new File(filename).getParent());

        if (!parent.exists()) {
            parent.mkdirs();
        }

        if (getLogger().isDebugEnabled())
            getLogger().debug("write file: " + filename);

        // Write file
        DocumentHelper.writeDocument(doc, new File(filename));

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
