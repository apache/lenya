/*
$Id: CommandLineTask.java,v 1.12 2003/07/09 13:44:51 egli Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.task;


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
     *  (non-Javadoc)
     * @see org.apache.lenya.cms.task.Task#execute(java.lang.String)
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
                throw new ExecutionException("%%%InputStream:S" + baout.toString() +
                    "END:InputStream%%%");
            }

            java.io.InputStream in_e = process.getErrorStream();
            java.io.ByteArrayOutputStream baout_e = new java.io.ByteArrayOutputStream();

            while ((bytes_read = in_e.read(buffer)) != -1) {
                baout_e.write(buffer, 0, bytes_read);
            }

            if (baout_e.toString().length() > 0) {
                throw new ExecutionException("###ErrorStream:START" + baout_e.toString() +
                    "END:ErrorStream###");
            }
        } catch (java.io.IOException e) {
            throw new ExecutionException(e);
        }
    }
}
