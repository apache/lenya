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

/* $Id: UploadHelper.java,v 1.1 2004/03/15 14:49:43 michi Exp $  */

package org.apache.lenya.cms.authoring;

import org.apache.cocoon.servlet.multipart.Part;
import org.apache.log4j.Category;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 *
 */
public class UploadHelper {
    Category log = Category.getInstance(UploadHelper.class);

    private String baseDir;

    /**
     *
     */
    public UploadHelper(String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * Save uploaded file
     * 
     * @param part
     */
    public boolean save(Part part) {

        File file = new File(baseDir + File.separator + part.getFileName());
        log.error(file.getAbsolutePath());
        File parent = new File(file.getParent());
        if (!parent.isDirectory()) {
            parent.mkdirs();
            log.info("Directory has been created: " + parent);
        }
	
	try {
            FileOutputStream out = new FileOutputStream(file.getAbsolutePath());
	    InputStream in = part.getInputStream();
            byte[] buf = new byte[4096];
            int read = in.read(buf);

            while (read > 0) {
                out.write(buf, 0, read);
                read = in.read(buf);
            }
        } catch (IOException e) {
            log.error(e);
            return false;
        } catch (Exception e) {
            log.error(e);
            return false;
        }
        return true;
    }
}
