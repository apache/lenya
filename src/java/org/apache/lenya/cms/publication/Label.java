/*
$Id: Label.java,v 1.7 2003/10/29 15:31:12 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.publication;

/**
 * The Label class encapsulates a string label and a associated language.
 *
 * @author Christian Egli
 * @version $Revision: 1.7 $
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
