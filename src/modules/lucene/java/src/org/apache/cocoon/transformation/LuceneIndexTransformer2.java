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
package org.apache.cocoon.transformation;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.components.search.Index;
import org.apache.cocoon.components.search.IndexException;
import org.apache.cocoon.components.search.components.AnalyzerManager;
import org.apache.cocoon.components.search.components.IndexManager;
import org.apache.cocoon.components.search.components.Indexer;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.ac.Identifiable;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.UserManager;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.modules.lucene.MetaDataFieldRegistry;
import org.apache.lenya.notification.Message;
import org.apache.lenya.notification.NotificationUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Another lucene index transformer.</br> allow
 * <ul>
 * <li>index function (update indexing or add indexing if clear attribute is
 * true)</li>
 * <li>lucene field boosting</li>
 * <li>delete function</li>
 * </ul>
 * 
 * <p>
 * This tranformer used several avalon components, but you can use them
 * separatly :
 * <ul>
 * <li>AnalyzerManager: you can setup a analyzer (configurable) in the
 * analyzer_manager tag in cocoon.xconf file</li>
 * <li>IndexManager: you can setup a index in a the /WEB-INF/index.xml (default
 * location , but you can specify the location in the IndexManager component
 * configuration in cocoon.xconf file)</li>
 * <li>Indexer (2 implementations: default (with update optimization) and
 * parallel implementation for multiple cpu)</li>
 * </p>
 * <p>
 * <strong>Example of input source: </strong>
 * </p>
 * <p>
 * <ul>
 * <li>to Index <br>
 * &lt;lucene:index xmlns:lucene=&quot;http://apache.org/cocoon/lucene/1.0&quot
 * <br/>indexid=&quot;myindex&quot; <br>
 * clear="true" (optinal attribute: clear index) <br/>merge-factor="100"&gt;
 * (optinal attribute: see lucene doc) <br>
 * <br/>&lt;lucene:document uid="http://myhost/myfile1.data"&gt; <br/>
 * &lt;lucene:field name="tile" &gt; sqdqsdq &lt;/lucene:field&gt; <br>
 * &lt;lucene:field name="description" &gt; a text bla bal blalael
 * balbal&lt;/lucene:field&gt; <br>
 * &lt;lucene:field name="date" &gt;10/12/2002&lt;/lucene:field&gt; <br/>
 * &lt;/lucene:document&gt; <br>
 * 
 * <p>
 * &lt;lucene:document uid="http://myhost/myfile2.data" &gt; <br>
 * &lt;lucene:field name="author" boost="2" &gt;Mr Author &lt;/lucene:field&gt;
 * <em>(boost the field for the search (see Lucene documentation))</em> <br/>
 * &lt;lucene:field name="langage" &gt;french&lt;/lucene:field&gt; <br>
 * &lt;/lucene:document&gt; <br>
 * &lt; /lucene:index&gt;
 * </p>
 * </li>
 * 
 * <li>To delete <br/>
 * <p>
 * &lt;lucene:delete indexid="myindex" &gt; <br>
 * &lt;lucene:document uid="http://myhost/myfile.data&quot; &gt; <br>
 * &lt;lucene:document uid="EODOED-EFE" <br>
 * &lt;/lucene:delete&gt;
 * </p>
 * 
 * <p>
 * <strong>Example of Output Source </strong>
 * </p>
 * <p>
 * &lt;page xmlns:lucene=&quot;http://apache.org/cocoon/lucene/1.0&quot;&gt;
 * <br>
 * &lt; lucene:index &gt; <br>
 * &lt;lucene:document uid="http://myhost/myfile1.data"/&gt; <br/>
 * &lt;lucene:document uid="http://myhost/myfile2.data"/&gt; <br/>
 * &lt;/lucene:index&gt;
 * </p>
 * <p>
 * &lt;lucene:delete &gt; &lt;lucene:document
 * uid="http://myhost/myfile1.data"/&gt; <br/>&lt;lucene:document
 * uid="EODOED-EFE"/&gt; <br/>&lt;/lucene:delete &gt;</br></li>
 * </ul>
 * 
 * @author Nicolas Maisonneuve
 */

public class LuceneIndexTransformer2 extends AbstractTransformer implements Recyclable,
        Serviceable, Configurable {

    public static final String DIRECTORY_DEFAULT = "index";

    public static final String LUCENE_URI = "http://apache.org/cocoon/lucene/1.0";

    public static final String LUCENE_PREXIF = "lucene";

    /**
     * action element : index doc
     */
    public static final String LUCENE_INDEXING_ELEMENT = "index";

    /**
     * action element: delete doc
     */
    public static final String LUCENE_DELETING_ELEMENT = "delete";

    /**
     * index identity (see index definition file)
     */
    public static final String LUCENE_INDEXING_INDEXID_ATTRIBUTE = "indexid";

    /**
     * Optional attribute: Clear index: true/false (default: false)
     */
    public static final String LUCENE_INDEXING_CREATE_ATTRIBUTE = "clear";

    /**
     * Optional attribute: Analyzer identity: see analyzerManager Component
     * (default: the analyer of the index declared in the index definition)
     */
    public static final String LUCENE_INDEXING_ANALYZER_ATTRIBUTE = "analyzer";

    /**
     * Optional attribute: MergeFactor number (default 10): improve the indexing
     * speed for large indexing (see Lucene docs)
     */
    public static final String LUCENE_INDEXING_MERGE_FACTOR_ATTRIBUTE = "mergefactor";

    /**
     * Lucene document element
     */
    public static final String LUCENE_DOCUMENT_ELEMENT = "document";

    /**
     * Lucene document uid field
     */
    public static final String LUCENE_DOCUMENT_UID_ATTRIBUTE = "uid";

    /**
     * lucene field element
     */
    public static final String LUCENE_FIELD_ELEMENT = "field";

    /**
     * lucene field name
     */
    public static final String LUCENE_FIELD_NAME_ATTRIBUTE = "name";

    /**
     * Optional attribute: lucene field boost (see lucene docs)
     */
    public static final String LUCENE_FIELD_BOOST_ATTRIBUTE = "boost";

    // The 6 states of the state machine
    private int processing;

    public static final int NO_PROCESSING = 0;

    public static final int INDEX_PROCESS = 1;

    public static final int IN_DOCUMENT_PROCESS = 2;

    public static final int IN_FIELD_PROCESS = 4;

    public static final int DELETE_PROCESS = 5;

    public static final int DELETING_PROCESS = 6;

    protected static final String NAMESPACE_ATTRIBUTE = "namespace";

    // Runtime variables
    private int mergeFactor;

    private AttributesImpl attrs = new AttributesImpl();

    private Index index;

    private Indexer indexer;

    private ServiceManager manager;

    private Document bodyDocument;

    private String uid;

    private String fieldname;

    private float fieldboost;

    private StringBuffer fieldvalue;

    private Request request;

    private String pubId;
    private String area;
    private String uuid;
    private String language;

    private MetaDataFieldRegistry registry;

    /**
     * Setup the transformer.
     */
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters parameters)
            throws ProcessingException, SAXException, IOException {
        this.request = ObjectModelHelper.getRequest(objectModel);
        try {
            this.pubId = parameters.getParameter("publicationId");
            this.area = parameters.getParameter("area");
            this.uuid = parameters.getParameter("uuid");
            this.language = parameters.getParameter("language");
        } catch (ParameterException e) {
            throw new ProcessingException(e);
        }
    }

    public void recycle() {
        super.recycle();
        this.index = null;
        this.indexer = null;
        this.processing = NO_PROCESSING;
        this.request = null;
        this.area = null;
        this.uuid = null;
        this.language = null;
        this.attrs = null;
        this.bodyDocument = null;
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    public void startDocument() throws SAXException {
        super.startDocument();
    }

    public void endDocument() throws SAXException {
        super.endDocument();
    }

    /**
     * Begin the scope of a prefix-URI Namespace mapping.
     * 
     * @param prefix The Namespace prefix being declared.
     * @param uri The Namespace URI the prefix is mapped to.
     */
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (processing == NO_PROCESSING) {
            super.startPrefixMapping(prefix, uri);
        }
    }

    /**
     * End the scope of a prefix-URI mapping.
     * 
     * @param prefix The prefix that was being mapping.
     */
    public void endPrefixMapping(String prefix) throws SAXException {
        if (processing == NO_PROCESSING) {
            super.endPrefixMapping(prefix);
        }
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {

        // getLogger().debug("START processing: "+processing+" "+localName);

        if (LUCENE_URI.equals(namespaceURI)) {
            switch (processing) {

            case NO_PROCESSING:

                // index action
                if (LUCENE_INDEXING_ELEMENT.equals(localName)) {
                    this.initIndexer(atts);
                    processing = INDEX_PROCESS;

                    super.startElement(namespaceURI, localName, qName, attrs);
                }
                // delete action
                else if (LUCENE_DELETING_ELEMENT.equals(localName)) {
                    this.initIndexer(atts);
                    processing = DELETE_PROCESS;
                    super.startElement(namespaceURI, localName, qName, attrs);
                } else {
                    handleError("element " + localName + " unknown");
                }
                break;

            case INDEX_PROCESS:

                // new document to index
                if (LUCENE_DOCUMENT_ELEMENT.equals(localName)) {

                    uid = atts.getValue(LUCENE_DOCUMENT_UID_ATTRIBUTE);
                    if (uid == null) {
                        handleError("<" + LUCENE_PREXIF + ":" + LUCENE_DOCUMENT_ELEMENT
                                + "> element must contain " + LUCENE_DOCUMENT_UID_ATTRIBUTE
                                + " attribute");
                    }
                    bodyDocument = index.createDocument(uid);
                    processing = IN_DOCUMENT_PROCESS;
                } else {
                    handleError("element " + localName + " is not allowed in  <" + LUCENE_PREXIF
                            + ":" + LUCENE_DOCUMENT_ELEMENT + "> element");
                }
                break;

            case DELETE_PROCESS:

                if (LUCENE_DOCUMENT_ELEMENT.equals(localName)) {
                    uid = atts.getValue(LUCENE_DOCUMENT_UID_ATTRIBUTE);
                    if (uid == null) {
                        handleError("<" + LUCENE_PREXIF + ":" + LUCENE_DOCUMENT_ELEMENT
                                + "> element must contain " + LUCENE_DOCUMENT_UID_ATTRIBUTE
                                + " attribute");
                    }
                    processing = DELETING_PROCESS;
                } else {
                    handleError("element " + localName + " is not a <lucene:document> element");
                }
                break;

            case IN_DOCUMENT_PROCESS:
                if (LUCENE_FIELD_ELEMENT.equals(localName)) {
                    final String namespace = atts.getValue(NAMESPACE_ATTRIBUTE);
                    final String name = atts.getValue(LUCENE_FIELD_NAME_ATTRIBUTE);
                    
                    // set the field name
                    this.fieldname = namespace == null ? name : getMetaDataFieldName(namespace, name);
                    if (this.fieldname == null || this.fieldname.equals("")) {
                        handleError("<lucene:field> element must contain name attribute");
                    }

                    // clear the text buffer
                    this.fieldvalue = new StringBuffer();

                    // set boost value
                    String fieldboostS = atts.getValue(LUCENE_FIELD_BOOST_ATTRIBUTE);
                    if (fieldboostS == null) {
                        fieldboost = 1.0f;
                    } else {
                        fieldboost = Float.parseFloat(fieldboostS);
                    }
                    processing = IN_FIELD_PROCESS;
                } else {
                    handleError("<" + LUCENE_PREXIF + ":" + LUCENE_FIELD_ELEMENT + " was expected!");
                }
                break;
            }
        } else {
            // bypass
            super.startElement(namespaceURI, localName, qName, atts);
        }
    }

    protected String getMetaDataFieldName(String namespace, String elementName) {
        if (this.registry == null) {
            try {
                this.registry = (MetaDataFieldRegistry) this.manager.lookup(MetaDataFieldRegistry.ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
        return this.registry.getFieldName(namespace, elementName);
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {

        // getLogger().debug("END processing: "+processing+" "+localName);

        if (LUCENE_URI.equals(namespaceURI)) {
            switch (processing) {

            case INDEX_PROCESS:
                if (LUCENE_INDEXING_ELEMENT.equals(localName)) {
                    // end of the indexing -> close the indexer
                    this.closeIndexer();
                    this.processing = NO_PROCESSING;
                    super.endElement(namespaceURI, localName, qName);
                } else {
                    handleUnexpectedClosingElement(localName, LUCENE_DELETING_ELEMENT);
                }
                break;

            case DELETE_PROCESS:
                if (LUCENE_DELETING_ELEMENT.equals(localName)) {
                    // end of the deleting -> close the indexer
                    this.closeIndexer();
                    this.processing = NO_PROCESSING;
                    super.endElement(namespaceURI, localName, qName);
                } else {
                    handleUnexpectedClosingElement(localName, LUCENE_DELETING_ELEMENT);
                }
                break;

            case IN_DOCUMENT_PROCESS:
                if (LUCENE_DOCUMENT_ELEMENT.equals(localName)) {
                    if (canIndex()) {
                        // index the document
                        try {
                            this.indexer.index(bodyDocument);
                        } catch (IndexException ex1) {
                            handleError(ex1);
                        }
                    }
                    if (this.getLogger().isDebugEnabled()) {
                        this.getLogger().debug(" lucene document: " + this.bodyDocument);
                    }
                    bodyDocument = null;
                    attrs.clear();
                    attrs.addAttribute(namespaceURI, "uid", "uid", "CDATA", uid);
                    super.startElement(namespaceURI, localName, qName, attrs);
                    super.endElement(namespaceURI, localName, qName);
                    this.processing = INDEX_PROCESS;
                } else {
                    handleUnexpectedClosingElement(localName, LUCENE_DOCUMENT_ELEMENT);
                }
                break;

            case DELETING_PROCESS:
                if (LUCENE_DOCUMENT_ELEMENT.equals(localName)) {
                    // delete a document
                    if (canIndex()) {
                        try {
                            indexer.del(uid);
                        } catch (IndexException ex2) {
                            handleError(ex2);
                        }
                    }
                    attrs.clear();
                    attrs.addAttribute(namespaceURI, "uid", "uid", "CDATA", uid);
                    super.startElement(namespaceURI, localName, qName, attrs);
                    super.endElement(namespaceURI, localName, qName);
                    this.processing = DELETE_PROCESS;
                } else {
                    handleUnexpectedClosingElement(localName, LUCENE_DOCUMENT_ELEMENT);
                }
                break;

            case IN_FIELD_PROCESS:
                if (LUCENE_FIELD_ELEMENT.equals(localName)) {

                    // create lucene field
                    try {
                        Field f = index.createField(fieldname, fieldvalue.toString());
                        f.setBoost(fieldboost);
                        
                        // add field to the lucene document
                        bodyDocument.add(f);
                        processing = IN_DOCUMENT_PROCESS;
                    } catch (IndexException ex) {
                        handleError(ex);
                    }
                } else {
                    handleUnexpectedClosingElement(localName, LUCENE_FIELD_ELEMENT);
                }
                break;

            default:
                handleError("Inappropriate element '" + localName + "' in state '" + processing  + "'!");
            }
        } else {
            super.endElement(namespaceURI, localName, qName);
        }
    }

    protected void handleUnexpectedClosingElement(String localName, String expectedLocalName)
            throws SAXException {
        handleError("</lucene:" + expectedLocalName + "> was expected instead of </lucene:" + localName + ">!");
    }

    protected boolean canIndex() {
        return this.indexer != null;
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (processing == IN_FIELD_PROCESS) {
            this.fieldvalue.append(ch, start, length);
        } else {
            super.characters(ch, start, length);
        }

    }

    /**
     * Configure the Indexer
     * 
     * @param id the indexid
     * @param analyzerid
     * @param mergeF
     * @param clear
     * @throws SAXException
     */
    private void initIndexer(Attributes atts) throws SAXException {

        final String indexId = atts.getValue(LUCENE_INDEXING_INDEXID_ATTRIBUTE);
        final String mergeF = atts.getValue(LUCENE_URI, LUCENE_INDEXING_MERGE_FACTOR_ATTRIBUTE);
        final String clear = atts.getValue(LUCENE_URI, LUCENE_INDEXING_CREATE_ATTRIBUTE);
        attrs = new AttributesImpl(atts);

        setIndexer(indexId);

        if (canIndex()) {
            
            String analyzerId = atts.getValue(LUCENE_URI, LUCENE_INDEXING_ANALYZER_ATTRIBUTE);
            setAnalyzer(analyzerId);
            
            // set clear mode
            boolean new_index = (clear != null && clear.toLowerCase().equals("true")) ? true
                    : false;
            if (new_index) {
                try {
                    indexer.clearIndex();
                } catch (IndexException ex3) {
                    handleError("error clearing index", ex3);
                }
            }

            // set the mergeFactor
            if (mergeF != null) {
                mergeFactor = Integer.parseInt(mergeF);
                indexer.setMergeFactor(mergeFactor);
            }

            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug(
                        "index " + indexId + " clear: " + new_index + " analyzerid: " + analyzerId
                                + "mergefactor: " + mergeF);
            }
        }
    }

    protected void setIndexer(String indexId) throws SAXException {
        IndexManager indexManager = null;
        try {
            indexManager = (IndexManager) manager.lookup(IndexManager.ROLE);
            index = indexManager.getIndex(indexId);
            if (index == null) {
                handleError("index [" + indexId + "] no found in the index definition");
            }
            else {
                indexer = index.getIndexer();
                if (indexer == null) {
                    handleError("Index [" + indexId + "] did return a null indexer.");
                }
            }
        } catch (ServiceException ex1) {
            handleError(ex1);
        } catch (IndexException ex3) {
            handleError("get Indexer error for index [" + indexId + "]", ex3);
        } finally {
            manager.release(indexManager);
        }
    }

    /**
     * Set a custum analyzer (default: the analyzer of the index).
     * @param analyzerId The analyzer ID (may be null)
     * @throws SAXException if an error occurs.
     */
    protected void setAnalyzer(String analyzerId) throws SAXException {
        if (analyzerId == null) {
            analyzerId = index.getDefaultAnalyzerID();
        }
        if (analyzerId != null) {
            Analyzer analyzer = null;
            AnalyzerManager analyzerManager = null;
            try {
                analyzerManager = (AnalyzerManager) manager.lookup(AnalyzerManager.ROLE);
                analyzer = analyzerManager.getAnalyzer(analyzerId);
                indexer.setAnalyzer(analyzer);
            } catch (ServiceException ex1) {
                handleError(ex1);
            } catch (ConfigurationException ex2) {
                handleError("error setting analyzer for index [" + this.index.getID() + "]", ex2);
            }
            finally {
                manager.release(analyzerManager);
            }
        }
    }
    
    void handleError(String message, Exception ex) throws SAXException {
        handleError(message + ": " + getExceptionMessage(ex));
    }

    void handleError(Exception ex) throws SAXException {
        handleError(getExceptionMessage(ex));
    }

    protected String getExceptionMessage(Exception ex) throws SAXException {
        String exMsg = ex.getMessage();
        String msg = exMsg == null ? "" : " (" + exMsg + ")";
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ex.printStackTrace(printWriter);
        return ex.getClass().getName() + msg + ", Stack trace: " + stringWriter.toString();
    }

    /**
     * Handle Exception or Error
     * 
     * @param msg
     * @param ex
     * @throws SAXException
     */
    void handleError(String msg) throws SAXException {
        closeIndexer();

        try {
            Session session = RepositoryUtil.getSession(this.manager, this.request);
            User sender = session.getIdentity().getUser();
            UserManager userManager = (UserManager) sender.getItemManager();
            User recipient = userManager.getUser(this.notificationRecipient);
            Identifiable[] recipients = { recipient };

            String subject = "indexing-failed-subject";
            String[] subjectParams = new String[0];
            String body = "indexing-failed-body";
            String[] bodyParams = { this.pubId, this.area, this.uuid, this.language, msg };

            Message message = new Message(subject, subjectParams, body, bodyParams, sender,
                    recipients);
            NotificationUtil.notify(this.manager, message);

            getLogger().error(
                    "Could not index document [" + this.pubId + ":" + this.area + ":" + this.uuid
                            + ":" + this.language + "], sent message to user ["
                            + this.notificationRecipient + "].");

        } catch (Exception e) {
            throw new SAXException(e);
        }

        /*
         * if (ex == null) { // this.getLogger().error(msg); throw new
         * SAXException(msg); } else { // this.getLogger().error(msg, ex); throw
         * new SAXException(msg, ex); }
         */
    }

    /**
     * Close the indexer
     * 
     * @throws SAXException
     */
    void closeIndexer() throws SAXException {
        if (index != null) {
            index.releaseIndexer(indexer);
        }
    }

    private String notificationRecipient = null;

    public void configure(Configuration config) throws ConfigurationException {
        this.notificationRecipient = config.getChild("notify").getAttribute("user");
    }

}
