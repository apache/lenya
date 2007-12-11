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
package org.apache.cocoon.components.search.utils;

/**
 * Utility class
 * 
 * @author Maisonneuve Nicolas
 * 
 */
import java.io.IOException;
import java.util.HashMap;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceValidity;
import org.xml.sax.SAXException;

public class SourceHelper {

    static final private HashMap sources = new HashMap();

    static final private DefaultConfigurationBuilder confBuilder = new DefaultConfigurationBuilder();

    static final public void registerSource(Source source) {
        if (!sources.containsKey(source)) {
            SourceValidity refValidity = source.getValidity();
            sources.put(source, refValidity);
        }
    }

    /**
     * Check the validity of the source with the registered source
     * 
     * @return true if the source didn't changed
     */
    static final public boolean checkSourceValidity(Source source) {
        SourceValidity newValidity = source.getValidity();
        SourceValidity refValidity = (SourceValidity) sources.get(source);
        return checkSourceValidity(newValidity, refValidity);
    }

    /**
     * Compare two sources
     * 
     * @return true if the source didn't changed
     */
    static final public boolean checkSourceValidity(SourceValidity s1Validity,
            SourceValidity s2Validity) {

        int valid = s2Validity.isValid();
        boolean isValid;
        if (valid == 0) {
            valid = s2Validity.isValid(s1Validity);
            isValid = (valid == 1);
        } else {
            isValid = (valid == 1);
        }
        return isValid;
    }

    static final public Configuration build(Source source)
            throws ConfigurationException {
        try {
            return confBuilder.build(source.getInputStream());
        } catch (IOException ex) {
            throw new ConfigurationException("File " + source.getURI(), ex);
        } catch (SAXException ex) {
            throw new ConfigurationException(
                    "SAX Error in the configuration File", ex);
        }
    }

}
