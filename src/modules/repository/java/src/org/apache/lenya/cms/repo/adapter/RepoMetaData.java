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
package org.apache.lenya.cms.repo.adapter;

import java.util.Arrays;
import java.util.HashMap;

import org.apache.lenya.cms.metadata.ElementSet;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.metadata.Element;

public class RepoMetaData implements MetaData {

    private org.apache.lenya.cms.repo.metadata.MetaData delegate;

    public RepoMetaData(org.apache.lenya.cms.repo.metadata.MetaData metaData) {
        this.delegate = metaData;
    }
    
    protected org.apache.lenya.cms.repo.metadata.MetaData getDelegate() {
        return this.delegate;
    }

    public void save() throws DocumentException {
    }

    public String[] getValues(String key) throws DocumentException {
        try {
            return delegate.getValues(key);
        } catch (RepositoryException e) {
            throw new DocumentException(e);
        }
    }

    public String getFirstValue(String key) throws DocumentException {
        try {
            if (delegate.getElementSet().getElement(key).isMultiple()) {
                String[] values = delegate.getValues(key);
                return values.length == 0 ? null : values[0];
            } else {
                return delegate.getValue(key);
            }
        } catch (RepositoryException e) {
            throw new DocumentException(e);
        }
    }

    public String[] getAvailableKeys() {
        throw new RuntimeException("not implemented");
    }

    public void setValue(String key, String value) throws DocumentException {
        try {
            if (delegate.getElementSet().getElement(key).isMultiple()) {
                delegate.clear(key);
                delegate.addValue(key, value);
            } else {
                delegate.setValue(key, value);
            }
        } catch (RepositoryException e) {
            throw new DocumentException(e);
        }
    }

    public void addValue(String key, String value) throws DocumentException {
        try {
            delegate.addValue(key, value);
        } catch (RepositoryException e) {
            throw new DocumentException(e);
        }
    }

    public void replaceBy(MetaData other) throws DocumentException {
        throw new RuntimeException("not implemented");
    }

    public String[] getPossibleKeys() {
        Element[] elements;
        try {
            elements = delegate.getElementSet().getElements();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        String[] names = new String[elements.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = elements[i].getName();
        }
        return names;
    }

    public HashMap getAvailableKey2Value() {
        throw new RuntimeException("not implemented");
    }

    public boolean isValidAttribute(String key) {
        return Arrays.asList(getPossibleKeys()).contains(key);
    }

    public long getLastModified() throws DocumentException {
        throw new RuntimeException("not implemented");
    }

    public ElementSet getElementSet() {
        // TODO Auto-generated method stub
        return null;
    }

}
