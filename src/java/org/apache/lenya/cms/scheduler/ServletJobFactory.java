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

/* $Id: ServletJobFactory.java,v 1.12 2004/03/01 16:18:26 gregor Exp $  */

package org.apache.lenya.cms.scheduler;

import org.apache.log4j.Category;


/**
 * Factory for building serlvet jobs.
 */
public final class ServletJobFactory {
    
    /**
     * Ctor.
     */
    private ServletJobFactory() {
    }

    private static Category log = Category.getInstance(ServletJobFactory.class);

    /**
     * Creates a job.
     * @param jobClassName The name of the Java class used to instanciate the job object.
     * @return A servlet job.
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
     * Creates a job. 
     *
     * @param cl The Java class used to instanciate the job object.
     *
     * @return A servlet job.
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
