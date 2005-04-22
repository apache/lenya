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

/* $Id$  */

package org.apache.lenya.cms.publication;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;

import org.apache.lenya.cms.authoring.ParentChildCreatorInterface;


/**
 * A document type.
 */
public class DocumentType extends AbstractLogEnabled {
    /**
     * <code>NAMESPACE</code> The doctypes namespace
     */
    public static final String NAMESPACE = "http://apache.org/cocoon/lenya/doctypes/1.0";
    /**
     * <code>DEFAULT_PREFIX</code> The doctypes namespace prefix
     */
    public static final String DEFAULT_PREFIX = "dt";

    /** Creates a new instance of DocumentType
     * 
     * @param _name the name of the document type
     * @param _logger a logger
     */
    protected DocumentType(String _name, Logger _logger) {
        assert _name != null;

        ContainerUtil.enableLogging(this, _logger);
        this.name = _name;

        if (getLogger().isDebugEnabled())
            getLogger().debug("DocumentType() constructed with"
              + "\n\t name [" + name + "]"
		);
    }

    private String name;

    /**
    * Returns the name of this document type.
     * @return A string value.
     */
    public String getName() {
        return this.name;
    }

    private ParentChildCreatorInterface creator = null;

	/**
	 * Get the creator for this document type.
	 * @return a <code>ParentChildCreatorInterface</code>
	 */
    public ParentChildCreatorInterface getCreator() {
        return this.creator;
    }

	/**
	 * Set the creator
	 * @param _creator a <code>ParentChildCreatorInterface</code>
	 */
    protected void setCreator(ParentChildCreatorInterface _creator) {
        assert _creator != null;
        this.creator = _creator;
    }

    private String workflowFile = null;

    /**
     * Returns if this document type has a workflow definition.
     * @return A boolean value.
     */
    public boolean hasWorkflow() {
        return this.workflowFile != null;
    }

	/**
	 * Get the file name of the workflow file.
	 * @return a <code>String</code>
	 * @throws DocumentTypeBuildException if the document type has no workflow
	 */
    public String getWorkflowFileName() throws DocumentTypeBuildException {
        if (!hasWorkflow()) {
            throw new DocumentTypeBuildException("The document type '" + getName() +
                "' has no workflow!");
        }
        return this.workflowFile;
    }

	/**
	 * Set the file name of the workflow file.
	 * @param string the new file name
	 */
    public void setWorkflowFileName(String string) {
        assert string != null;
        this.workflowFile = string;
    }

    private String[] linkAttributeXPaths = { };

    /**
     * Returns an array of XPaths representing attributes to be rewritten
     * when a document URL has changed.
     * @return An array of strings.
     */
    public String[] getLinkAttributeXPaths() {
        return this.linkAttributeXPaths;
    }
    
    /**
     * Sets the link attribute XPath values.
     * @param xPaths An array of strings.
     */
    public void setLinkAttributeXPaths(String[] xPaths) {
        this.linkAttributeXPaths = xPaths;
    }


    private String sampleContentLocation;

    /**
    * Returns the location of sample contents for this type
     * @return A string value.
     */
    public String getSampleContentLocation() {
        return this.sampleContentLocation;
    }

    /**
    * Set the location of sample contents for this type
     * @param _location the location
     */
    public void setSampleContentLocation(String _location) {
        this.sampleContentLocation = _location;
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getName();
    }


}
