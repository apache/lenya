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

/* $Id: ValidateAction.java,v 1.10 2004/03/18 14:51:58 egli Exp $  */

package org.apache.lenya.cms.cocoon.acting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

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
import org.xml.sax.SAXException;

/**
 * Action to validate an xml document with relax ng schema.
 */
public class ValidateAction extends AbstractConfigurableAction {
    Category log = Category.getInstance(ValidateAction.class);

    /** (non-Javadoc)
     * @see org.apache.cocoon.acting.Action#act(org.apache.cocoon.environment.Redirector, org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     **/
    public Map act(
        Redirector redirector,
        SourceResolver resolver,
        Map objectModel,
        String source,
        Parameters parameters)
        throws Exception {
        File sitemap = new File(new URL(resolver.resolveURI("").getURI()).getFile());
        File schema =
            new File(
                sitemap.getAbsolutePath()
                    + File.separator
                    + parameters.getParameter("schema"));
        getLogger().debug("schema: " + schema.getAbsolutePath());

        Request request = ObjectModelHelper.getRequest(objectModel);

        if (request.getParameter("cancel") != null) {
            getLogger().warn(".act(): Editing has been canceled");
            return null;
        }
        if (!schema.isFile()) {
            log.warn("No such schema: " + schema.getAbsolutePath());
            return null;
        }

        try {
            File tmpFile = createTmpFile(request.getParameter("content"));
            String message = validateDocument(schema, tmpFile);
            tmpFile.delete();
            if (message != null) {
                HashMap hmap = new HashMap();
                hmap.put("message", "RELAX NG Validation failed: " + message);
                return hmap;
            }
        } catch (Exception e) {
            // FIXME: could it be that the tmpFile is not removed in the case of 
            // an exception? Exceptions happen everytime the validation fails
            getLogger().error("RELAX NG Validation failed: " + e.getMessage());
            HashMap hmap = new HashMap();
            hmap.put("message", "RELAX NG Validation failed: " + e.getMessage());
            return hmap;
        }
        return null;
    }

    /**
     * Validate document
     * @param schema The relax ng schema.
     * @param file The file to validate
     * @return The validation error message or null.
     */
    private String validateDocument(File schema, File file) throws Exception {
        return RelaxNG.validate(schema, file);

    }

    private File createTmpFile(String content)
        throws SAXException, ParserConfigurationException, TransformerException, IOException {
        File tmpFile = File.createTempFile("OneformEditor", null);
        getLogger().debug("file: " + tmpFile.getAbsolutePath());

        //write POST content in temporary file
        FileWriter fileWriter = new FileWriter(tmpFile);
        fileWriter.write(content);
        fileWriter.close();

        Document document = null;
        DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
        parserFactory.setValidating(false);
        parserFactory.setNamespaceAware(true);
        parserFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = parserFactory.newDocumentBuilder();

        document = builder.parse(tmpFile.getAbsolutePath());

        DocumentHelper.writeDocument(document, tmpFile);

        return tmpFile;
    }
}
