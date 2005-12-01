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
package org.apache.lenya.cms.jcr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jcr.Value;

import org.apache.lenya.cms.jcr.mapping.PropertyNodeProxy;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.metadata.ElementSet;
import org.apache.lenya.cms.repo.metadata.MetaData;

/**
 * Meta data proxy.
 */
public class MetaDataProxy extends PropertyNodeProxy implements MetaData {

    protected static final String NODE_NAME = "lenya:meta";
    protected static final String NODE_TYPE = "lnt:meta";
    protected static final String ELEMENT_SET_PROPERTY = "lenya:elementSet";

    protected String getPropertyName() {
        return ELEMENT_SET_PROPERTY;
    }

    public String getValue(String name) throws RepositoryException {
        if (getElementSet().getElement(name).isMultiple()) {
            throw new RepositoryException("The element [" + name + "] is multiple.");
        }
        try {
            if (getNode().hasProperty(name)) {
                return getNode().getProperty(name).getString();
            } else {
                return null;
            }
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public String[] getValues(String name) throws RepositoryException {
        if (!getElementSet().getElement(name).isMultiple()) {
            throw new RepositoryException("The element [" + name + "] is not multiple.");
        }
        try {
            if (getNode().hasProperty(name)) {
                Value[] values = getNode().getProperty(name).getValues();
                String[] strings = new String[values.length];
                for (int i = 0; i < values.length; i++) {
                    strings[i] = values[i].getString();
                }
                return strings;
            } else {
                return new String[0];
            }
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public void setValue(String name, String value) throws RepositoryException {
        if (getElementSet().getElement(name).isMultiple()) {
            throw new RepositoryException("The element [" + name + "] is multiple.");
        }
        try {
            getNode().setProperty(name, value);
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public void addValue(String name, String value) throws RepositoryException {
        if (!getElementSet().getElement(name).isMultiple()) {
            throw new RepositoryException("The element [" + name + "] is not multiple.");
        }
        try {
            List values = new ArrayList(Arrays.asList(getValues(name)));
            values.add(value);
            getNode().setProperty(name, (String[]) values.toArray(new String[values.size()]));
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public void clear(String name) throws RepositoryException {
        try {
            getNode().getProperty(name).remove();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public void removeValue(String name, String value) throws RepositoryException {
        if (!getElementSet().getElement(name).isMultiple()) {
            throw new RepositoryException("The element [" + name + "] is not multiple.");
        }
        try {
            List values = new ArrayList(Arrays.asList(getValues(name)));
            if (!values.contains(value)) {
                throw new RepositoryException("The element [" + name
                        + "] does not contain the value [" + value + "].");
            }
            values.remove(value);
            getNode().setProperty(name, (String[]) values.toArray(new String[values.size()]));
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public ElementSet getElementSet() throws RepositoryException {
        String elementSetName = getPropertyString(ELEMENT_SET_PROPERTY);
        return getRepository().getMetaDataRegistry().getElementSet(elementSetName);
    }

}
