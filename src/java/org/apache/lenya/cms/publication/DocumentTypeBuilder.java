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

package org.apache.lenya.cms.publication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.lenya.cms.authoring.ParentChildCreatorInterface;
import org.xml.sax.SAXException;


/**
 * A builder for document types.
 */
public final class DocumentTypeBuilder {
    /** Creates a new instance of DocumentTypeBuilder */
    private DocumentTypeBuilder() {
	    // do nothing
    }

    /**
     * The default document types configuration directory, relative to the publication directory.
     */
    public static final String DOCTYPE_DIRECTORY = "config/doctypes".replace('/', File.separatorChar);

    /**
     * <code>CONFIG_FILE</code> The default document types configuration file, relative to the publication directory.
     */
    public static final String CONFIG_FILE = "doctypes.xconf".replace('/', File.separatorChar);
    /**
     * <code>DOCTYPES_ELEMENT</code> The doctypes element
     */
    public static final String DOCTYPES_ELEMENT = "doctypes";
    /**
     * <code>DOCTYPE_ELEMENT</code> The doctype element
     */
    public static final String DOCTYPE_ELEMENT = "doc";
    /**
     * <code>TYPE_ATTRIBUTE</code> The type attribute
     */
    public static final String TYPE_ATTRIBUTE = "type";
    /**
     * <code>CREATOR_ELEMENT</code> The creator element
     */
    public static final String CREATOR_ELEMENT = "creator";
    /**
     * <code>SRC_ATTRIBUTE</code> The src attribute
     */
    public static final String SRC_ATTRIBUTE = "src";
    /**
     * <code>WORKFLOW_ELEMENT</code> The workflow element
     */
    public static final String WORKFLOW_ELEMENT = "workflow";
    /**
     * <code>ELEMENT_REWRITE_ATTRIBUTE</code> The link-attribute element.
     */
    public static final String ELEMENT_REWRITE_ATTRIBUTE = "link-attribute";
    /**
     * <code>ATTRIBUTE_XPATH</code> The xpath attribute.
     */
    public static final String ATTRIBUTE_XPATH = "xpath";

    /**
     * Builds a document type for a given name.
     * @param name A string value.
     * @param publication The publication the document type belongs to.
     * @return A document type object.
     * @throws DocumentTypeBuildException When something went wrong.
     */
    public static DocumentType buildDocumentType(String name, Publication publication)
        throws DocumentTypeBuildException {
        DocumentType type = new DocumentType(name);

        File configDirectory = new File(publication.getDirectory(), DOCTYPE_DIRECTORY);
        File configFile = new File(configDirectory, CONFIG_FILE);

        try {
            Configuration configuration = new DefaultConfigurationBuilder().buildFromFile(configFile);

            Configuration[] doctypeConfigurations = configuration.getChildren(DOCTYPE_ELEMENT);
            Configuration doctypeConf = null;

            for (int i = 0; i < doctypeConfigurations.length; i++) {
                if (doctypeConfigurations[i].getAttribute(TYPE_ATTRIBUTE).equals(name)) {
                    doctypeConf = doctypeConfigurations[i];
                }
            }

            if (doctypeConf == null) {
                throw new DocumentTypeBuildException("No definition found for doctype '" + name +
                    "'!");
            }

            ParentChildCreatorInterface creator;
            Configuration creatorConf = doctypeConf.getChild(CREATOR_ELEMENT, false);

            if (creatorConf != null) {
                String creatorClassName = creatorConf.getAttribute(SRC_ATTRIBUTE);
                Class creatorClass = Class.forName(creatorClassName);
                creator = (ParentChildCreatorInterface) creatorClass.newInstance();
                creator.init(creatorConf);
            } else {
                creator = new org.apache.lenya.cms.authoring.DefaultBranchCreator();
            }

            type.setCreator(creator);

            Configuration workflowConf = doctypeConf.getChild(WORKFLOW_ELEMENT, false);

            if (workflowConf != null) {
                String workflowFileName = workflowConf.getAttribute(SRC_ATTRIBUTE);
                type.setWorkflowFileName(workflowFileName);
            }
            
            Configuration[] rewriteAttributeConfigs = doctypeConf.getChildren(ELEMENT_REWRITE_ATTRIBUTE);
            List xPaths = new ArrayList();
            for (int i = 0; i < rewriteAttributeConfigs.length; i++) {
                String xPath = rewriteAttributeConfigs[i].getAttribute(ATTRIBUTE_XPATH);
                xPaths.add(xPath);
            }
            String[] xPathArray = (String[]) xPaths.toArray(new String[xPaths.size()]);
            type.setLinkAttributeXPaths(xPathArray);
            
        } catch (final ConfigurationException e) {
            throw new DocumentTypeBuildException(e);
        } catch (final SAXException e) {
            throw new DocumentTypeBuildException(e);
        } catch (final IOException e) {
            throw new DocumentTypeBuildException(e);
        } catch (final DocumentTypeBuildException e) {
            throw new DocumentTypeBuildException(e);
        } catch (final ClassNotFoundException e) {
            throw new DocumentTypeBuildException(e);
        } catch (final InstantiationException e) {
            throw new DocumentTypeBuildException(e);
        } catch (final IllegalAccessException e) {
            throw new DocumentTypeBuildException(e);
        }

        return type;
    }
}
