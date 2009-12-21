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
package org.apache.cocoon.components.search.analyzer;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * Configurable Stopword Analyzer
 * 
 * Config file:
 * 
 * <stopWords><stopWord>a </stopWord> <stopWord>the </stopWord> <stopWord>but
 * </stopWord> </stopWords>
 * 
 * @author Nicolas Maisonneuve
 */
public class ConfigurableStopwordAnalyzer extends ConfigurableAnalyzer {

    /** The element containing a stop word. */
    private static final String STOP_WORD_ELEMENT = "stopword";

    /**
     * Configures the analyzer.(stop words)
     */
    public void configure(Configuration configuration)
            throws ConfigurationException {
        String[] words = stopTableBuilder(configuration);
        logger.info("stop words number: " + words.length);
        analyzer = new StandardAnalyzer(words);
    }

    /**
     * Build Stop Table
     * 
     * @param conf
     *            Configuration file (above the STOP_WORDS ELEMENT)
     * @throws ConfigurationException
     * @return String[] array with all excluded words
     */
    static public String[] stopTableBuilder(Configuration conf)
            throws ConfigurationException {

        Configuration[] cStops = conf.getChildren(STOP_WORD_ELEMENT);
        if (cStops != null) {
            final String[] words = new String[cStops.length];
            for (int i = 0; i < cStops.length; i++) {
                words[i] = cStops[i].getValue();
            }
            return words;
        }

        final String[] words = new String[0];
        return words;
    }

}
