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

/* $Id: ValidateAction.java,v 1.8 2004/03/01 16:18:21 gregor Exp $  */

package org.apache.lenya.cms.cocoon.acting;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractConfigurableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.util.IOUtils;
import org.apache.lenya.xml.RelaxNG;
import org.apache.log4j.Category;

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
		File sitemap =
			new File(new URL(resolver.resolveURI("").getURI()).getFile());
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
		if (schema.isFile()) {
			//create temporary file to validate 
			if (!(sitemap.exists())) {
				sitemap.mkdir();
			}
			File file =
				IOUtils.createFile(sitemap, parameters.getParameter("file"));
			getLogger().debug("file: " + file.getAbsolutePath());

            //write POST content in temporary file
			String content = request.getParameter("content");
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(content);
			fileWriter.close();

                        log.debug("Schema: " + schema.getAbsolutePath());
            
                        //validate temporary file
                        String message = validateDocument(schema, file);
                        if (message != null) {
                            getLogger().error("RELAX NG Validation failed: " + message);
                            HashMap hmap = new HashMap();
                            hmap.put("message", "RELAX NG Validation failed: " + message);
                            return hmap;
                        }
		} else {
			log.warn("No such schema: " + schema.getAbsolutePath());
		}
		return null;
	}

	/**
     * Validate document
	 * @param schema The relax ng schema.
	 * @param file The file to validate
	 * @return The validation error message or null.
	 */
	private String validateDocument(File schema, File file) {
		// FIXME: what is this method for? It seems to be a lot of hot air and
		// some logging around a call to RelaxNG.validate
		try {
			File parentFile = file.getParentFile();
			if (!(parentFile.exists())) {
				parentFile.mkdir();
			}
			String filename = file.getName();
			// FIXME: why do we create a file and never use it later?
			File valFile =
				IOUtils.createFile(parentFile, filename + ".validate");
			getLogger().debug("validation file: " + valFile.getAbsolutePath());

			return RelaxNG.validate(schema, file);
		} catch (Exception e) {
			getLogger().error(e.getMessage());
			return "" + e;
		}
	}
}
