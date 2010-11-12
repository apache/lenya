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
package org.apache.lenya.cms.publication;

import java.util.Arrays;

public class UriHandlerImpl implements UriHandler {

    private DocumentFactory factory;

    public UriHandlerImpl(DocumentFactory factory) {
        this.factory = factory;
    }

    public Document getDocument(String webappUri) throws ResourceNotFoundException {
        return this.factory.getFromURL(webappUri);
    }

    public boolean isDocument(String webappUri) {
        return this.factory.isDocument(webappUri);
    }

    public Area getArea(String webappUri) throws ResourceNotFoundException {
        URLInformation info = new URLInformation(webappUri);
        return getPublication(info.getPublicationId()).getArea(info.getArea());
    }

    public Publication getPublication(String webappUri) throws ResourceNotFoundException {
        String id = new URLInformation(webappUri).getPublicationId();
        if (id == null) {
            throw new ResourceNotFoundException("No publication for URI " + webappUri);
        }
        try {
            return this.factory.getPublication(id);
        } catch (PublicationException e) {
            throw new ResourceNotFoundException(e);
        }
    }

    public boolean isArea(String webappUri) {
        URLInformation info = new URLInformation(webappUri);
        String pubId = info.getPublicationId();
        if (pubId != null && isPublication(pubId)) {
            Publication pub = getPublication(pubId);
            String area = info.getArea();
            if (area != null && Arrays.asList(pub.getAreaNames()).contains(area)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPublication(String webappUri) {
        String id = new URLInformation(webappUri).getPublicationId();
        return id != null && this.factory.existsPublication(id);
    }

}
