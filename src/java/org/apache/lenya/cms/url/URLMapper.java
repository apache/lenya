/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.url;

import org.apache.lenya.cms.repo.Area;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.Translation;

/**
 * Maps URLs to content items and vice versa.
 */
public interface URLMapper {
    
    /**
     * The Avalon role.
     */
    String ROLE = URLMapper.class.getName();

    /**
     * Maps a URL to a translation.
     * @param area The area.
     * @param url The URL inside the area.
     * @return A translation or <code>null</code> if the URL doesn't point to a translation.
     * @throws RepositoryException if a repository-related error occurs.
     */
    Translation getTranslation(Area area, String url) throws RepositoryException;
    
    /**
     * Maps a translation to a URL.
     * @param translation The translation.
     * @return A URL string or <code>null</code> if the translation is not accessible in the URL space.
     * @throws RepositoryException if a repository-related error occurs.
     */
    String getURL(Translation translation) throws RepositoryException;
    
}
