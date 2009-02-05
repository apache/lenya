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
package org.apache.lenya.cms.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.cocoon.components.context.ContextUtility;
import org.apache.lenya.cms.linking.LinkRewriter;
import org.apache.lenya.cms.linking.OutgoingLinkRewriter;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.usecase.UsecaseInvoker;
import org.apache.lenya.cms.usecase.UsecaseMessage;
import org.apache.lenya.util.ServletHelper;
import org.apache.lenya.cms.site.usecases.CreateResource;

/**
 * Usecase to insert an image into a document.
 * 
 * @version $Id$
 */
public class InsertAsset extends CreateResource {

    protected static final String DOCUMENT = "document";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        deleteParameter(RELATIONS);
        loadResources();

        setParameter(DOCUMENT, getSourceDocument());
        try {
            User user = getSession().getIdentity().getUser();
            if (user != null) {
                setParameter("creator", user.getId());
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void doCheckPreconditions() throws Exception {
        if (!ServletHelper.isUploadEnabled(manager)) {
            addErrorMessage("Upload is not enabled please check local.build.properties!");
        }
    }

    protected Document[] getResourceDocuments() throws DocumentException {
        String mimeTypePrefix = getParameterAsString("mimeTypePrefix", "");
        List list = new ArrayList();
        Document[] docs = getSourceDocument().area().getDocuments();
        for (int i = 0; i < docs.length; i++) {
            if (docs[i].getResourceType().getName().equals("resource")) {
                String resMimeType = docs[i].getMimeType();
                if (resMimeType == null) {
                    resMimeType = "unknown";
                }
                if (resMimeType.startsWith(mimeTypePrefix)) {
                    list.add(docs[i]);
                }
            }
        }
        return (Document[]) list.toArray(new Document[list.size()]);
    }

    protected void loadResources() {
        ContextUtility context = null;
        try {
            context = (ContextUtility) this.manager.lookup(ContextUtility.ROLE);
            Request request = context.getRequest();
            boolean ssl = request.isSecure();

            LinkRewriter rewriter = new OutgoingLinkRewriter(this.manager, getSession(),
                    getSourceURL(), ssl, false, false);
            Map asset2proxyUrl = new HashMap();
            setParameter("asset2proxyUrl", asset2proxyUrl);

            Document[] resources = getResourceDocuments();

            for (int i = 0; i < resources.length; i++) {

                String originalUrl = resources[i].getCanonicalWebappURL();
                int lastDotIndex = originalUrl.lastIndexOf('.');
                String extension = resources[i].getSourceExtension();
                String url = originalUrl.substring(0, lastDotIndex) + "." + extension;

                String proxyUrl = rewriter.rewrite(url);
                asset2proxyUrl.put(resources[i], proxyUrl);

            }

            setParameter("assets", resources);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (context != null) {
                this.manager.release(context);
            }
        }
    }

    /**
     * Delegates to the main assets usecase; the name of the usecase being
     * delegated to is set in the configuration parameter "asset-usecase".
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
                    getLogger().debug(
                            "InsertAsset::advance() calling invoker with usecaseName ["
                                    + usecaseName + "]");
                invoker.invoke(getSourceURL(), usecaseName, getParameters());
                if (invoker.getResult() == UsecaseInvoker.SUCCESS) {
                    loadResources();
                    deleteParameter("title");
                    deleteParameter("creator");
                    deleteParameter("rights");
                } else {
                    List messages = invoker.getErrorMessages();
                    for (Iterator i = messages.iterator(); i.hasNext();) {
                        UsecaseMessage message = (UsecaseMessage) i.next();
                        addErrorMessage(message.getMessage());
                    }
                }
                /*
                 * The <input type="file"/> value cannot be passed to the next
                 * screen because the browser doesn't allow this for security
                 * reasons.
                 */
                deleteParameter("file");
            } catch (Exception e) {
                throw new UsecaseException(e);
            } finally {
                if (invoker != null) {
                    this.manager.release(invoker);
                }
            }
        }
    }

}
