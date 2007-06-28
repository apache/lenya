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
                "url" : "lenya-document:" + allNodes[i].getUuid(),
                "title" : getTitle(allNodes[i].getLink(language).getDocument()),
                "label" : languageLabel.getLabel(),
                "id" : allNodes[i].getName(),
                "fullid" : allNodes[i].getPath(),
                "language" : pageEnvelope.getDocument().getLanguage()});
        	addedResourcesCount++;
        }
    }
    cocoon.releaseComponent(flowHelper);
    cocoon.sendPage("sitetree_link_library_template", {"resources" : resources});
}

function getTitle(doc) {
    var meta = doc.getMetaData("http://purl.org/dc/elements/1.1/");
    var title = meta.getFirstValue("title");
    return title;
}

/**
 * Collects infos about all image resources in a publication.
 */
function publication_image_library() {        
    var flowHelper = cocoon.getComponent("org.apache.lenya.cms.cocoon.flow.FlowHelper");
    var pageEnvelope = flowHelper.getPageEnvelope(cocoon);
    var areaName = pageEnvelope.getArea();
    var site = pageEnvelope.getPublication().getArea(areaName).getSite();
    var imageInfos = new ArrayList();
    
    var rootPath = cocoon.parameters["rootPath"];
    var allNodes = site.getNode(rootPath).preOrder();
    
    var baseUrl = cocoon.parameters["baseUrl"];
    
    for (var i=0; i < allNodes.length; i++) {
    
        if (allNodes[i].getPath().equals(pageEnvelope.getDocument().getPath()))
            continue;
            
        var languages = allNodes[i].getLanguages();
        for (var lang = 0; lang < languages.length; lang++) {
            var doc = allNodes[i].getLink(languages[lang]).getDocument();
            if (doc.getResourceType().getName().equals("resource")) {
            
	            var title = getTitle(doc);
	            var url = doc.getCanonicalDocumentURL();
	            url = url.substring(0, url.length() - 4);
	            url = url + doc.getSourceExtension();
	            
	            imageInfos.add({
	                    "previewurl" : baseUrl + url,
	                    "url" : baseUrl + url,
	                    "name" : doc.getName(),
	                    "title" : title,
	                    "length" : doc.getContentLength(),
	                    "iconUrl" : cocoon.parameters["iconUrl"]
	            });
            }
        }
            
    }
    cocoon.releaseComponent(flowHelper);
    cocoon.sendPage(cocoon.parameters["template"], {"imageInfos" : imageInfos});
}
