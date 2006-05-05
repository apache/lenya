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

package org.apache.lenya.config.core;

import java.io.File;

/**
 * File Configuration
 */
abstract public class FileConfiguration extends Configuration {

    private File filenameDefault;
    private File filenameLocal;

    /**
     *
     */
    public File getFilenameDefault() {
        return filenameDefault;
    }

    /**
     *
     */
    public File getFilenameLocal() {
        return filenameLocal;
    }

    /**
     *
     */
    public void setFilenameDefault(String filename) {
        this.filenameDefault = new File(filename);
    }

    /**
     * @param String Filename
     */
    public void setFilenameLocal(String filename) {
        this.filenameLocal = new File(filename);
    }

    /**
     *
     */
    public boolean localConfigExists() {
        if (getFilenameLocal().exists()) {
            return true;
        } else {
            return false;
        }
    }
}
