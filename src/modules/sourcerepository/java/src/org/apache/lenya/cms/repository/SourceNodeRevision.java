/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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
package org.apache.lenya.cms.repository;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.rc.CheckInEntry;
import org.apache.lenya.cms.rc.RCML;
import org.apache.lenya.cms.rc.RCMLEntry;

/**
 * Revision implementation.
 */
public class SourceNodeRevision extends AbstractLogEnabled implements Revision {

    private SourceNode node;
    private int number;
    private ServiceManager manager;
    private long time = -1;
    private String userId;

    /**
     * @param node The node.
     * @param number The revision number.
     * @param manager The service manager.
     * @param logger The logger.
     */
    public SourceNodeRevision(SourceNode node, int number, ServiceManager manager, Logger logger) {
        this.node = node;
        this.number = number;
        this.manager = manager;
        ContainerUtil.enableLogging(this, logger);
    }

    public long getTime() {
        initialize();
        return this.time;
    }

    protected void initialize() {
        try {
            if (this.time == -1) {
                SourceNodeRCML rcml = (SourceNodeRCML) this.node.getRcml();
                Vector entries = rcml.getEntries();
                for (Iterator i = entries.iterator(); i.hasNext();) {
                    RCMLEntry entry = (RCMLEntry) i.next();
                    if (entry.getType() == RCML.ci
                            && ((CheckInEntry) entry).getVersion() == this.number) {
                        this.time = entry.getTime();
                        this.userId = entry.getIdentity();
                    }
                }
            }
            if (this.time == -1) {
                throw new RuntimeException("No entry found for [" + this.node.getSourceURI()
                        + "], revision [" + this.number + "]");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getInputStream() {
        Source source = null;
        SourceResolver resolver = null;
        try {
            String sourceUri = getSourceURI();
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(sourceUri);
            if (source.exists()) {
                return source.getInputStream();
            } else {
                throw new RuntimeException("No check-in entry found for ["
                        + this.node.getSourceURI() + "], revision [" + this.number + "]");
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
    }

    public String getSourceURI() {
        SourceNodeRCML rcml = (SourceNodeRCML) this.node.getRcml();
        String sourceUri = rcml.getBackupSourceUri(this.node.getContentSource(), getTime());
        return sourceUri;
    }

    public int getNumber() {
        return this.number;
    }

    public MetaData getMetaData(String namespaceUri) throws MetaDataException {
        return getMetaDataHandler().getMetaData(namespaceUri);
    }

    private SourceNodeMetaDataHandler metaDataHandler = null;

    protected SourceNodeMetaDataHandler getMetaDataHandler() {
        if (this.metaDataHandler == null) {
            this.metaDataHandler = new SourceNodeMetaDataHandler(this.manager, getMetaSourceUri(), this);
        }
        return this.metaDataHandler;
    }

    protected String getMetaSourceUri() {
        String realSourceUri = SourceWrapper.computeRealSourceUri(this.manager, this.node.getSession(), 
                this.node.getSourceURI(), getLogger());
        return realSourceUri + ".meta." + getTime() + ".bak";
    }

    public String[] getMetaDataNamespaceUris() throws MetaDataException {
        return getMetaDataHandler().getMetaDataNamespaceUris();
    }

    public boolean exists() throws RepositoryException {
        try {
            return SourceUtil.exists(getSourceURI(), this.manager);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public long getContentLength() throws RepositoryException {
        try {
            return SourceUtil.getContentLength(getSourceURI(), this.manager);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public long getLastModified() throws RepositoryException {
        try {
            return SourceUtil.getLastModified(getSourceURI(), this.manager);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public String getMimeType() throws RepositoryException {
        try {
            return SourceUtil.getMimeType(getSourceURI(), this.manager);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public String getUserId() {
        initialize();
        return this.userId;
    }

}
