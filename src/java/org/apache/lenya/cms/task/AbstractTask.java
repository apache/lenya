/*
 * $Id: AbstractTask.java,v 1.3 2003/02/07 12:14:22 ah Exp $
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
package org.wyona.cms.task;

import org.apache.avalon.framework.parameters.Parameters;

import org.apache.log4j.Category;

import java.util.StringTokenizer;


/**
 * DOCUMENT ME!
 *
 * @author ah
 */
public abstract class AbstractTask implements Task {
    static Category log = Category.getInstance(AbstractTask.class);
    private Parameters parameters = new Parameters();
    private String label = "default task";

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Parameters getParameters() {
        Parameters params = new Parameters();
        params = params.merge(parameters);

        return params;
    }

    /**
     * DOCUMENT ME!
     *
     * @param parameters DOCUMENT ME!
     */
    public void parameterize(Parameters parameters) {
        log.debug("Initializing parameters");

        for (int i = 0; i < parameters.getNames().length; i++) {
            log.debug("Setting parameter " + parameters.getNames()[i] + " to " +
                parameters.getParameter(parameters.getNames()[i], "default"));
        }

        this.parameters = this.parameters.merge(parameters);
    }

    /**
     * Return the label to be displayed.
     *
     * @return DOCUMENT ME!
     */
    public String getLabel() {
        return label;
    }

    /**
     * DOCUMENT ME!
     *
     * @param label DOCUMENT ME!
     */
    public void setLabel(String label) {
        this.label = label;
    }
}
