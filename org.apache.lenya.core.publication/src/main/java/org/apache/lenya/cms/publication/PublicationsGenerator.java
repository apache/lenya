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
package org.apache.lenya.cms.publication;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.Generator;
import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.cocoon.xml.XMLConsumer;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Generates a list of available pulications:
 * 
 * <pre>
 * &lt;publications xmlns=&quot;http://apache.org/lenya/publications&quot;&gt;
 *   &lt;publication id=&quot;foo&quot; src="[publication source URI]"/&gt;
 *   &lt;publication id=&quot;bar&quot; src="[publication source URI]" /&gt;
 *   ...
 * &lt;/publications&gt;
 * </pre>
 */
public class PublicationsGenerator implements Generator {

    protected static final String ELEM_PUBLICATIONS = "publications";
    protected static final String ELEM_PUBLICATION = "publication";
    protected static final String ATTR_ID = "id";
    protected static final String ATTR_SRC = "src";

    public static final String NAMESPACE = "http://apache.org/lenya/publications";

    private Repository repository;
    private XMLConsumer consumer;

    public void generate() throws IOException, SAXException, ProcessingException {
        this.consumer.startDocument();
        this.consumer.startPrefixMapping("", NAMESPACE);
        this.consumer.startElement(NAMESPACE, ELEM_PUBLICATIONS, ELEM_PUBLICATIONS,
                new AttributesImpl());

        ProcessInfoProvider process = (ProcessInfoProvider) WebAppContextUtils
                .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
        HttpServletRequest request = process.getRequest();
        Session session = this.repository.getSession(request);

        for (String pubId : session.getPublicationIds()) {
            Publication pub = session.getPublication(pubId);
            AttributesImpl attrs = new AttributesImpl();
            attrs.addAttribute("", ATTR_ID, ATTR_ID, "CDATA", pub.getId());
            attrs.addAttribute("", ATTR_SRC, ATTR_SRC, "CDATA", pub.getSourceUri());
            this.consumer.startElement(NAMESPACE, ELEM_PUBLICATION, ELEM_PUBLICATION, attrs);
        }

        this.consumer.endElement(NAMESPACE, ELEM_PUBLICATIONS, ELEM_PUBLICATIONS);
        this.consumer.endPrefixMapping("");
        this.consumer.endDocument();
    }

    public void setConsumer(XMLConsumer consumer) {
        this.consumer = consumer;
    }

    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters params)
            throws ProcessingException, SAXException, IOException {
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

}
