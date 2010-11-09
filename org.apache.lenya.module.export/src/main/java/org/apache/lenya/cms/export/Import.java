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
package org.apache.lenya.cms.export;

import java.io.File;

import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.usecase.AbstractUsecase;
import org.apache.lenya.utils.URLInformation;
/**
 * Import content.
 */
public class Import extends AbstractUsecase {

    private Importer importer;
    
		protected void initParameters() {
			
        super.initParameters();
        //String pubId = new URLInformation(getSourceURL()).getPublicationId();
        String pubId = new URLInformation().getPublicationId();
        Publication pub = getSession().getPublication(pubId);
        String path = getExampleContentPath(pub);
        if (!new File(path).exists()) {
            path = getExampleContentPath(getDefaultPub());
        }
        setParameter("path", path);
    }

    protected String getExampleContentPath(Publication publication) {
        return publication.getSourceUri() + "/example-content";
    }

    protected Publication getDefaultPub() {
        return getSession().getPublication("default");
    }

    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        Area area = getArea();
        Document[] doc = area.getDocuments();
        String test = "test";
        if (area.getDocuments().length > 0) {
            addErrorMessage("You can't import anything because this publication already contains content.");
        }
    }

    protected Area getArea() {
        /*String url = getSourceURL();
        URLInformation info = new URLInformation(url);*/
    	URLInformation info = new URLInformation();
        String pubId = info.getPublicationId();
        String areaName = info.getArea();
        return getSession().getPublication(pubId).getArea(areaName);
    }

    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();
        String path = getParameterAsString("path");
        String baseUri = "file://" + path;
        String sitetreeUri = baseUri + "/sitetree.xml";
        if (!importer.checkSitetreeUri(sitetreeUri)){
        	addErrorMessage("The sitetree file does not exist in this directory.");
        }
        
    }

    protected void doExecute() throws Exception {
        super.doExecute();
        String path = getParameterAsString("path");
        /*Importer importer = new Importer(getLogger());
        importer.setDocumentManager(getDocumentManager());
        importer.setResourceTypeResolver(getResourceTypeResolver());
        importer.setSourceResolver(getSourceResolver());*/
        
        importer.setLogger(getLogger());
        importer.importContent(getDefaultPub(), getArea(), path);
    }
    
    public Importer getImporter() {
			return importer;
		}

		public void setImporter(Importer importer) {
			this.importer = importer;
		}

}
