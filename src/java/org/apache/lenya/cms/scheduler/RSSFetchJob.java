/*
 * $Id: RSSFetchJob.java,v 1.3 2003/02/17 12:55:18 egli Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
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
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.wyona.cms.scheduler;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import org.apache.log4j.Category;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.net.URL;


/*
import org.quartz.*;
import org.quartz.simpl.*;
*/

/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 */
public class RSSFetchJob implements Job {
    static Category log = Category.getInstance(RSSFetchJob.class);

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     *
     * @throws JobExecutionException DOCUMENT ME!
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            URL url = new URL("http://www.freshmeat.net/backend/fm.rdf");
            log.debug(".execute(): Remote URL: " + url);

            HttpClient httpClient = new HttpClient();

            HttpMethod httpMethod = new GetMethod();
            httpMethod.setRequestHeader("Content-type", "text/plain");
            httpMethod.setPath(url.getPath());

            httpClient.startSession(url);
            httpClient.executeMethod(httpMethod);

            byte[] sresponse = httpMethod.getResponseBody();
            log.fatal(".execute(): Response from remote server: " + new String(sresponse));
            httpClient.endSession();
        } catch (Exception e) {
            log.error(e);
        }
    }
}
