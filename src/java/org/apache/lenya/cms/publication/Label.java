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

/* $Id: Label.java,v 1.8 2004/03/01 16:18:17 gregor Exp $  */

package org.apache.lenya.cms.publication;

/**
 * The Label class encapsulates a string label and a associated language.
 */
public class Label {
    private String label = null;
    private String language = null;

    /**
     * Creates a new Label object with no language.
     *
     * @param label the actual label
     */
    public Label(String label) {
        this(label, null);
    }

    /**
     * Creates a new Label object.
     *
     * @param label the actual label
     * @param language the language
     */
    public Label(String label, String language) {
        this.label = label;
        this.language = language;
    }

    /**
     * Get the actual label of the Label object
     *
     * @return the actual label as a String
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the actual label of the label object.
     * 
     * @param label The label.
     */
    public void setLabel(String label) {
        this.label = label;
    }
    /**
     * Get the language of this Label object 
     *
     * @return the language
     */

    public String getLanguage() {
        return language;
    }
    
    
    /** (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getLabel() + " " + getLanguage();
    }

    /** (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        boolean equals = false;

        if (getClass().isInstance(obj)) {
            Label otherLabel = (Label)obj;
            equals =
                getLabel().equals(otherLabel.getLabel())
                    && getLanguage().equals(otherLabel.getLanguage());
        }

        return equals;
    }

    /** (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return getLabel().hashCode() + getLanguage().hashCode();
    }

}
