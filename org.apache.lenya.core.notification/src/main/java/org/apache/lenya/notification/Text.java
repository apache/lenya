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
package org.apache.lenya.notification;

import java.util.Arrays;

import org.apache.lenya.util.Assert;

/**
 * A translatable text with optional i18n parameters.
 */
public class Text {

    private String text;
    private Text[] parameters;
    private boolean translate;

    /**
     * @param text The text.
     * @param translate if the text should be translated.
     */
    public Text(String text, boolean translate) {
        Assert.notNull("text", text);
        this.text = text;
        this.translate = translate;
    }

    /**
     * Text objects built using this constructor are always translated.
     * @param text The text.
     * @param params The parameters.
     */
    public Text(String text, Text[] params) {
        this(text, true);
        this.parameters = params;
    }

    /**
     * Convenience constructor which allows to pass a string array instead of a
     * text array as parameters.
     * @param text The text.
     * @param params The parameters.
     */
    public Text(String text, String[] params) {
        this(text, convert(params));
    }

    protected static Text[] convert(String[] params) {
        Text[] textParams = new Text[params.length];
        for (int i = 0; i < params.length; i++) {
            textParams[i] = new Text(params[i], false);
        }
        return textParams;
    }

    /**
     * @return The text.
     */
    public String getText() {
        return this.text;
    }

    /**
     * @return if the text should be translated.
     */
    public boolean translate() {
        return this.translate;
    }

    /**
     * @return The parameters.
     */
    public Text[] getParameters() {
        return this.parameters == null ? new Text[0] : cloneArray(this.parameters);
    }

    protected static Text[] cloneArray(Text[] params) {
        return (Text[]) Arrays.asList(params).toArray(new Text[params.length]);
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.text).append(" ").append(this.parameters);
        return buf.toString();
    }

}
