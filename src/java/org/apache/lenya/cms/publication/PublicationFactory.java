/*
$Id: PublicationFactory.java,v 1.7 2003/07/02 17:50:38 andreas Exp $
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

import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;

import org.apache.log4j.Category;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author  andreas
 */
public final class PublicationFactory {
    private PublicationFactory() {
    }

    private static Category log = Category.getInstance(PublicationFactory.class);
    private static Map idToPublication = new HashMap();

    /**
     * Creates a new publication.
     * The publication ID is resolved from the request URI.
     * The servlet context path is resolved from the context object.
     * @param objectModel The object model of the Cocoon component.
     */
    public static Publication getPublication(Map objectModel) {
        assert objectModel != null;
        Request request = ObjectModelHelper.getRequest(objectModel);
        Context context = ObjectModelHelper.getContext(objectModel);
        return getPublication(request, context);
    }

    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     * @param servletContextPath DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Publication getPublication(String id, String servletContextPath) {
        assert id != null;
        assert servletContextPath != null;

        Publication publication;

        if (idToPublication.containsKey(id)) {
            publication = (Publication) idToPublication.get(id);
        } else {
            publication = new Publication(id, servletContextPath);
            idToPublication.put(id, publication);
        }

        assert publication != null;

        return publication;
    }
    
    /**
     * Creates a new publication based on a request and a context.
     * @param request A request.
     * @param context A context.
     * @return A publication.
     */
    public static Publication getPublication(Request request, Context context) {
        String contextPath = request.getContextPath();

        if (contextPath == null) {
            contextPath = "";
        }

        String webappUri = request.getRequestURI().substring(contextPath.length());

        String publicationId = webappUri.split("/")[1];
        assert !"".equals(publicationId);

        String servletContextPath = context.getRealPath("");
        return getPublication(publicationId, servletContextPath);
    }
    
}
