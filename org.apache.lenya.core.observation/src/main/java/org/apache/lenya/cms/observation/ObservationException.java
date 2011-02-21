/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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
package org.apache.lenya.cms.observation;

/**
 * Observation-specific exception.
 */
public class ObservationException extends Exception {

    private static final long serialVersionUID = -4025029670092614593L;

    /**
     * 
     */
    public ObservationException() {
        super();
    }

    /**
     * @param arg0
     * @param arg1
     */
    public ObservationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     */
    public ObservationException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public ObservationException(Throwable arg0) {
        super(arg0);
    }

}
