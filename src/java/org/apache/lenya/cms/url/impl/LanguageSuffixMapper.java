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
package org.apache.lenya.cms.url.impl;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.lenya.cms.repo.Area;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.Site;
import org.apache.lenya.cms.repo.SiteNode;
import org.apache.lenya.cms.repo.Translation;
import org.apache.lenya.cms.url.URLMapper;

/**
 * URL mapper which uses a language suffix.
 */
public class LanguageSuffixMapper extends AbstractLogEnabled implements URLMapper, Configurable {

    /**
     * Ctor.
     */
    public LanguageSuffixMapper() {
    }

    public Translation getTranslation(Area area, final String url) throws RepositoryException {

        String strippedUrl = removeExtensions(url);

        String language = getLanguage(strippedUrl);
        String fullLanguage = "".equals(language) ? "" : ("_" + language);
        final String path = strippedUrl.substring(0, strippedUrl.length() - fullLanguage.length());

        if ("".equals(language)) {
            return null;
        }

        if (!path.startsWith("/")) {
            throw new RuntimeException("Path [" + path + "] does not start with '/'!");
        }

        SiteNode siteNode = area.getSite().getNode(path);

        Translation document = null;
        if (siteNode != null) {
            document = siteNode.getAsset().getTranslation(language);
        }
        return document;
    }

    /**
     * Removes all "."-separated extensions from a URL (e.g., <code>/foo.print.html</code> is
     * transformed to <code>/foo</code>).
     * @param url The URL to trim.
     * @return A URL string.
     */
    protected String removeExtensions(String url) {
        int dotIndex = url.indexOf(".");
        if (dotIndex > -1) {
            url = url.substring(0, dotIndex);
        }
        return url;
    }

    /**
     * Returns the language of a URL.
     * @param urlWithoutSuffix The URL without the suffix.
     * @return A string.
     */
    protected String getLanguage(String urlWithoutSuffix) {

        String language = "";
        String url = urlWithoutSuffix;

        int languageSeparatorIndex = url.lastIndexOf("_");
        if (languageSeparatorIndex > -1) {
            String suffix = url.substring(languageSeparatorIndex + 1);
            if (suffix.length() <= 5) {
                language = suffix;
            }
        }
        return language;
    }

    public String getURL(Translation translation) throws RepositoryException {
        Site site = translation.getAsset().getContent().getArea().getSite();
        SiteNode node = site.getFirstReference(translation.getAsset());
        String url = node.getPath() + getSeparator() + translation.getLanguage() + ".html";
        return url;
    }

    protected static final String DEFAULT_SEPARATOR = "_";

    private String separator = DEFAULT_SEPARATOR;

    protected String getSeparator() {
        return this.separator;
    }

    public void configure(Configuration config) throws ConfigurationException {
        Configuration separatorConfig = config.getChild("separator", false);
        if (separatorConfig != null) {
            this.separator = separatorConfig.getValue();
        }
    }

}
