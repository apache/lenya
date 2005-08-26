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

/* @version $Id:$*/
package org.apache.lenya.cms.cocoon.generation;

import java.io.IOException;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Generator to provide a JCR view.
 */
public class JCRSysViewGenerator extends ServiceableGenerator {
    private static final Logger log = Logger.getLogger(JCRSysViewGenerator.class);

    protected static final String ROOT_PARAMETER = "root";

    protected String root;

    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par)
            throws ProcessingException, SAXException, IOException {
        try {
            root = par.getParameter(ROOT_PARAMETER);
        } catch (ParameterException e) {
            throw new ProcessingException("'root' parameter not found", e);
        }

        super.setup(resolver, objectModel, src, par);
    }

    public void generate() throws IOException, SAXException, ProcessingException {
        Repository repo = null;
        try {
            repo = (Repository) manager.lookup(Repository.class.getName());
        } catch (Exception e) {
            throw new CascadingRuntimeException("Cannot lookup repository", e);
        }

        Session session;
        try {
            session = repo.login();
        } catch (LoginException e) {
            throw new ProcessingException("Login to repository failed", e);
        } catch (RepositoryException e) {
            throw new ProcessingException("Cannot access repository", e);
        }

        // Export repository system view: with binary content, recurse.
        try {
            log.debug("Generating JCR system view at node [" + root + "]");
            session.exportSystemView(root, this.contentHandler, false, false);
        } catch (PathNotFoundException e) {
            throw new ProcessingException("Export root path not found", e);
        } catch (SAXException e) {
            throw new ProcessingException("Export of repository system view failed", e);
        } catch (RepositoryException e) {
            throw new ProcessingException("Cannot access repository", e);
        }
    }

}
