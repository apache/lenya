/*
 * $Id: CommandLineTask.java,v 1.8 2003/03/04 19:44:55 gregor Exp $
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
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.cms.task;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 */
public class CommandLineTask extends AbstractTask {

    /**
     * Creates a new CommandLineTask object.
     */
    public CommandLineTask() {
    }

    /**
     * Execute the task. All parameters must have been set with parameterize().
     *
     * @param path DOCUMENT ME!
     */
    public void execute(String path) throws ExecutionException {
        String command = getParameters().getParameter("command",
                "echo \"Exception: No command parameter\"");

        try {
            Process process = Runtime.getRuntime().exec(command);

            java.io.InputStream in = process.getInputStream();
            byte[] buffer = new byte[1024];
            int bytes_read = 0;
            java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();

            while ((bytes_read = in.read(buffer)) != -1) {
                baout.write(buffer, 0, bytes_read);
            }

            if (baout.toString().length() > 0) {
                throw new ExecutionException("%%%InputStream:S" + baout.toString() + "END:InputStream%%%");
            }

            java.io.InputStream in_e = process.getErrorStream();
            java.io.ByteArrayOutputStream baout_e = new java.io.ByteArrayOutputStream();

            while ((bytes_read = in_e.read(buffer)) != -1) {
                baout_e.write(buffer, 0, bytes_read);
            }

            if (baout_e.toString().length() > 0) {
                throw new ExecutionException("###ErrorStream:START" + baout_e.toString() + "END:ErrorStream###");
            }
        } catch (java.io.IOException e) {
            throw new ExecutionException(e);
        }
    }
}
