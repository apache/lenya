/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.commons.lang.LocaleUtils;

/**
 * The DateConverterModule converts a date string from one format into another format. The
 * conversion is defined by the nested elements &lt;src-pattern/&gt; and &lt;pattern/&gt; of the
 * module declaration.
 * 
 */
public class DateConverterModule extends AbstractInputModule implements ThreadSafe {

    public Object getAttribute(String name, Configuration modeConf,
            @SuppressWarnings("rawtypes") Map objectModel) throws ConfigurationException {

        String srcPattern = (String) this.settings.get("src-pattern");
        String pattern = (String) this.settings.get("pattern");
        String locale = (String) this.settings.get("locale");

        if (modeConf != null) {
            srcPattern = modeConf.getChild("src-pattern").getValue(srcPattern);
            pattern = modeConf.getChild("pattern").getValue(pattern);
            locale = modeConf.getChild("locale").getValue(locale);
        }

        if (srcPattern == null) {
            throw new ConfigurationException("Source date pattern not specified.");
        }
        if (pattern == null) {
            throw new ConfigurationException("Date pattern not specified.");
        }

        try {
            final SimpleDateFormat srcFormat = new SimpleDateFormat(srcPattern);
            final Locale loc = (locale == null) ? Locale.getDefault() : LocaleUtils
                    .toLocale(locale);
            final SimpleDateFormat format = new SimpleDateFormat(pattern, loc);
            Date date = srcFormat.parse(name);
            return format.format(date);
        } catch (Exception e) {
            throw new ConfigurationException("Could not convert date: " + name, e);
        }
    }
}
