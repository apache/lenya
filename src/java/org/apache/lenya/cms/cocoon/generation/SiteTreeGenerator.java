/*
 * Copyright 1999-2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *  
 */

/* $Id:$ */

package org.apache.lenya.cms.cocoon.generation;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.TimeStampValidity;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.LastModified;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SiteTreeGenerator extends ServiceableGenerator
    implements Parameterizable, CacheableProcessingComponent
{
    private static Logger log = Logger.getLogger(SiteTreeGenerator.class);
    
    protected final static String I18N_PX = "i18n";
    protected final static String I18N_NS = "http://apache.org/cocoon/i18n/2.1";
    
    protected final static String CDATA = "CDATA";
    
    protected final static String SITE_ELEMENT = "site";
    protected final static String NODE_ELEMENT = "node";
    protected final static String LABEL_ELEMENT = "label";
    
    protected final static String LABEL_ATTRIBUTE = "label";
    protected final static String ATTR_ATTRIBUTE = "attr";
    protected final static String Q_ATTR_ATTRIBUTE = I18N_PX + ":" + ATTR_ATTRIBUTE;
    protected final static String ID_ATTRIBUTE = "id";
    protected final static String LANG_ATTRIBUTE = "lang";
    protected final static String HREF_ATTRIBUTE = "href";
    protected final static String LINK_ATTRIBUTE = "link";
    protected final static String VISIBLEINNAV_ATTRIBUTE = "visibleinnav";
    protected final static String SUFFIX_ATTRIBUTE = "suffix";

    // TODO: is this correct re xml namespace?
    protected final static String Q_LANG_ATTRIBUTE = "xml:lang";
    
    public final static String AREA_PARAMETER = "area";

    private final AttributesImpl atts = new AttributesImpl();
    
    SiteTree sitetree = null;
    String area = null;
    
    /**
     * No parameters implemented.
     * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
     */
    public void parameterize(Parameters parameters) throws ParameterException {
    }
    
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par)
        throws ProcessingException, SAXException, IOException
    {
        log.debug("setup");
        try {
            Publication publication = PublicationFactory.getPublication(objectModel);
            area = par.getParameter(AREA_PARAMETER);
            sitetree = publication.getTree(area);
        } catch (PublicationException e) {
            throw new ProcessingException("Unable to get sitetree: publication exception.", e);
        } catch (ParameterException e) {
            throw new ProcessingException("Unable to get sitetree: parameter 'area' not found.", e);
        } catch (SiteTreeException e) {
            throw new ProcessingException("Unable to get sitetree.", e);
        }
    }
    
    /**
     * @see org.apache.cocoon.generation.Generator#generate()
     */
    public void generate() throws SAXException {
        log.debug("generate");
        // Start the document and set the namespace.
        this.contentHandler.startDocument();
        // Default namespace.
        this.contentHandler.startPrefixMapping("", SiteTree.NAMESPACE_URI);
        this.contentHandler.startPrefixMapping(I18N_PX, I18N_NS);

        generateSiteTree(sitetree);

        // End the document.
        this.contentHandler.endPrefixMapping("");
        this.contentHandler.endDocument();
    }

    private void generateSiteTree(SiteTree tree) throws SAXException {
        atts.clear();
        // TODO: Do not hardcode "Authoring" label!!!
        atts.addAttribute("", LABEL_ATTRIBUTE, LABEL_ATTRIBUTE, CDATA, "Authoring");
        atts.addAttribute(I18N_NS, ATTR_ATTRIBUTE, Q_ATTR_ATTRIBUTE, CDATA, "label");

        this.contentHandler.startElement(SiteTree.NAMESPACE_URI, SITE_ELEMENT, SITE_ELEMENT, atts);

        SiteTreeNode[] topNodes = tree.getTopNodes();
        for (int i=0; i<topNodes.length; i++) {
            generateNodes(topNodes[i]);
        }

        this.contentHandler.endElement(SiteTree.NAMESPACE_URI, SITE_ELEMENT, SITE_ELEMENT);
    }
    
    private void generateNodes(SiteTreeNode node) throws SAXException {
        atts.clear();
        atts.addAttribute("", ID_ATTRIBUTE, ID_ATTRIBUTE, CDATA, node.getId());
        if (node.getHref() != null)
            atts.addAttribute("", HREF_ATTRIBUTE, HREF_ATTRIBUTE, CDATA, node.getHref());
        if (node.getSuffix() != null)
            atts.addAttribute("", SUFFIX_ATTRIBUTE, SUFFIX_ATTRIBUTE, CDATA, node.getSuffix());
        atts.addAttribute("", LINK_ATTRIBUTE, LINK_ATTRIBUTE, CDATA, Boolean.toString(node.hasLink()));
        atts.addAttribute("", VISIBLEINNAV_ATTRIBUTE, VISIBLEINNAV_ATTRIBUTE, CDATA, Boolean.toString(node.visibleInNav()));

        this.contentHandler.startElement(SiteTree.NAMESPACE_URI, NODE_ELEMENT, NODE_ELEMENT, atts);
        
        Label[] labels = node.getLabels();
        for (int i=0; i<labels.length; i++) 
            generateLabels(labels[i]);
        SiteTreeNode[] children = node.getChildren();
        for (int i=0; i<children.length; i++)
            generateNodes(children[i]);
        
        this.contentHandler.endElement(SiteTree.NAMESPACE_URI, NODE_ELEMENT, NODE_ELEMENT);
    }
    
    private void generateLabels(Label label) throws SAXException {
        atts.clear();
        atts.addAttribute("", LANG_ATTRIBUTE, Q_LANG_ATTRIBUTE, CDATA, label.getLanguage());

        this.contentHandler.startElement(SiteTree.NAMESPACE_URI, LABEL_ELEMENT, LABEL_ELEMENT, atts);
        char[] labelA = label.getLabel().toCharArray();
        this.contentHandler.characters(labelA, 0, labelA.length);
        this.contentHandler.endElement(SiteTree.NAMESPACE_URI, LABEL_ELEMENT, LABEL_ELEMENT);
    }
    
    /**
     * Recycle the generator
     */
    public void recycle() {
        log.debug("recycle");
        super.recycle();
        sitetree = null;
        area = null;
    }

    /**
     * @see org.apache.cocoon.caching.CacheableProcessingComponent#getKey()
     */
    public Serializable getKey() {
        return area;
    }

    /**
     * @see org.apache.cocoon.caching.CacheableProcessingComponent#getValidity()
     */
    public SourceValidity getValidity() {
        // Check if sitetree implementation supports last modified
        if (!(sitetree instanceof LastModified)) {
            return null;
        } else {
            return new TimeStampValidity(((LastModified)sitetree).getLastModified());
        }
    }
}
