package org.lenya.lucene.index;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Lucene" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Lucene", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.document.Document;
//import org.apache.lucene.demo.html.HTMLParser;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.lenya.lucene.HTMLDocument;
import org.lenya.lucene.IndexEnvironment;
import org.lenya.lucene.index.ConfigurableDocumentCreator;
import org.lenya.xml.DocumentHelper;
import org.xml.sax.SAXException;

public class ConfigurableIndexer
    extends AbstractIndexer {
    
    public DocumentCreator createDocumentCreator(Configuration configuration)
            throws Exception {
                
        String configurationFileName = configuration.getChild("configuration").getAttribute("src");
        File configurationFile = new File(configurationFileName);
        String stylesheet = getStylesheet(configurationFile);
        return new ConfigurableDocumentCreator(stylesheet);
    }
    
    public static final String CONFIGURATION_CREATOR_STYLESHEET = "org/lenya/lucene/index/configuration2xslt.xsl";
    
    /**
     * Converts the configuration file to an XSLT stylesheet and returns a reader that reads this stylesheet.
     */
    protected static String getStylesheet(File configurationFile)
            throws Exception {
                
        URL configurationCreatorURL
        = ConfigurableIndexer.class.getClassLoader().getResource(CONFIGURATION_CREATOR_STYLESHEET);
        File configurationStylesheetFile = new File(new URI(configurationCreatorURL.toString()));
        org.w3c.dom.Document configurationDocument = DocumentHelper.readDocument(configurationFile);
        
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer configurationTransformer
        = tFactory.newTransformer(new StreamSource(configurationStylesheetFile));
        
        DOMSource source = new DOMSource(configurationDocument);
        Writer stylesheetWriter = new StringWriter();
        configurationTransformer.transform(source, new StreamResult(stylesheetWriter));
        return stylesheetWriter.toString();
    }
    
}
