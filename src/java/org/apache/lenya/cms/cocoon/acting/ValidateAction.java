/*
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.cocoon.acting;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.io.FileWriter;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractConfigurableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.xml.RelaxNG;

/**
 * Action to validate an xml document
 * 
 * @author Edith Chevrier
 * @version 2004.1.07
 */
public class ValidateAction extends AbstractConfigurableAction {

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
		File file =
			new File(
				sitemap.getAbsolutePath()
					+ File.separator
					+ parameters.getParameter("file"));
		File schema =
			new File(
				sitemap.getAbsolutePath()
					+ File.separator
					+ parameters.getParameter("schema"));

            getLogger().debug("file: " + file.getAbsolutePath());
            getLogger().debug("schema: " + schema.getAbsolutePath());
		Request request = ObjectModelHelper.getRequest(objectModel);

		if (request.getParameter("cancel") != null) {
			getLogger().warn(".act(): Editing has been canceled");
			return null;
		}
		if (schema.isFile()) {
            if (!file.getParentFile().isDirectory()){
                file.getParentFile().mkdir();
            }
            file.createNewFile();
            String content = request.getParameter("content");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        	String message = validateDocument(schema, file);
			if (message != null) {
				getLogger().error("RELAX NG Validation failed: " + message);
				HashMap hmap = new HashMap();
				hmap.put("message", "RELAX NG Validation failed: " + message);
				return hmap;
			}
        } else {
			getLogger().warn("No such schema: " + schema.getAbsolutePath());
		}
	    return null;
    }

	/**
	 * Validate document
	 */
	private String validateDocument(
		File schema,
		File file) {
            
        try {
/** FIXME
 * what is this whole file creation business here?
 */
            File valFile = new File(file.getAbsolutePath() + ".validate");
            if (!valFile.getParentFile().isDirectory()){
                valFile.getParentFile().mkdir();
            }
            valFile.createNewFile();
			return RelaxNG.validate(
				schema,
				file);
		} catch (Exception e) {
			getLogger().error(e.getMessage());
			return "" + e;
		}
        }
}