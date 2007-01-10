/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

importClass(Packages.java.util.ArrayList);

importClass(Packages.org.apache.lenya.cms.cocoon.flow.FlowHelper);
importClass(Packages.org.apache.lenya.cms.publication.util.DocumentHelper);

/**
 * Kupu usecase flow.
 * @version $Id$
 */

/**
 * Collects all link/url information from a publication's siteree.
 * This information is used to generate xml for the Kupu (link) library drawer.
 */
function sitetree_link_library() {
    
    var flowHelper = cocoon.getComponent("org.apache.lenya.cms.cocoon.flow.FlowHelper");
    var documentHelper = flowHelper.getDocumentHelper(cocoon);
    var pageEnvelope = flowHelper.getPageEnvelope(cocoon);
    var areaName = pageEnvelope.getArea();
    var area = pageEnvelope.getPublication().getArea(areaName);
    var siteTree = area.getSite();
    var allNodes = siteTree.preOrder();
    var resources = new ArrayList(allNodes.length - 1);
    var addedResourcesCount = 0;
    var language = pageEnvelope.getDocument().getLanguage();
    
    for(var i=1; i < allNodes.length; i++) {
        if (allNodes[i].hasLink(language)) {
    		var languageLabel = allNodes[i].getLink(language);
			/* If the current sitree node does not exist in the displayed document's language
		 	 * Continue with next node. This is a quick fix for bug #32808.
		 	 * Next step would be to offer all links to the available languages.
		 	 * by roku 
		 	*/
		
        	resources.add(addedResourcesCount, {
                "url" : documentHelper.getDocumentUrl(allNodes[i].getUuid(), pageEnvelope.getDocument().getArea(), null),
                "label" : languageLabel.getLabel(),
                "id" : allNodes[i].getName(),
                "fullid" : allNodes[i].getPath(),
                "language" : pageEnvelope.getDocument().getLanguage()});
        	addedResourcesCount++;
        }
    }
    
    cocoon.sendPage("sitetree_link_library_template", {"resources" : resources});
}

/**
 * Collects infos about all image resources in a publication.
FIXME PublicationHelper does no longer exist
function publication_image_library() {        
    var pageEnvelope = new FlowHelper().getPageEnvelope(cocoon);
    var pubHelper = new PublicationHelper(pageEnvelope.getPublication());
    var allDocs = pubHelper.getAllDocuments(pageEnvelope.getDocument().getArea(), pageEnvelope.getDocument().getLanguage());
    var imageInfos = new ArrayList();
    
    for(var i=0; i<allDocs.length; i++) {
        if(allDocs[i].getId().equals(pageEnvelope.getDocument().getId()))
            continue;
        var resourcesMgr = new DefaultResourcesManager(allDocs[i]);
        var imageResources = resourcesMgr.getImageResources();
        
        for(var j=0; j<imageResources.length; j++) {
            var metaDoc = org.apache.lenya.xml.DocumentHelper.readDocument(resourcesMgr.getMetaFile(imageResources[j]));
            var title = metaDoc.getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", "title").item(0).getChildNodes().item(0).getNodeValue();
            
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
*/