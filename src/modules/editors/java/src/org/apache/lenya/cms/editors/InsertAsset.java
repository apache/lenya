/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lenya.ac.User;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.usecase.UsecaseInvoker;
import org.apache.lenya.cms.usecase.UsecaseMessage;
import org.apache.lenya.util.ServletHelper;

/**
 * Usecase to insert an image into a document.
 * 
 * @version $Id$
 */
public class InsertAsset extends DocumentUsecase {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        loadResources();
        
        try {
            User user = getSession().getIdentity().getUser();
            if (user != null) {
                setParameter("creator", user.getId());
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.DocumentUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        if (!ServletHelper.isUploadEnabled(manager)) {
            addErrorMessage("Upload is not enabled please check local.build.properties!");
        }
    }

    protected Document[] getResourceDocuments() throws DocumentException {
        List list = new ArrayList();
        Document[] docs = getSourceDocument().area().getDocuments();
        for (int i = 0; i < docs.length; i++) {
            if (docs[i].getResourceType().getName().equals("resource")) {
                list.add(docs[i]);
            }
        }
        return (Document[]) list.toArray(new Document[list.size()]);
    }    
    
    protected void loadResources() {        
        try {            
            Document[] resources = getResourceDocuments();

            List selectedResources = new ArrayList();
            String mimeTypePrefix = getParameterAsString("mimeTypePrefix", "");
            for (int i = 0; i < resources.length; i++) {
                String resMimeType = resources[i].getMimeType();
                if(resMimeType == null)
                    resMimeType = "unknown";
                if (resMimeType.startsWith(mimeTypePrefix)) {
                    selectedResources.add(resources[i]);
                }
            }

            setParameter("assets", selectedResources);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } 
    }

    /**
     * Delegates to the main assets usecase; the name of
     * the usecase being delegated to is set in the
     * configuration parameter "asset-usecase".
     *
     * @see org.apache.lenya.cms.usecase.Usecase#advance()
     */
    public void advance() throws UsecaseException {
        super.advance();
        if (getParameterAsBoolean("upload", false)) {
            UsecaseInvoker invoker = null;
            try {
                invoker = (UsecaseInvoker) this.manager.lookup(UsecaseInvoker.ROLE);
                String usecaseName = getParameterAsString("asset-usecase");

                if (getLogger().isDebugEnabled())
                    getLogger().debug("InsertAsset::advance() calling invoker with usecaseName [" + usecaseName + "]");
                invoker.invoke(getSourceURL(), usecaseName, getParameters());
                if (invoker.getResult() == UsecaseInvoker.SUCCESS) {
                    loadResources();
                    deleteParameter("title");
                    deleteParameter("creator");
                    deleteParameter("rights");
                }
                else {
                    List messages = invoker.getErrorMessages();
                    for (Iterator i = messages.iterator(); i.hasNext(); ) {
                        UsecaseMessage message = (UsecaseMessage) i.next();
                        addErrorMessage(message.getMessage());
                    }
                }
                /*
                 * The <input type="file"/> value cannot be passed to the next screen because
                 * the browser doesn't allow this for security reasons.
                 */
                deleteParameter("file");
            }
            catch (Exception e) {
                throw new UsecaseException(e);
            } finally {
                if (invoker != null) {
                    this.manager.release(invoker);
                }
            }
        }
    }

}
