importClass(Packages.java.util.ArrayList);

importClass(Packages.org.apache.cocoon.components.ContextHelper);

importClass(Packages.org.apache.lenya.cms.cocoon.flow.FlowHelper);
importClass(Packages.org.apache.lenya.cms.publication.Publication);
importClass(Packages.org.apache.lenya.cms.publication.DocumentHelper);

/**
 * Collects all link/url information from a publication's siteree.
 * This information is used to generate xml for the Kupu (link) library drawer.
 * @version $Id$
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