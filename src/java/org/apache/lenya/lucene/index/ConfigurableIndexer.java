/*
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.lucene.index;

import org.apache.lenya.xml.DocumentHelper;

import org.apache.log4j.Category;

import org.w3c.dom.Element;

import java.io.File;
import java.io.FileFilter;
import java.io.StringWriter;
import java.io.Writer;

import java.net.URI;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/**
 * @author Andreas Hartmann
 * @author Michael Wechner
 * @version $Id: ConfigurableIndexer.java,v 1.10 2003/11/14 00:01:22 michi Exp $
 */
public class ConfigurableIndexer extends AbstractIndexer {
    Category log = Category.getInstance(ConfigurableIndexer.class);

    /**
     * Instantiate a Document Creator for creating Lucene Documents
     *
     * @param element <code>indexer</code> node
     *
     * @return DocumentCreator
     *
     * @throws Exception DOCUMENT ME!
     */
    public DocumentCreator createDocumentCreator(Element indexer, String configFileName) throws Exception {
        log.debug(".createDocumentCreatort(): Element name: " + indexer.getNodeName());

        // FIXME: concat these files ...
        String configurationFileName = new File(configFileName).getParent() + File.separator + getLuceneDocConfigFileName(indexer);
        File configurationFile = new File(configurationFileName);
        String stylesheet = getStylesheet(configurationFile);
        return new ConfigurableDocumentCreator(stylesheet);
    }

    public static final String CONFIGURATION_CREATOR_STYLESHEET = "org/apache/lenya/lucene/index/configuration2xslt.xsl";

    /**
     * Converts the configuration file to an XSLT stylesheet and returns a reader that reads this stylesheet.
     */
    protected String getStylesheet(File configurationFile) throws Exception {
        log.debug(".getStylesheet(): Configuration file: " + configurationFile.getAbsolutePath());

        URL configurationCreatorURL = ConfigurableIndexer.class.getClassLoader().getResource(CONFIGURATION_CREATOR_STYLESHEET);
        File configurationStylesheetFile = new File(new URI(configurationCreatorURL.toString()));
        org.w3c.dom.Document configurationDocument = DocumentHelper.readDocument(configurationFile);

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer configurationTransformer = tFactory.newTransformer(new StreamSource(configurationStylesheetFile));

        DOMSource source = new DOMSource(configurationDocument);
        Writer stylesheetWriter = new StringWriter();
        configurationTransformer.transform(source, new StreamResult(stylesheetWriter));

        log.debug(".getStylesheet(): Meta Stylesheet: " + stylesheetWriter.toString());

        return stylesheetWriter.toString();
    }

    /**
     * Returns the filter used to receive the indexable files.
     */
    public FileFilter getFilter() {
        String[] indexableExtensions = { "xml" };
        return new AbstractIndexer.DefaultIndexFilter(indexableExtensions);
    }

    /**
     *
     */
   private String getLuceneDocConfigFileName(Element indexer) {
       String luceneDocConfigFileName = null;

       org.w3c.dom.NodeList nl = indexer.getChildNodes();
       for (int i = 0; i < nl.getLength(); i++) {
           org.w3c.dom.Node node = nl.item(i);
           if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE && node.getNodeName().equals("configuration")) {
               log.debug(".getLuceneDocConfigFileName(): Node configuration exists!");
               luceneDocConfigFileName = ((Element)node).getAttribute("src");
           }
       }
       if (luceneDocConfigFileName == null) {
           log.error(".getLuceneDocConfigFileName(): ERROR: Lucene Document Configuration is not specified (indexer/configuration/@src)");
       }
       log.debug(".getLuceneDocConfigFileName(): Lucene Document Configuration: " + luceneDocConfigFileName);
       return luceneDocConfigFileName;
   }
}
