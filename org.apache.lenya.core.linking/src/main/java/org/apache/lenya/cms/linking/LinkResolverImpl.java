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
package org.apache.lenya.cms.linking;

import java.net.MalformedURLException;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.ResourceNotFoundException;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.Session;

/**
 * Link resolver implementation.
 */
public class LinkResolverImpl extends AbstractLogEnabled implements LinkResolver, Configurable {

    /**
     * The Avalon role.
     */
    public static final String ROLE = LinkResolverImpl.class.getName();

    //florent : change the method signature by adding a session attribute because document don't still handle his session
    //TODO :  remove comments when ok
    public LinkTarget resolve(Session session, Document currentDoc, String linkUri) throws MalformedURLException {

        Link link = new Link(linkUri);

        String language = getValue(link.getLanguage(), currentDoc.getLanguage());
        String revisionString = getValue(link.getRevision(), null);
        String area = getValue(link.getArea(), currentDoc.getArea());
        //TODO : florent : remove it when ok
        //String pubId = getValue(link.getPubId(), currentDoc.getPublication().getId());
        String pubId = getValue(link.getPubId(), currentDoc.getPublicationId());
        
        String uuid = getValue(link.getUuid(), currentDoc.getUUID());
        if (uuid.length() == 0) {
            uuid = currentDoc.getUUID();
        }

        //TODO : florent : remove comment when ok
        //return resolve(currentDoc.getSession(), pubId, area, uuid, language, revisionString);
        return resolve(session, pubId, area, uuid, language, revisionString);
    }

    protected String getValue(String value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    private int fallbackMode = MODE_DEFAULT_LANGUAGE;

    public int getFallbackMode() {
        return this.fallbackMode;
    }

    public void setFallbackMode(int mode) {
        this.fallbackMode = mode;
    }

    protected static final String ELEMENT_FALLBACK = "fallback";

    public void configure(Configuration config) throws ConfigurationException {
        Configuration fallbackConfig = config.getChild(ELEMENT_FALLBACK, false);
        if (fallbackConfig != null) {
            boolean fallback = config.getValueAsBoolean();
            if (fallback) {
                setFallbackMode(MODE_DEFAULT_LANGUAGE);
            } else {
                setFallbackMode(MODE_FAIL);
            }
        }
    }

    public LinkTarget resolve(Session session, String linkUri) throws MalformedURLException {

        Link link = new Link(linkUri);
        String language = link.getLanguage();
        assert language != null;
        String uuid = link.getUuid();
        assert uuid != null;
        String area = link.getArea();
        assert area != null;
        String pubId = link.getPubId();
        assert pubId != null;

        String revisionString = getValue(link.getRevision(), null);

        return resolve(session, pubId, area, uuid, language, revisionString);
    }

    protected LinkTarget resolve(Session session, String pubId, String area, String uuid,
            String language, String revisionString) {
        int revision;
        if (revisionString == null) {
            revision = -1;
        } else {
            revision = Integer.valueOf(revisionString).intValue();
        }

        Publication pub = session.getPublication(pubId);
        Area areaObj = pub.getArea(area);
        Document doc;
        if (areaObj.contains(uuid, language)) {
            try {
							doc = areaObj.getDocument(uuid, language);
						} catch (ResourceNotFoundException e) {
								throw new RuntimeException(e);
						}
        } else {
            if (this.fallbackMode == MODE_FAIL) {
                doc = null;
            } else if (this.fallbackMode == MODE_DEFAULT_LANGUAGE) {
                if (areaObj.contains(uuid, pub.getDefaultLanguage())) {
                    try {
											doc = pub.getArea(area).getDocument(uuid, pub.getDefaultLanguage(), revision);
										} catch (ResourceNotFoundException e) {
												throw new RuntimeException(e);
										}
                } else {
                    doc = null;
                }
            } else {
                throw new RuntimeException("The fallback mode [" + this.fallbackMode
                        + "] is not supported!");
            }
        }
        if (doc == null) {
            return new LinkTarget();
        } else {
            if (revision > -1) {
                return new LinkTarget(doc, revision);
            } else {
                return new LinkTarget(doc);
            }
        }
    }

}
