/*
$Id: DocumentTypeBuilder.java,v 1.9 2004/02/02 02:50:39 stefano Exp $
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

import java.io.File;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.lenya.cms.authoring.ParentChildCreatorInterface;


/**
 * A builder for document types.
 *
 * @author <a href="mailto:andreas.hartmann@wyona.org">Andreas Hartmann</a>
 */
public final class DocumentTypeBuilder {
    /** Creates a new instance of DocumentTypeBuilder */
    private DocumentTypeBuilder() {
    }

    /**
     * The default document types configuration directory, relative to the publication directory.
     */
    public static final String DOCTYPE_DIRECTORY = "config/doctypes".replace('/', File.separatorChar);

    /*
     * The default document types configuration file, relative to the publication directory.
     */
    public static final String CONFIG_FILE = "doctypes.xconf".replace('/', File.separatorChar);
    public static final String DOCTYPES_ELEMENT = "doctypes";
    public static final String DOCTYPE_ELEMENT = "doc";
    public static final String TYPE_ATTRIBUTE = "type";
    public static final String CREATOR_ELEMENT = "creator";
    public static final String SRC_ATTRIBUTE = "src";
    public static final String WORKFLOW_ELEMENT = "workflow";

    /**
     * Builds a document type for a given name.
     *
     * @param name A string value.
     * @param publication The publication the document type belongs to.
     * @return A document type object.
     * @throws DocumentTypeBuildException When something went wrong.
     */
    public static DocumentType buildDocumentType(String name, Publication publication)
        throws DocumentTypeBuildException {
        DocumentType type = new DocumentType(name);

        File configDirectory = new File(publication.getDirectory(), DOCTYPE_DIRECTORY);
        File configFile = new File(configDirectory, CONFIG_FILE);

        try {
            Configuration configuration = new DefaultConfigurationBuilder().buildFromFile(configFile);

            Configuration[] doctypeConfigurations = configuration.getChildren(DOCTYPE_ELEMENT);
            Configuration doctypeConf = null;

            for (int i = 0; i < doctypeConfigurations.length; i++) {
                if (doctypeConfigurations[i].getAttribute(TYPE_ATTRIBUTE).equals(name)) {
                    doctypeConf = doctypeConfigurations[i];
                }
            }

            if (doctypeConf == null) {
                throw new DocumentTypeBuildException("No definition found for doctype '" + name +
                    "'!");
            }

            ParentChildCreatorInterface creator;
            Configuration creatorConf = doctypeConf.getChild(CREATOR_ELEMENT, false);

            if (creatorConf != null) {
                String creatorClassName = creatorConf.getAttribute(SRC_ATTRIBUTE);
                Class creatorClass = Class.forName(creatorClassName);
                creator = (ParentChildCreatorInterface) creatorClass.newInstance();
                creator.init(creatorConf);
            } else {
                creator = new org.apache.lenya.cms.authoring.DefaultBranchCreator();
            }

            type.setCreator(creator);

            Configuration workflowConf = doctypeConf.getChild(WORKFLOW_ELEMENT, false);

            if (workflowConf != null) {
                String workflowFileName = workflowConf.getAttribute(SRC_ATTRIBUTE);
                type.setWorkflowFileName(workflowFileName);
            }
        } catch (Exception e) {
            throw new DocumentTypeBuildException(e);
        }

        return type;
    }
}
