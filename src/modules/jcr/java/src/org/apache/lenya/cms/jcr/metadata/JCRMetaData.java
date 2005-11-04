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
package org.apache.lenya.cms.jcr.metadata;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Value;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.publication.DocumentException;

/**
 * JCR based meta data.
 */
public class JCRMetaData extends AbstractLogEnabled implements MetaData {

    private String namespace;
    private String sourceUri;
    protected ServiceManager manager;

    private Map key2values = null;

    public JCRMetaData(String namespace, String sourceUri, ServiceManager manager, Logger logger) {
        this.namespace = namespace;
        this.sourceUri = sourceUri;
        this.manager = manager;
        ContainerUtil.enableLogging(this, logger);
    }

    protected Map getKey2Values() {
        if (this.key2values == null) {
            load();
        }
        return this.key2values;
    }

    protected void load() {

        this.key2values = new HashMap();
        SourceResolver resolver = null;
        /*
        JCRNodeSource source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = (JCRNodeSource) resolver.resolveURI(this.sourceUri);

            Node node = source.getNode();
            String prefix = node.getSession()
                    .getWorkspace()
                    .getNamespaceRegistry()
                    .getPrefix(this.namespace);
            if (!prefix.equals("")) {
                prefix = prefix + ":";
            }

            String possibleKeys[] = getPossibleKeys();
            for (int i = 0; i < possibleKeys.length; i++) {
                String key = prefix + possibleKeys[i];
                if (node.hasProperty(key)) {
                    Property property = node.getProperty(key);
                    Value[] values = property.getValues();
                    String[] stringValues = new String[values.length];
                    for (int v = 0; v < values.length; v++) {
                        stringValues[v] = values[v].getString();
                    }
                    this.key2values.put(possibleKeys[i], stringValues);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
        */
    }

    public void save() throws DocumentException {
        SourceResolver resolver = null;
        /*
        JCRNodeSource source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = (JCRNodeSource) resolver.resolveURI(this.sourceUri);

            if (!source.exists()) {
                OutputStream stream = source.getOutputStream();
                stream.flush();
                stream.close();
            }
            
            Node node = source.getNode();
            String prefix = node.getSession()
                    .getWorkspace()
                    .getNamespaceRegistry()
                    .getPrefix(this.namespace);
            if (!prefix.equals("")) {
                prefix = prefix + ":";
            }

            String possibleKeys[] = getPossibleKeys();
            for (int i = 0; i < possibleKeys.length; i++) {
                String[] stringValues = (String[]) getKey2Values().get(possibleKeys[i]);
                String key = prefix + possibleKeys[i];
                node.setProperty(key, stringValues);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
        */
    }

    public String[] getValues(String key) throws DocumentException {
        return (String[]) getKey2Values().get(key);
    }

    public String getFirstValue(String key) throws DocumentException {
        String value = null;
        String[] values = (String[]) getKey2Values().get(key);
        if (values.length > 0) {
            value = values[0];
        }
        return value;
    }

    public void setValue(String key, String value) throws DocumentException {
        String[] values = { value };
        getKey2Values().put(key, values);
    }

    public void addValue(String key, String value) throws DocumentException {
        String[] values = (String[]) getKey2Values().get(key);
        List valueList = Arrays.asList(values);
        valueList.add(value);
        values = (String[]) valueList.toArray(new String[valueList.size()]);
        getKey2Values().put(key, values);
    }

    public void replaceBy(MetaData other) throws DocumentException {
        this.key2values = new HashMap();
        String[] keys = getPossibleKeys();
        for (int i = 0; i < keys.length; i++) {
            String[] values = other.getValues(keys[i]);
            this.key2values.put(keys[i], values);
        }
        save();
    }

    private String[] possibleKeys = new String[0];

    public void setPossibleKeys(String[] keys) {
        this.possibleKeys = keys;
    }

    public String[] getPossibleKeys() {
        return this.possibleKeys;
    }

    public boolean isValidAttribute(String key) {
        String[] keys = getPossibleKeys();
        return Arrays.asList(keys).contains(key);
    }

}
