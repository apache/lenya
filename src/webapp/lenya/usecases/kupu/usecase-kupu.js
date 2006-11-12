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

importClass(Packages.java.util.ArrayList);

importClass(Packages.org.apache.lenya.cms.cocoon.flow.FlowHelper);
importClass(Packages.org.apache.lenya.cms.publication.util.DocumentHelper);
importClass(Packages.org.apache.lenya.cms.publication.DefaultResourcesManager);

/**
 * Kupu usecase flow.
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
    var addedResourcesCount = 0;
    
    for(var i=1; i<allNodes.size(); i++) {
    	var languageLabel = allNodes.get(i).getLabel(pageEnvelope.getDocument().getLanguage())
		/* If the current sitree node does not exist in the displayed document's language
		 * Continue with next node. This is a quick fix for bug #32808.
		 * Next step would be to offer all links to the available languages.
		 * by roku 
		 */
		if (languageLabel == null) continue;
		
        resources.add(addedResourcesCount, {
                "url" : documentHelper.getDocumentUrl(allNodes.get(i).getAbsoluteId(), pageEnvelope.getDocument().getArea(), null),
                "label" : languageLabel.getLabel(),
                "id" : allNodes.get(i).getId(),
                "fullid" : allNodes.get(i).getAbsoluteId(),
                "language" : pageEnvelope.getDocument().getLanguage()});
        addedResourcesCount++;
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