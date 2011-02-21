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
package org.apache.lenya.cms.metadata;

//florent import org.apache.lenya.cms.repository.metadata.MetaDataException;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.metadata.MetaData;

public class MetaDataWrapper implements MetaData {

    //florent private org.apache.lenya.cms.repository.metadata.MetaData delegate;
	private MetaData delegate;

    //florent public MetaDataWrapper(org.apache.lenya.cms.repository.metadata.MetaData delegate) {
	public MetaDataWrapper(MetaData delegate) {
        this.delegate = delegate;
    }

    //florent protected org.apache.lenya.cms.repository.metadata.MetaData getDelegate() {
	protected MetaData getDelegate() {
        return this.delegate;
    }

    public void addValue(String key, String value)
            //florent throws org.apache.lenya.cms.metadata.MetaDataException {
    throws MetaDataException {
        try {
            this.delegate.addValue(key, value);
        } catch (MetaDataException e) {
            throw new org.apache.lenya.cms.metadata.MetaDataException(e);
        }
    }

    public void forcedReplaceBy(MetaData other)
            //florent throws org.apache.lenya.cms.metadata.MetaDataException {
    throws MetaDataException {
        MetaDataWrapper wrapper = (MetaDataWrapper) other;
        try {
            this.delegate.forcedReplaceBy(wrapper.getDelegate());
        } catch (MetaDataException e) {
            throw new org.apache.lenya.cms.metadata.MetaDataException(e);
        }
    }

    public String[] getAvailableKeys() {
        return this.delegate.getAvailableKeys();
    }

    private ElementSet elements;

    public ElementSet getElementSet() {
        if (this.elements == null) {
            this.elements = new ElementSetWrapper(this.delegate.getElementSet());
        }
        return this.elements;
    }

    public String getFirstValue(String key) throws org.apache.lenya.cms.metadata.MetaDataException {
        try {
            return this.delegate.getFirstValue(key);
        } catch (MetaDataException e) {
            throw new org.apache.lenya.cms.metadata.MetaDataException(e);
        }
    }

    public long getLastModified() throws org.apache.lenya.cms.metadata.MetaDataException {
        try {
            return this.delegate.getLastModified();
        } catch (MetaDataException e) {
            throw new org.apache.lenya.cms.metadata.MetaDataException(e);
        }
    }

    public String[] getPossibleKeys() {
        return this.delegate.getPossibleKeys();
    }

    public String[] getValues(String key) throws org.apache.lenya.cms.metadata.MetaDataException {
        try {
            return this.delegate.getValues(key);
        } catch (MetaDataException e) {
            throw new org.apache.lenya.cms.metadata.MetaDataException(e);
        }
    }

    public boolean isValidAttribute(String key) {
        return this.delegate.isValidAttribute(key);
    }

    public void removeAllValues(String key) throws org.apache.lenya.cms.metadata.MetaDataException {
        try {
            this.delegate.removeAllValues(key);
        } catch (MetaDataException e) {
            throw new org.apache.lenya.cms.metadata.MetaDataException(e);
        }

    }

    public void replaceBy(MetaData other) throws org.apache.lenya.cms.metadata.MetaDataException {
        try {
            MetaDataWrapper wrapper = (MetaDataWrapper) other;
            this.delegate.replaceBy(wrapper.getDelegate());
        } catch (MetaDataException e) {
            throw new org.apache.lenya.cms.metadata.MetaDataException(e);
        }

    }

    public void setValue(String key, String value)
            throws org.apache.lenya.cms.metadata.MetaDataException {
        try {
            this.delegate.setValue(key, value);
        } catch (MetaDataException e) {
            throw new org.apache.lenya.cms.metadata.MetaDataException(e);
        }

    }

}
