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
/* $Id: ValidateAction.java,v 1.9 2004/03/17 12:53:01 gregor Exp $ */
package org.apache.lenya.cms.cocoon.acting;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractConfigurableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.RelaxNG;
import org.apache.log4j.Category;
import org.w3c.dom.Document;
/**
 * Action to validate an xml document with relax ng schema.
 */
public class ValidateAction extends AbstractConfigurableAction {
    Category log = Category.getInstance(ValidateAction.class);
    /**
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.acting.Action#act(org.apache.cocoon.environment.Redirector,
     *      org.apache.cocoon.environment.SourceResolver, java.util.Map,
     *      java.lang.String,
     *      org.apache.avalon.framework.parameters.Parameters)
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source,
            Parameters parameters) throws Exception {
        Request request = ObjectModelHelper.getRequest(objectModel);
        if (request.getParameter("cancel") != null) {
            getLogger().warn(".act(): Editing has been canceled");
            return null;
        }
        File sitemap = new File(new URL(resolver.resolveURI("").getURI()).getFile());
        File schema = new File(sitemap.getAbsolutePath() + File.separator
                + parameters.getParameter("schema"));
        getLogger().debug("schema: " + schema.getAbsolutePath());
        File file = new File(sitemap.getAbsolutePath() + File.separator
                + parameters.getParameter("file"));
        getLogger().debug("file: " + file.getAbsolutePath());
        if (file.isFile()) {
            try {
                Document document = null;
                DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
                parserFactory.setValidating(false);
                parserFactory.setNamespaceAware(true);
                parserFactory.setIgnoringElementContentWhitespace(true);
                DocumentBuilder builder = parserFactory.newDocumentBuilder();
                try {
                    document = builder.parse(file.getAbsolutePath());
                } catch (Exception e) {
                    getLogger().error(".act(): Exception: " + e.getMessage(), e);
                    HashMap hmap = new HashMap();
                    if (e.getMessage() != null) {
                        hmap.put("message", e.getMessage());
                    } else {
                        hmap.put("message", "No message (" + e.getClass().getName() + ")");
                    }
                    return hmap;
                }
                // validate against relax ng
                if (schema.isFile()) {
                    DocumentHelper.writeDocument(document, new File(file.getCanonicalPath()
                            + ".validate"));
                    String message = validateDocument(schema, new File(file.getCanonicalPath()
                            + ".validate"));
                    if (message != null) {
                        log.error("RELAX NG Validation failed: " + message);
                        HashMap hmap = new HashMap();
                        hmap.put("message", "RELAX NG Validation failed: " + message);
                        return hmap;
                    }
                } else {
                    log.warn("No such schema: " + schema.getAbsolutePath());
                }
                DocumentHelper.writeDocument(document, file);
                return null;
            } catch (Exception e) {
                getLogger().error(".act(): Exception: " + e.getMessage(), e);
                HashMap hmap = new HashMap();
                if (e.getMessage() != null) {
                    hmap.put("message", e.getMessage());
                } else {
                    hmap.put("message", "No message (" + e.getClass().getName() + ")");
                }
                return hmap;
            }
        } else {
            getLogger().error(".act(): No such file: " + file.getAbsolutePath());
            HashMap hmap = new HashMap();
            hmap.put("message", "No such file: " + file.getAbsolutePath());
            return hmap;
        }
    }
    /**
     * Validate document
     * 
     * @param schema
     *            The relax ng schema.
     * @param file
     *            The file to validate
     * @return The validation error message or null.
     */
    private String validateDocument(File schema, File file) {
        try {
            return RelaxNG.validate(schema, file);
        } catch (Exception e) {
            getLogger().error(e.getMessage());
            return "" + e;
        }
    }
}
