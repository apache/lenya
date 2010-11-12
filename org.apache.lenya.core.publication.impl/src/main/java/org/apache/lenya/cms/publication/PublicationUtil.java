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
package org.apache.lenya.cms.publication;

import java.util.Arrays;

/**
 * Publication utility.
 */
public class PublicationUtil {

    /**
     * Checks if a publication id is valid.
     * @param id
     * @return true if the id contains only lowercase letters and/or numbers, and is not an empty
     *         string.
     */
    public static boolean isValidPublicationID(String id) {
        return id.matches("[a-z0-9]+");
    }

    private static final String[] areas = { Publication.AUTHORING_AREA, Publication.DAV_AREA,
            Publication.STAGING_AREA, Publication.LIVE_AREA, Publication.ARCHIVE_AREA,
            Publication.TRASH_AREA };

    /**
     * Returns if a given string is a valid area name.
     * @param area The area string to test.
     * @return A boolean value.
     */
    public static boolean isValidArea(String area) {
        return area != null && Arrays.asList(areas).contains(area);
    }

}
