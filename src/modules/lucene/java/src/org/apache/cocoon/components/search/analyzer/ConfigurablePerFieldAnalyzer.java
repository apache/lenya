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
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;

/**
 * Configurable PerFieldAnalyzerWrapper. Allow one analyzer per field for
 * indexing a document (useful for multilanguage document)
 * 
 * (@link org.apache.lucene.analysis.PerFieldAnalyzerWrapper class)
 * 
 * 
 * A config file for this analyzer is:
 * 
 * <!-- if a lucene document containing a field not present in the "field" tags,
 * the defaultAnalyzer would be used --> <config defaultAnalyzer="analyzerEN">
 * <fields><!-- if a lucene document contains the field "summury" , the
 * analyzer "analyzerEN" would be used --> <field name="summury"
 * analyzer="analyzerEN"/> <field name="desc_fr" analyzer="analyzerFR"/> <field
 * name="desc_en" analyzer="analyzerEN"/> <field name="desc_de"
 * analyzer="analyzerDE"/> </fields> </config>
 * 
 * @author Nicolas Maisonneuve
 */
public class ConfigurablePerFieldAnalyzer extends ConfigurableAnalyzer {

    public static final String CONFIG_DEFAULTANALYZER_ATTRIBUTE = "defaultAnalyzer";

    public static final String FIELDS_ELEMENT = "fields";

    public static final String FIELD_ELEMENT = "field";

    public static final String FIELD_NAME_ATTRIBUTE = "name";

    public static final String FIELD_ANALYZERID_ATTRIBUTE = "analyzer";

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.analyzer.ConfigurableAnalyzer#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration)
            throws ConfigurationException {

        String analyzerid = configuration
                .getAttribute(CONFIG_DEFAULTANALYZER_ATTRIBUTE);

        Analyzer analyzer = analyzerM.getAnalyzer(analyzerid);
        if (analyzer == null) {
            throw new ConfigurationException("analyzer " + analyzerid
                    + " doesn't exist");
        }

        PerFieldAnalyzerWrapper tmpanalyzer = new PerFieldAnalyzerWrapper(
                analyzer);
        Configuration[] conffield = configuration.getChild(FIELDS_ELEMENT)
                .getChildren(FIELD_ELEMENT);

        for (int i = 0; i < conffield.length; i++) {

            String fieldname = conffield[i].getAttribute(FIELD_NAME_ATTRIBUTE);
            analyzerid = conffield[i].getAttribute(FIELD_ANALYZERID_ATTRIBUTE);

            if (fieldname == null || fieldname.equals("")) {
                throw new ConfigurationException("element " + FIELD_ELEMENT
                        + " must have the " + FIELD_NAME_ATTRIBUTE
                        + " attribute");
            }
            if (analyzerid == null || analyzerid.equals("")) {
                throw new ConfigurationException("element " + FIELD_ELEMENT
                        + " must have the " + FIELD_ANALYZERID_ATTRIBUTE
                        + " attribute");
            }

            analyzer = analyzerM.getAnalyzer(analyzerid);

            if (analyzer == null) {
                throw new ConfigurationException("analyzer " + analyzerid
                        + " doesn't exist");
            }
            tmpanalyzer.addAnalyzer(fieldname, analyzer);
        }
        this.analyzer = tmpanalyzer;
    }

}
