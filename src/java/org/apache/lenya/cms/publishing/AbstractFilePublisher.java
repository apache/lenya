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

/* $Id: AbstractFilePublisher.java,v 1.10 2004/03/01 16:18:18 gregor Exp $  */

package org.apache.lenya.cms.publishing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * @deprecated use the publish ant task instead.
 */
public abstract class AbstractFilePublisher extends AbstractPublisher {
    /**
     * Utility function to copy a source file to destination
     *
     * @param source a <code>File</code> value
     * @param destination a <code>File</code> value
     *
     * @exception IOException if an error occurs
     * @throws FileNotFoundException DOCUMENT ME!
     */
    protected void copyFile(File source, File destination)
        throws IOException, FileNotFoundException {
        if (!source.exists()) {
            throw new FileNotFoundException();
        }

        File parentDestination = new File(destination.getParent());

        if (!parentDestination.exists()) {
            parentDestination.mkdirs();
        }

        org.apache.avalon.excalibur.io.FileUtil.copyFile(source, destination);
    }
}
