/*
 * Copyright 1999-2004 The Apache Software Foundation Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */

/* $Id: UploadHelper.java,v 1.5 2004/08/16 12:10:52 andreas Exp $ */

package org.apache.lenya.cms.authoring;

import org.apache.cocoon.environment.Request;
import org.apache.cocoon.servlet.multipart.Part;
import org.apache.log4j.Category;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Helper class for uploading files.
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann </a>
 * @author <a href="mailto:michi@apache.org">Michael Wechner </a>
 * @version $Id: UploadHelper.java,v 1.5 2004/08/16 12:10:52 andreas Exp $
 */
public class UploadHelper {

    private static final Category log = Category.getInstance(UploadHelper.class);

    private File directory;

    /**
     * Ctor.
     * @param directory The directory to save the files to.
     */
    public UploadHelper(File directory) {
        this.directory = directory;
    }

    /**
     * Ctor.
     * @param directoryPath The path of the directory to save the files to.
     */
    public UploadHelper(String directoryPath) {
        this.directory = new File(directoryPath);
    }

    /**
     * Save uploaded file
     * @param part The part of the multipart request.
     * @return <code>true</code> if the upload succeeded, <code>false</code> otherwise.
     */
    public boolean save(Part part) {

        File file = new File(directory, part.getFileName());
        if (log.isDebugEnabled()) {
            log.debug("Uploading file: [" + file.getAbsolutePath() + "]");
        }

        if (!directory.isDirectory()) {
            directory.mkdirs();
            if (log.isInfoEnabled()) {
                log.info("Directory has been created: [" + directory + "]");
            }
        }

        InputStream in = null;
        OutputStream out =  null;
        try {
            out = new FileOutputStream(file.getAbsolutePath());
            in = part.getInputStream();
            byte[] buf = new byte[4096];
            int read = in.read(buf);

            while (read > 0) {
                out.write(buf, 0, read);
                read = in.read(buf);
            }
        } catch (Exception e) {
            log.error(e);
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                log.error(e);
            }
        }
        return true;
    }

    /**
     * Saves the a file the request for a certain request parameter name.
     * @param requestParameter The name of the &lt;input type="file"/&gt; request parameter value.
     * @return The saved file or <code>null</code> if the upload was not successful.
     * @throws Exception when something went wrong.
     */
    public File save(Request request, String requestParameter) throws Exception {

        Part part = (Part) request.get(requestParameter);

        File file = null;

        boolean success = save(part);
        if (success) {
            file = new File(directory, part.getFileName());
        }

        return file;
    }

}