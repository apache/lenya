/*
$Id: PublicationAccessControllerResolver.java,v 1.3 2003/07/15 14:46:09 egli Exp $
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
package org.apache.lenya.cms.ac2;

import java.io.File;
import java.net.URI;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;

/**
 * Resolves the access controller according to the <code>ac.xconf</code> file of a publication.
 * 
 * @author andreas
 */
public class PublicationAccessControllerResolver
    extends AbstractLogEnabled
    implements AccessControllerResolver, Serviceable {

    protected static final String CONFIGURATION_FILE =
        "config/ac/ac.xconf".replace('/', File.separatorChar);
    protected static final String TYPE_ATTRIBUTE = "type";

    /**
     * @see org.apache.lenya.cms.ac2.AccessControllerResolver#resolveAccessController(java.lang.String)
     */
    public AccessController resolveAccessController(String webappUrl)
        throws AccessControlException {
        return resolveAccessController(webappUrl, "context:///");
    }

    /**
     * @see org.apache.lenya.cms.ac2.AccessControllerResolver#resolveAccessController(java.lang.String)
     */
    public AccessController resolveAccessController(String webappUrl, String contextUri)
        throws AccessControlException {

        assert webappUrl.startsWith("/");

        getLogger().debug("Resolving controller for URL [" + webappUrl + "]");

        AccessController controller = null;

        // remove leading slash
        webappUrl = webappUrl.substring(1);

        if (webappUrl.length() > 0) {

            SourceResolver resolver = null;
            Source contextSource = null;
            File contextDir;
            try {
                resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
                contextSource = resolver.resolveURI(contextUri);
                contextDir = new File(new URI(contextSource.getURI()));
                assert contextDir.isDirectory();
            } catch (Exception e) {
                throw new AccessControlException(e);
            } finally {
                if (resolver != null) {
                    if (contextSource != null) {
                        resolver.release(contextSource);
                    }
                    manager.release(resolver);
                }
            }

            String publicationId = webappUrl.split("/")[0];

            if (PublicationFactory
                .existsPublication(publicationId, contextDir.getAbsolutePath())) {
                
                getLogger().debug("Publication [" + publicationId + "] exists.");
                Publication publication;
                try {
                    publication =
                        PublicationFactory.getPublication(
                            publicationId,
                            contextDir.getAbsolutePath());
                } catch (PublicationException e) {
					throw new AccessControlException(e);
                }

                String publicationUrl = webappUrl.substring(("/" + publicationId).length());
                controller = resolveAccessController(publication, publicationUrl);
            }
            else {
                getLogger().debug("Publication [" + publicationId + "] does not exist.");
        }
        }

        return controller;
    }

    /**
     * Resolves an access controller for a certain URL within a publication.
     * @param publication The publication.
     * @param url The url within the publication.
     * @return An access controller.
     * @throws AccessControlException when something went wrong.
     */
    public AccessController resolveAccessController(Publication publication, String url)
        throws AccessControlException {

        assert publication != null;

        AccessController accessController = null;
        File configurationFile = new File(publication.getDirectory(), CONFIGURATION_FILE);

        if (configurationFile.isFile()) {
        try {
            Configuration configuration =
                new DefaultConfigurationBuilder().buildFromFile(configurationFile);
            String type = configuration.getAttribute(TYPE_ATTRIBUTE);

            boolean authorized;
            accessController =
                (AccessController) getManager().lookup(AccessController.ROLE + "/" + type);

            if (accessController instanceof Configurable) {
                ((Configurable) accessController).configure(configuration);
            }

        } catch (Exception e) {
            throw new AccessControlException(e);
        }
        }

        return accessController;
    }

    private ServiceManager manager;

    /**
     * @see org.apache.lenya.cms.ac2.AccessControllerResolver#release(org.apache.lenya.cms.ac2.AccessController)
     */
    public void release(AccessController controller) {
        if (controller != null) {
            getManager().release(controller);
        }
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    /**
     * Returns the service manager of this Serviceable.
     * @return A service manager.
     */
    public ServiceManager getManager() {
        return manager;
    }

}
