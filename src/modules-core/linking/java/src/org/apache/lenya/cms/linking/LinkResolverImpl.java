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

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.util.Query;

/**
 * Link resolver implemenation.
 */
public class LinkResolverImpl extends AbstractLogEnabled implements LinkResolver {

    /**
     * The Avalon role.
     */
    public static final String ROLE = LinkResolverImpl.class.getName();
    
    protected static final String PAIR_DELIMITER = ",";
    protected static final String KEY_VALUE_DELIMITER = "=";

    public LinkTarget resolve(Document currentDoc, String linkUri) throws MalformedURLException {

        String[] schemeAndPath = linkUri.split(":");
        String path = schemeAndPath[1];

        String pubId;
        String uuid;
        String area;
        String language;
        int revision = -1;

        if (path.indexOf(PAIR_DELIMITER) > -1) {
            int firstDelimiterIndex = path.indexOf(PAIR_DELIMITER);
            uuid = path.substring(0, firstDelimiterIndex);
            String pathQueryString = path.substring(firstDelimiterIndex + 1);
            Query query = new Query(pathQueryString, PAIR_DELIMITER, KEY_VALUE_DELIMITER);
            pubId = query.getValue("pub", currentDoc.getPublication().getId());
            area = query.getValue("area", currentDoc.getArea());
            language = query.getValue("lang", currentDoc.getLanguage());
            String revisionString = query.getValue("rev", null);
            if (revisionString != null) {
                revision = Integer.valueOf(revisionString).intValue();
            }
        } else {
            uuid = path;
            pubId = currentDoc.getPublication().getId();
            area = currentDoc.getArea();
            language = currentDoc.getLanguage();
        }

        if (uuid.length() == 0) {
            uuid = currentDoc.getUUID();
        }

        try {
            Publication pub = currentDoc.getFactory().getPublication(pubId);
            Area areaObj = pub.getArea(area);
            Document doc;
            if (areaObj.contains(uuid, language)) {
                doc = areaObj.getDocument(uuid, language);
            } else {
                if (this.fallbackMode == MODE_FAIL) {
                    doc = null;
                } else if (this.fallbackMode == MODE_DEFAULT_LANGUAGE) {
                    if (areaObj.contains(uuid, pub.getDefaultLanguage())) {
                        doc = areaObj.getDocument(uuid, pub.getDefaultLanguage());
                    }
                    else {
                        doc = null;
                    }
                } else {
                    throw new RuntimeException("The fallback mode [" + this.fallbackMode
                            + "] is not supported!");
                }
            }
            if (revision > -1) {
                return new LinkTarget(doc, revision);
            }
            else {
                return new LinkTarget(doc);
            }
        } catch (PublicationException e) {
            throw new RuntimeException(e);
        }
    }

    private int fallbackMode = MODE_DEFAULT_LANGUAGE;

    public int getFallbackMode() {
        return this.fallbackMode;
    }

    public void setFallbackMode(int mode) {
        this.fallbackMode = mode;
    }

}
