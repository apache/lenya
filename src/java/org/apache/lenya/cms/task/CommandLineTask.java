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

/* $Id: CommandLineTask.java,v 1.13 2004/03/01 16:18:20 gregor Exp $  */

package org.apache.lenya.cms.task;


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
