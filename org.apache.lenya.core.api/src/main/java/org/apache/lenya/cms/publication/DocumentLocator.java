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

import java.util.Map;
import java.util.WeakHashMap;

/**
 * A DocumentLocator describes a document based on its path in the site structure. The actual
 * document doesn't have to exist.
 */
public class DocumentLocator {

    private static Map locators = new WeakHashMap();

    /**
     * Returns a specific document locator.
     * @param pubId The publication ID.
     * @param area The area of the document.
     * @param path The path of the document in the site structure.
     * @param language The language of the document.
     * @return A document locator.
     */
    public static DocumentLocator getLocator(String pubId, String area, String path, String language) {
        String key = DocumentLocator.getKey(pubId, area, path, language);
        DocumentLocator locator = (DocumentLocator) locators.get(key);
        if (locator == null) {
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

    /**
     * @return The area of the document.
     */
    public String getArea() {
        return area;
    }

    /**
     * @return The language of the document.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @return The path of the document in the site structure.
     */
    public String getPath() {
        return path;
    }

    /**
     * @return The publication ID.
     */
    public String getPublicationId() {
        return pubId;
    }

    /**
     * Returns a locator with the same publication ID, area, and language, but a different path in
     * the site structure.
     * @param path The path.
     * @return A document locator.
     */
    public DocumentLocator getPathVersion(String path) {
        return DocumentLocator.getLocator(getPublicationId(), getArea(), path, getLanguage());
    }

    /**
     * Returns a descendant of this locator.
     * @param relativePath The relative path which must not begin with a slash and must not be
     *            empty.
     * @return A document locator.
     */
    public DocumentLocator getDescendant(String relativePath) {
        if (relativePath.length() == 0) {
            throw new IllegalArgumentException("The relative path must not be empty!");
        }
        if (relativePath.startsWith("/")) {
            throw new IllegalArgumentException("The relative path must not start with a slash!");
        }
        return getPathVersion(getPath() + "/" + relativePath);
    }

    /**
     * Returns a child of this locator.
     * @param step The relative path to the child, it must not contain a slash.
     * @return A document locator.
     */
    public DocumentLocator getChild(String step) {
        if (step.indexOf("/") > -1) {
            throw new IllegalArgumentException("The step [" + step + "] must not contain a slash!");
        }
        return getDescendant(step);
    }

    /**
     * Returns the parent of this locator.
     * @return A document locator or <code>null</code> if this is the root locator.
     */
    public DocumentLocator getParent() {
        int lastSlashIndex = getPath().lastIndexOf("/");
        if (lastSlashIndex > -1) {
            String parentPath = getPath().substring(0, lastSlashIndex);
            return getPathVersion(parentPath);
        } else {
            return null;
        }
    }

    /**
     * Returns the parent of this locator.
     * @param defaultPath The path of the locator to return if this is the root locator.
     * @return A document locator.
     */
    public DocumentLocator getParent(String defaultPath) {
        DocumentLocator parent = getParent();
        if (parent != null) {
            return parent;
        } else {
            return getPathVersion(defaultPath);
        }
    }

    /**
     * Returns a locator with the same publication ID, area, and path, but with a different
     * language.
     * @param language The language.
     * @return A document locator.
     */
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

    /**
     * Returns a locator with the same publication ID, path, and language, but with a different
     * area.
     * @param area The area.
     * @return A document locator.
     */
    public DocumentLocator getAreaVersion(String area) {
        return DocumentLocator.getLocator(getPublicationId(), area, getPath(), getLanguage());
    }

}
