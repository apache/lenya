importClass(Packages.java.util.ArrayList);

importClass(Packages.org.apache.cocoon.components.ContextHelper);

importClass(Packages.org.apache.lenya.cms.cocoon.flow.FlowHelper);
importClass(Packages.org.apache.lenya.cms.publication.Publication);
importClass(Packages.org.apache.lenya.cms.publication.DocumentHelper);
importClass(Packages.org.apache.lenya.cms.publication.PublicationHelper);
importClass(Packages.org.apache.lenya.cms.publication.ResourcesManager);

importClass(Packages.java.io.BufferedOutputStream);
importClass(Packages.java.io.FileOutputStream);

/**
 * @version $Id$
 */

/**
 * Collects all link/url information from a publication's siteree.
 * This information is used to generate xml for the Kupu (link) library drawer.
 */
function sitetree_link_library() {
    
    var flowHelper = new FlowHelper();
    var documentHelper = flowHelper.getDocumentHelper(cocoon);
    var pageEnvelope = flowHelper.getPageEnvelope(cocoon);
    var siteTree = pageEnvelope.getPublication().getSiteTree(pageEnvelope.getDocument().getArea());
    var allNodes = siteTree.getNode("/").preOrder();
    var resources = new ArrayList(allNodes.size()-1);
    
    for(var i=1; i<allNodes.size(); i++) {
        resources.add(i-1, {
                "url" : documentHelper.getDocumentUrl(allNodes.get(i).getAbsoluteId(), pageEnvelope.getDocument().getArea(), null),
                "label" : allNodes.get(i).getLabel(pageEnvelope.getDocument().getLanguage()).getLabel(),
                "id" : allNodes.get(i).getId(),
                "fullid" : allNodes.get(i).getAbsoluteId(),
                "language" : pageEnvelope.getDocument().getLanguage()});
    }
    
    cocoon.sendPage("sitetree_link_library_template", {"resources" : resources});
}

/**
 * Collects infos about all image resources in a publication.
 */
function publication_image_library() {        
    var pageEnvelope = new FlowHelper().getPageEnvelope(cocoon);
    var pubHelper = new PublicationHelper(pageEnvelope.getPublication());
    var allDocs = pubHelper.getAllDocuments(pageEnvelope.getDocument().getArea(), pageEnvelope.getDocument().getLanguage());
    var imageInfos = new ArrayList();
    
    for(var i=0; i<allDocs.length; i++) {
        if(allDocs[i].getId().equals(pageEnvelope.getDocument().getId()))
            continue;
        var resourcesMgr = new ResourcesManager(allDocs[i]);
        var imageResources = resourcesMgr.getImageResources();
        
        for(var j=0; j<imageResources.length; j++) {
            var metaDoc = org.apache.lenya.xml.DocumentHelper.readDocument(resourcesMgr.getMetaFile(imageResources[j]));
            title = metaDoc.getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", "title").item(0).getChildNodes().item(0).getNodeValue();
            
            imageInfos.add({
                    "url" : pageEnvelope.getContext() + "/" + resourcesMgr.getResourceUrl(imageResources[j]),
                    "name" : imageResources[j].getName(),
                    "title" : title,
                    "length" : imageResources[j].length(),
                    "iconUrl" : cocoon.parameters["iconUrl"]
            });
        }
    }
    cocoon.sendPage(cocoon.parameters["template"], {"imageInfos" : imageInfos});
}

/**
 * Saves the edited document submitted by Kupu.
 */
function save() {
    cocoon.log.debug("Starting to save Kupu doc.");

    try {        
        new FlowHelper().savePipelineToDocument(cocoon, cocoon.parameters["template"]);
        cocoon.response.setStatus(204); // Expected by Kupu upon successful save
    } catch(e) { 
        cocoon.log.error("Error saving Kupu doc.", e);
        cocoon.response.setStatus(500);// Send real error page? Kupu wo't display it anyway, but would be cleaner 
    }
}