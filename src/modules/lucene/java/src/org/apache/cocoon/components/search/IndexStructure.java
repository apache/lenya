/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.components.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.cocoon.components.search.components.Indexer;
import org.apache.cocoon.components.search.fieldmodel.FieldDefinition;

/**
 * Index Definition class, contain all the index field definitions.
 * 
 * @author Nicolas Maisonneuve
 * 
 */
public final class IndexStructure {

    private Map fielddefs;

    public IndexStructure() {
        fielddefs = new HashMap();

        // A index has always an UID field
        FieldDefinition fielddef = FieldDefinition.create(
                Indexer.DOCUMENT_UID_FIELD, FieldDefinition.KEYWORD);
        fielddef.setStore(true);
        this.addFieldDef(fielddef);

    }

    /**
     * add a fieldDefiniition to the indexDefinition
     * 
     * @param fielddef
     */
    public void addFieldDef(FieldDefinition fielddef) {
        if (fielddefs.containsKey(fielddef.name())) {
            throw new IllegalArgumentException(" field with the name "
                    + fielddef.name() + " is already used");
        }
        fielddefs.put(fielddef.name(), fielddef);
    }

    /**
     * @return all fieldnames contained in the index
     */
    public final String[] getFieldNames() {
        Set results = fielddefs.keySet();
        return (String[]) results.toArray(new String[results.size()]);
    }

    /**
     * return all fieldDefinitions
     * 
     * @return FieldDefinition[]
     */
    public final FieldDefinition[] getFieldDef() {
        Collection results = fielddefs.values();
        return (FieldDefinition[]) results.toArray(new FieldDefinition[results
                .size()]);
    }

    /**
     * Return the fieldDefinition associated to the name
     * 
     * @param fieldname
     *            String the name of the fieldDefiniation
     * @return FieldDefinition
     */
    public final FieldDefinition getFieldDef(String fieldname) {
        return (FieldDefinition) fielddefs.get(fieldname);
    }

    /**
     * check if this field exist
     * 
     * @param name
     *            the field's name
     * @return true if a field with this name exist
     */
    public final boolean hasField(String name) {
        return fielddefs.containsKey(name.intern());
    }

    public String toString() {
        StringBuffer result = new StringBuffer("DocumentFactory:");
        Iterator iter = this.fielddefs.values().iterator();
        while (iter.hasNext()) {
            FieldDefinition item = (FieldDefinition) iter.next();
            result.append("\n").append(item.toString());
        }
        return result.toString();
    }

}
