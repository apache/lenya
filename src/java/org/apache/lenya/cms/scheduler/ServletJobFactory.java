/*
 * $Id: ServletJobFactory.java,v 1.3 2003/03/04 17:46:35 gregor Exp $
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
/*
 * ServletJobFactory.java
 *
 * Created on November 8, 2002, 2:08 PM
 */
package org.lenya.cms.scheduler;

import org.apache.log4j.Category;


/**
 * DOCUMENT ME!
 *
 * @author ah
 */
public class ServletJobFactory {
    static Category log = Category.getInstance(ServletJobFactory.class);

    /**
     * DOCUMENT ME!
     *
     * @param jobClassName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static ServletJob createJob(String jobClassName) {
        try {
            Class cl = Class.forName(jobClassName);

            return createJob(cl);
        } catch (Exception e) {
            log.error("Cannot create Job instance: " + e);

            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param cl DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static ServletJob createJob(Class cl) {
        try {
            ServletJob job = (ServletJob) cl.newInstance();

            return job;
        } catch (Exception e) {
            log.error("Cannot create Job instance: " + e);

            return null;
        }
    }
}
