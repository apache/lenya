/*
 * $Id: NoPasswordYetAction.java,v 1.8 2003/04/24 13:52:38 gregor Exp $
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

import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;

import java.io.File;

import java.util.HashMap;
import java.util.Map;
import org.apache.cocoon.environment.ObjectModelHelper;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.8 $
 */
public class NoPasswordYetAction extends AbstractAction {
    /**
     * DOCUMENT ME!
     *
     * @param redirector DOCUMENT ME!
     * @param resolver DOCUMENT ME!
     * @param objectModel DOCUMENT ME!
     * @param source DOCUMENT ME!
     * @param params DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source,
        Parameters params) {
        // This is the name of the flag file that tells us if
        // this is a fresh installation. If the file is present,
        // we assume a fresh install and return an empty map,
        // otherwise we assume a configured installation and return 
        // null.
        //
        // We get this parameter from the sitemap through the params object.
        // The filename is relative to the WEB-INF directory.
        //
        String flagFileRelativePath = params.getParameter("flagfilerelativepath",
                "new_installation_flag");
        Context context = ObjectModelHelper.getContext(objectModel);
        String flagFileAbsolutePath = context.getRealPath("/") + "/" + flagFileRelativePath;
        getLogger().error("DetectNewInstallationAction: flagFileAbsolutePath = " +
            flagFileAbsolutePath);

        if (!new File(flagFileAbsolutePath).exists()) {
            getLogger().error("DetectNewInstallationAction: does not exist, skipping first-time setup!");

            return null;
        }

        getLogger().error("DetectNewInstallationAction: does exist!");

        Map sitemapParams = new HashMap();
        sitemapParams.put("flagfileabsolutepath", flagFileAbsolutePath);

        return sitemapParams;
    }
}
