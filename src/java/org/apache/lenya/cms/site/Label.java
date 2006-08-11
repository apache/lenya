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

/* @version $Id$ */

package org.apache.lenya.cms.site;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.Publication;

/**
 * The Label class encapsulates a string label and a associated language.
 */
public abstract class Label implements Link {
    private String label = null;
    private String language = null;

    /**
     * Creates a new Label object.
     * @param factory The document factory.
     * @param node The site node.
     * @param _label the actual label
     * @param _language the language
     */
    public Label(DocumentFactory factory, SiteNode node, String _label, String _language) {
        this.label = _label;
        this.language = _language;
        this.factory = factory;
        this.node = node;
    }

    /**
     * Get the actual label of the Label object
     * 
     * @return the actual label as a String
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Set the actual label of the label object.
     * 
     * @param _label The label.
     */
    public void setLabel(String _label) {
        this.label = _label;
    }

    /**
     * Get the language of this Label object
     * 
     * @return the language
     */

    public String getLanguage() {
        return this.language;
    }

    /**
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getLabel() + " " + getLanguage();
    }

    /**
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        boolean equals = false;

        if (getClass().isInstance(obj)) {
            Label otherLabel = (Label) obj;
            equals = getLabel().equals(otherLabel.getLabel())
                    && getLanguage().equals(otherLabel.getLanguage());
        }

        return equals;
    }

    /**
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return getLabel().hashCode() + getLanguage().hashCode();
    }

    private SiteNode node;
    private DocumentFactory factory;

    public Document getDocument() {
        Publication pub = getNode().getStructure().getPublication();
        String area = getNode().getStructure().getArea();
        try {
            return this.factory.get(pub, area, getNode().getUuid(), getLanguage());
        } catch (DocumentBuildException e) {
            throw new RuntimeException(e);
        }
    }

    public SiteNode getNode() {
        return this.node;
    }

}
