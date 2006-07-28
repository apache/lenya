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
package org.apache.lenya.cms.publication;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.lenya.cms.site.SiteNode;

public class DocumentLocator {

    private static Map locators = new WeakHashMap();

    public static DocumentLocator getLocator(String pubId, String area, String path, String language) {
        String key = DocumentLocator.getKey(pubId, area, path, language);
        DocumentLocator locator = null;
        if (locators.containsKey(key)) {
            locator = (DocumentLocator) locators.get(key);
        } else {
            locator = new DocumentLocator(pubId, area, path, language);
            locators.put(key, locator);
        }
        return locator;
    }

    protected static final String getKey(String pubId, String area, String path, String language) {
        return pubId + ":" + area + ":" + path + ":" + language;
    }

    private String pubId;
    private String area;
    private String path;
    private String language;

    protected DocumentLocator(String pubId, String area, String path, String language) {
        this.path = path;
        this.pubId = pubId;
        this.area = area;
        this.language = language;
    }

    public String getArea() {
        return area;
    }

    public String getLanguage() {
        return language;
    }

    public String getPath() {
        return path;
    }

    public String getPublicationId() {
        return pubId;
    }

    public DocumentLocator getPathVersion(String path) {
        return DocumentLocator.getLocator(getPublicationId(), getArea(), path, getLanguage());
    }

    public DocumentLocator getDescendant(String relativePath) {
        if (relativePath.length() == 0) {
            throw new IllegalArgumentException("The relative path must not be empty!");
        }
        return getPathVersion(getPath() + "/" + relativePath);
    }

    public DocumentLocator getChild(String step) {
        if (step.indexOf("/") > -1) {
            throw new IllegalArgumentException("The step [" + step + "] must not contain a slash!");
        }
        return getDescendant(step);
    }

    public DocumentLocator getParent() {
        int lastSlashIndex = getPath().lastIndexOf("/");
        if (lastSlashIndex > -1) {
            String parentPath = getPath().substring(0, lastSlashIndex);
            return getPathVersion(parentPath);
        } else {
            return null;
        }
    }

    public DocumentLocator getParent(String defaultPath) {
        DocumentLocator parent = getParent();
        if (parent != null) {
            return parent;
        } else {
            return getPathVersion(defaultPath);
        }
    }

    public DocumentLocator getLanguageVersion(String language) {
        return DocumentLocator.getLocator(getPublicationId(), getArea(), getPath(), language);
    }

    protected String getKey() {
        return DocumentLocator.getKey(getPublicationId(), getArea(), getPath(), getLanguage());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DocumentLocator)) {
            return false;
        }
        DocumentLocator locator = (DocumentLocator) obj;
        return locator.getKey().equals(getKey());
    }

    public int hashCode() {
        return getKey().hashCode();
    }

    public String toString() {
        return getKey();
    }

    public DocumentLocator getAreaVersion(String area) {
        return DocumentLocator.getLocator(getPublicationId(), area, getPath(), getLanguage());
    }

}
