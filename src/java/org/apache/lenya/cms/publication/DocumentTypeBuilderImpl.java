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

package org.apache.lenya.cms.publication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.lenya.cms.authoring.DefaultBranchCreator;
import org.apache.lenya.cms.authoring.NodeCreatorInterface;
import org.xml.sax.SAXException;

/**
 * Avalon Service for building an instance of {@link DocumentType} using information
 * from the <code>doctypes.xconf</code> configuration file.
 * 
 * <p>
 * Since this service is very frequently used, it is implemented as a poolable service.
 * </p> 
 * <p>
 * Furthermore, the instances are cached, to avoid re-reading configuration unless the
 * configuration file has changed.
 * </p>
 * 
 * @version $Id$
 */
public final class DocumentTypeBuilderImpl extends AbstractLogEnabled implements
        DocumentTypeBuilder, Serviceable, Poolable {

    /** Creates a new instance of DocumentTypeBuilder */
    public DocumentTypeBuilderImpl() {
        // do nothing
    }

    /**
     * The default document types configuration directory, relative to the publication directory.
     */
    private static final String DOCTYPE_DIRECTORY = "config/doctypes".replace('/',
            File.separatorChar);

    /**
     * The location of sample contents for this resource type. Note that this need not be a file.
     */
    private static final String DOCTYPE_SAMPLES = "config/doctypes/samples/";

    /**
     * <code>CONFIG_FILE</code> The default document types configuration file, relative to the
     * publication directory.
     */
    public static final String CONFIG_FILE = "doctypes.xconf";
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
     * <code>SCHEMA_ELEMENT</code> The RelaxNG schema element
     */
    public static final String SCHEMA_ELEMENT = "schema";
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
     * <code>SAMPLE_NAME</code> The sample name parameter
     */
    public static final String SAMPLE_NAME = "sample-name";

    /**
     * Builds an instance of document type for a given resource type name.
     * 
     * @param name The name of the resource type
     * @param publication The publication the document type belongs to.
     * @return A document type object.
     * @throws DocumentTypeBuildException When something went wrong.
     */
    public DocumentType buildDocumentType(String name, Publication publication)
            throws DocumentTypeBuildException {

        if (publication == null)
            throw new DocumentTypeBuildException("illegal usage, publication is null");

        // see if configuration has changed since last load.
        // if it has, do not use cache.
        boolean useCache = isEntryUptodate(confLastModifiedCache,
                publication.getId(),
                getDocTypeConfigFile(publication).lastModified());

        // this will refer to the returned instance
        DocumentType type = null;

        if (useCache) {
            // try to get an instance from cache
            type = docTypeCache.get(publication.getId(), name);
        }

        if (getLogger().isDebugEnabled())
            getLogger().debug("buildDocumentType() called with name [" + name
                    + "], publication.getId [" + publication.getId() + "], lookInCache ["
                    + useCache + "], is in cache [" + (type != null) + "]");

        if (type == null) {

            // create a new instance of DocumentType
            type = new DocumentType(name, getLogger());

            try {
                // retrieve configuration for this publication
                Configuration configuration = getDocTypeConfiguration(publication);

                // now get configuration for this resource type
                Configuration[] doctypeConfigurations = configuration.getChildren(DOCTYPE_ELEMENT);

                Configuration doctypeConf = null;

                for (int i = 0; i < doctypeConfigurations.length; i++) {
                    if (doctypeConfigurations[i].getAttribute(TYPE_ATTRIBUTE).equals(name)) {
                        doctypeConf = doctypeConfigurations[i];
                        break;
                    }
                }

                if (doctypeConf == null) {
                    throw new DocumentTypeBuildException("No definition found for doctype '" + name);
                }

                Configuration schemaConf = doctypeConf.getChild(SCHEMA_ELEMENT, false);

                if (schemaConf != null) {
                    String schemaFileName = schemaConf.getAttribute(SRC_ATTRIBUTE);
                    type.setSchemaDefinition(schemaFileName);
                }

                NodeCreatorInterface creator;
                Configuration creatorConf = doctypeConf.getChild(CREATOR_ELEMENT, false);

                if (creatorConf != null) {
                    String creatorClassName = creatorConf.getAttribute(SRC_ATTRIBUTE);
                    Class creatorClass = Class.forName(creatorClassName);
                    creator = (NodeCreatorInterface) creatorClass.newInstance();
                    creator.init(creatorConf, manager, getLogger());
                } else {
                    creator = new DefaultBranchCreator();
                }

                type.setCreator(creator);

                // determine the sample content location.
                if (creatorConf != null) {
                    Configuration sampleConf = creatorConf.getChild(SAMPLE_NAME, false);
                    if (sampleConf != null) {
                        String sampleLocation = sampleConf.getValue();
                        String pubBase = publication.getSourceURI();
                        type.setSampleContentLocation(pubBase + "/" + DOCTYPE_SAMPLES
                                + sampleLocation);
                    }
                }

                Configuration workflowConf = doctypeConf.getChild(WORKFLOW_ELEMENT, false);

                if (workflowConf != null) {
                    String workflowFileName = workflowConf.getAttribute(SRC_ATTRIBUTE);
                    type.setWorkflowFileName(workflowFileName);
                }

                Configuration[] rewriteAttributeConfigs = doctypeConf
                        .getChildren(ELEMENT_REWRITE_ATTRIBUTE);
                List xPaths = new ArrayList();
                for (int i = 0; i < rewriteAttributeConfigs.length; i++) {
                    String xPath = rewriteAttributeConfigs[i].getAttribute(ATTRIBUTE_XPATH);
                    xPaths.add(xPath);
                }
                String[] xPathArray = (String[]) xPaths.toArray(new String[xPaths.size()]);
                type.setLinkAttributeXPaths(xPathArray);

                docTypeCache.add(publication.getId(), name, type);

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
        }

        return type;
    }

    // for each publication key, stores a hash of docType objects
    private DocTypeCache docTypeCache = new DocTypeCache();

    private Hashtable confCache = new Hashtable();
    private Hashtable confLastModifiedCache = new Hashtable();

    private Configuration getDocTypeConfiguration(Publication _publication)
            throws DocumentTypeBuildException, SAXException, IOException, ConfigurationException {

        Object conf = confCache.get(_publication.getId());

        if (getLogger().isDebugEnabled())
            getLogger().debug("getDocTypeConfiguration() for publication [" + _publication.getId()
                    + "], conf in cache ? " + (conf != null));

        File configFile = getDocTypeConfigFile(_publication);
        if (!configFile.exists())
            throw new DocumentTypeBuildException("configuration file for publication ["
                    + _publication.getId() + "] does not exist");

        if (conf == null
                || !isEntryUptodate(confLastModifiedCache, _publication.getId(), configFile
                        .lastModified())) {

            // load / reload the configuration from file
            if (getLogger().isDebugEnabled())
                getLogger()
                        .debug("getDocTypeConfiguration() reloading configuration for publication ["
                                + _publication.getId() + "]");

            conf = new DefaultConfigurationBuilder().buildFromFile(configFile);

            // put in cache
            confCache.put(_publication.getId(), conf);
            confLastModifiedCache.put(_publication.getId(), new Long(configFile.lastModified()));
        }

        return (Configuration) conf;
    }

    private boolean isEntryUptodate(Hashtable entryTimestamps, String key, long timestamp) {

        boolean isUptodate = false;
        Object lastModifiedEntry = entryTimestamps.get(key);
        if (lastModifiedEntry != null) {
            long oldTimestamp = ((Long) lastModifiedEntry).longValue();
            if (timestamp <= oldTimestamp)
                isUptodate = true;

            if (getLogger().isDebugEnabled())
                getLogger().debug("isEntryUptodate() called for key [" + key + "] and timestamp ["
                        + timestamp + "], current timestamp is [" + timestamp + "], returning "
                        + isUptodate);

        } else {
            if (getLogger().isDebugEnabled())
                getLogger().debug("isEntryUptodate() has no previous entry, returning ["
                        + isUptodate);
        }

        return isUptodate;
    }

    /**
     * @param _publication The publication.
     * @return The doctype configuration file.
     * @throws DocumentTypeBuildException if the configuration file does not exist.
     */
    private File getDocTypeConfigFile(Publication _publication) throws DocumentTypeBuildException {

        File configDirectory = new File(_publication.getDirectory(), DOCTYPE_DIRECTORY);
        File ret = new File(configDirectory, CONFIG_FILE);
        if (!ret.exists())
            throw new DocumentTypeBuildException(
                    "Resource types configuration file for publication " + _publication.getId()
                            + " does not exist; should be located at [" + ret.getPath() + "]");

        return ret;
    }

    protected ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    private class DocTypeCache {

        // for each publication key, stores a hash of docType objects
        private Hashtable cache = new Hashtable();

        void add(String publicationId, String docTypeName, DocumentType type) {
            Object docTypes = cache.get(publicationId);
            if (docTypes == null) {
                Hashtable docTypesTable = new Hashtable();
                docTypesTable.put(docTypeName, type);
                cache.put(publicationId, docTypesTable);
            } else {
                Hashtable docTypesTable = (Hashtable) docTypes;
                docTypesTable.put(docTypeName, type);
            }
        }

        DocumentType get(String publicationId, String docTypeName) {
            DocumentType ret = null;
            Object docTypes = cache.get(publicationId);
            if (docTypes != null) {
                Hashtable docTypesTable = (Hashtable) docTypes;
                ret = (DocumentType) docTypesTable.get(docTypeName);
            }
            return ret;
        }
    }

}
