/*
$Id: PageEnvelopeModule.java,v 1.14 2003/07/23 13:21:45 gregor Exp $
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
package org.apache.lenya.cms.cocoon.components.modules.input;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PublicationException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;


/**
 * Input module wrapping the page envelope. This module provides publication
 * related information such as document-id, area, publication-id.
 * 
 * @see org.apache.lenya.cms.publication.PageEnvelope
 * 
 * @author  andreas
 */
public class PageEnvelopeModule extends AbstractInputModule {
	
    /**
     * Get the the page envelope for the given objectModel.
     * 
     * @param objectModel the objectModel for which the page enevelope is requested.
     * 
     * @return a <code>PageEnvelope</code>
     * 
     * @throws ConfigurationException if the page envelope could not be instantiated.
     */
    protected PageEnvelope getEnvelope(Map objectModel)
        throws ConfigurationException {
        PageEnvelope envelope = null;

        try {
            envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
        } catch (Exception e) {
            throw new ConfigurationException("Resolving page envelope failed: ", e);
        }

        return envelope;
    }

    /** (non-Javadoc)
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
        throws ConfigurationException, PageEnvelopeException, PublicationException {
        PageEnvelope envelope = getEnvelope(objectModel);
        Object value = null;

        if (name.equals(PageEnvelope.AREA)) {
            value = envelope.getArea();
        } else if (name.equals(PageEnvelope.CONTEXT)) {
            value = envelope.getContext();
        } else if (name.equals(PageEnvelope.PUBLICATION_ID)) {
            value = envelope.getPublication().getId();
        } else if (name.equals(PageEnvelope.PUBLICATION)) {
            value = envelope.getPublication();
        } else if (name.equals(PageEnvelope.DOCUMENT_ID)) {
            value = envelope.getDocumentId();
        } else if (name.equals(PageEnvelope.DOCUMENT_URL)) {
            value = envelope.getDocumentURL();
        } else if (name.equals(PageEnvelope.DOCUMENT_PATH)) {
            value = envelope.getDocumentPath();
        } else if (name.equals(PageEnvelope.DOCUMENT_FILE)) {
            value = envelope.getDocumentFile();
        } else if (name.equals(PageEnvelope.DOCUMENT_LANGUAGE)) {
            value = envelope.getDocument().getLanguage();
		} else if (name.equals(PageEnvelope.DOCUMENT_DC_TITLE)) {
			  value = envelope.getDocument().getDublinCore().getTitle();
		} else if (name.equals(PageEnvelope.DOCUMENT_DC_CREATOR)) {
			value = envelope.getDocument().getDublinCore().getCreator();
        } else if (name.equals(PageEnvelope.DOCUMENT_DC_SUBJECT)) {
			value = envelope.getDocument().getDublinCore().getSubject();
        } else if (name.equals(PageEnvelope.DOCUMENT_DC_DESCRIPTION)) {
			value = envelope.getDocument().getDublinCore().getDescription();
        } else if (name.equals(PageEnvelope.DOCUMENT_DC_RIGHTS)) {  
			value = envelope.getDocument().getDublinCore().getRights();
		} else if (name.equals(PageEnvelope.DOCUMENT_LASTMODIFIED)) {  
			value = envelope.getDocument().getLastModified();
		}
        return value;
    }

	/**
	 *  (non-Javadoc)
	 * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration, java.util.Map)
	 */
    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        return Arrays.asList(PageEnvelope.PARAMETER_NAMES).iterator();
    }

	/**
	 *  (non-Javadoc)
	 * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
	 */
    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
        throws ConfigurationException, PageEnvelopeException, PublicationException {
        Object[] objects = { getAttribute(name, modeConf, objectModel) };

        return objects;
    }
}
