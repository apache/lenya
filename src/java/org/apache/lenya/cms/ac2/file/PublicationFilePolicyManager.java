/*
$Id: PublicationFilePolicyManager.java,v 1.3 2003/08/12 15:15:54 andreas Exp $
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
package org.apache.lenya.cms.ac2.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac2.AccreditableManager;
import org.apache.lenya.cms.ac2.DefaultPolicy;
import org.apache.lenya.cms.ac2.Policy;
import org.apache.lenya.cms.publication.DefaultDocumentBuilder;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;

/**
 * A FilePolicyManager that resolves policies relative to the {publication}/config/ac/policies directory.<br/>
 * The "directory" parameter must point to the context:<br/>
 * &lt;parameter name="directory" value="context:///" /&gt;
 * 
 * @author andreas
 */
public class PublicationFilePolicyManager extends FilePolicyManager {

    protected static final String POLICIES_PATH =
        "config/ac/policies".replace('/', File.separatorChar);

    /**
     * @see org.apache.lenya.cms.ac2.file.FilePolicyManager#getPolicyURI(java.lang.String, java.lang.String)
     */
    protected String getPolicyURI(String url, String policyFilename)
        throws AccessControlException {
            
        getLogger().debug("Resolving policy URI for URL [" + url + "]");
            
        Publication publication = getPublication(url);
        url = url.substring(("/" + publication.getId()).length());

        String path = url.replace('/', File.separatorChar) + File.separator + policyFilename;
        File policyDirectory = new File(publication.getDirectory(), POLICIES_PATH);
        File policyFile = new File(policyDirectory, path);
        return policyFile.toURI().toString();
    }

    /**
     * @see org.apache.lenya.cms.ac2.InheritingPolicyManager#getPolicies(org.apache.lenya.cms.ac2.AccreditableManager, java.lang.String)
     */
    public DefaultPolicy[] getPolicies(AccreditableManager controller, String url)
        throws AccessControlException {

        getLogger().debug("Resolving policies for URL [" + url + "]");
            
        Publication publication = getPublication(url);
        String path = getPolicyPath(url, publication);
        
        List policies = new ArrayList();

        String[] directories = path.split("/");
        path = "/" + publication.getId();
        
        getLogger().debug("Building URL policy for URL [" + path + "]");
        Policy policy = buildURLPolicy(controller, path);
        policies.add(policy);

        for (int i = 0; i < directories.length; i++) {
            path += directories[i] + "/";
            getLogger().debug("Building subtree policy for URL [" + path + "]");
            policy = buildSubtreePolicy(controller, path);
            policies.add(policy);
        }

        return (DefaultPolicy[]) policies.toArray(new DefaultPolicy[policies.size()]);
    }

    /**
     * Returns the publication for a certain URL.
     * @param url The url.
     * @return A publication.
     * @throws AccessControlException when the publication could not be created.
     */
    protected Publication getPublication(String url) throws AccessControlException {
        getLogger().debug("Building publication");
        
        Publication publication;
        try {
            File servletContext = getPoliciesDirectory();
            getLogger().debug("Webapp URL:      [" + url + "]");
            getLogger().debug("Serlvet context: [" + servletContext.getAbsolutePath() + "]");
            publication = PublicationFactory.getPublication(url, servletContext);
        } catch (PublicationException e) {
            throw new AccessControlException(e);
        }
        return publication;
    }

    /**
     * Returns the policy path, containing the steps to look for policy files
     * separated by slashes. If the webapp URL corresponds to an existing document,
     * the document ID is used. Otherwise, the requested URL inside the publication
     * is returned.
     * @param webappUrl The webapp URL to obtain the policy for.
     * @param publication The publication.
     * @return A String.
     * @throws AccessControlException when something went wrong.
     */
    protected String getPolicyPath(String webappUrl, Publication publication)
        throws AccessControlException {
            
        getLogger().debug("Resolving policy path for URL [" + webappUrl + "]");
            
        Document document = null;
        String path;

        try {
            document = DefaultDocumentBuilder.getInstance().buildDocument(publication, webappUrl);
        } catch (DocumentBuildException e) {
            throw new AccessControlException(e);
        }

        if (document.getFile().exists()) {
            path = "/" + document.getArea() + document.getId();
            getLogger().debug("Document exists, using document ID [" + path + "]");
        } else {
            path = webappUrl.substring(("/" + publication.getId()).length());
            getLogger().debug("Document does not exist, using URL [" + path + "]");
        }
        return path;
    }

}
