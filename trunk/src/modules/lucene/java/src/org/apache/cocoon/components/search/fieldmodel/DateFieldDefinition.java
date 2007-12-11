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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Field;

/**
 * Field Definition for Date type
 * 
 * @author Nicolas Maisonneuve
 */
public class DateFieldDefinition extends FieldDefinition {

    private SimpleDateFormat df;

    /**
     * @param name
     *            name of the field
     */
    public DateFieldDefinition(String name) {
        super(name, DATE);
    }

    /**
     * Set the date format to parse string date in the
     * 
     * @see #createLField(String) method
     * @param df
     */
    public void setDateFormat(SimpleDateFormat df) {
        this.df = df;
    }

    /**
     * @return the dateformat
     */
    public SimpleDateFormat getDateFormat() {
        return df;
    }

    /**
     * Create a Lucene Field
     * 
     * @param dateString
     *            String date in string format
     * @return A field.
     * @see org.apache.lucene.document.Field
     * 
     */
    public final Field createLField(String dateString)
            throws IllegalArgumentException {
        Date date = null;
        try {
            date = df.parse(dateString);
        } catch (ParseException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
        return createLField(date);
    }

    /**
     * Create Lucene Field
     * 
     * @param date
     *            the date
     * @return A field.
     * @see org.apache.lucene.document.Field
     * 
     */
    public final Field createLField(Date date) {
        return new Field(name, DateField.dateToString(date), store, true, index);
    }

}
