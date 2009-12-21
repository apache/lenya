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
package org.apache.cocoon.components.search.fieldmodel;

import org.apache.lucene.document.Field;

/**
 * Field Definition class
 * 
 * @author Nicolas Maisonneuve
 * 
 */
public abstract class FieldDefinition {
    /**
     * Text type
     */
    public static final int TEXT = 0;

    /**
     * Keyword type
     */
    public static final int KEYWORD = 1;

    /**
     * Date type
     */
    public static final int DATE = 2;

    public static final String[] STRING_TYPE = { "text", "keyword", "date" };

    /**
     * Name of the field
     */
    protected String name;

    /**
     * type of the field (text, keyword, date)
     */
    protected int type;

    /**
     * Lucene Field specification
     */
    protected boolean store;

    protected boolean index;

    // futur lucene 1.9: protected Field.Store store;
    // futur lucene 1.9: protected Field.Index index;

    protected FieldDefinition(String name, String type)
            throws IllegalArgumentException {
        this(name, stringTotype(type));
    }

    protected FieldDefinition(String name, int type)
            throws IllegalArgumentException {
        this(name, type, false);
    }

    public static FieldDefinition create(String name, int type) {
        FieldDefinition field = null;

        if (name == null || name.trim().equals("")) {
            throw new IllegalArgumentException("name cannot be empty");
        }
        switch (type) {
        case TEXT:
        case KEYWORD:
            field = new StringFieldDefinition(name, type);
            break;
        case DATE:
            field = new DateFieldDefinition(name);
            break;
        default:
            throw new IllegalArgumentException("type not allowed");
        }
        return field;
    }

    /**
     * 
     * @param name
     *            String field's name
     * @param type
     *            int indexation type
     * @param store
     *            boolean store value in the index
     * @throws IllegalArgumentException
     */
    private FieldDefinition(String name, int type, boolean store)
            throws IllegalArgumentException {

        this.name = name.intern();
        setType(type);
        setStore(store);
    }

    public int hashCode() {
        return name.hashCode() * this.type;
    }

    public void setStore(boolean store) {
        // for futur lucene1.9
        // this.store=(store)?Field.Store.YES:Field.Store.NO;
        this.store = store;
    }

    public boolean getStore() {
        // for futur lucene1.9 return this.store==Field.Store.YES;
        return store;
    }

    public boolean equals(FieldDefinition fielddef) {
        if (name.equals(fielddef.name()) && getType() == fielddef.getType()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean equals(Object object) {
        if (object instanceof FieldDefinition) {
            return equals((FieldDefinition) object);
        } else {
            return false;
        }
    }

    public String name() {
        return name;
    }

    /**
     * Create Lucene Field
     * 
     * @param value
     *            String value to store in the lucene field
     * @return Field
     */
    public abstract Field createLField(String value);

    public int getType() {
        return type;
    }

    /**
     * Set the type of the FieldDefinition (DATE,TEXT,KEYWORD)
     * 
     * @param type
     *            int
     * @throws IllegalArgumentException
     */
    private void setType(int type) throws IllegalArgumentException {
        switch (type) {
        case FieldDefinition.TEXT:
            index = true;
            break;
        case FieldDefinition.DATE:
            index = true;
            break;
        case FieldDefinition.KEYWORD:
            index = false;
            break;
        default:
            throw new IllegalArgumentException("type not allowed");
        }
        this.type = type;
    }

    public final String toString() {
        StringBuffer b = new StringBuffer();
        b.append("name: " + name);
        b.append(", type: " + FieldDefinition.STRING_TYPE[type]);
        b.append(", store: " + getStore());
        return b.toString();
    }

    /**
     * Convert String to type
     * 
     * @param typename
     *            String
     * @return int
     */
    static final public int stringTotype(String typename)
            throws IllegalArgumentException {
        for (int i = 0; i < STRING_TYPE.length; i++) {
            if (typename.toLowerCase().equals(STRING_TYPE[i])) {
                return i;
            }
        }
        throw new IllegalArgumentException("type " + typename
                + " is not allowed");
    }

}
