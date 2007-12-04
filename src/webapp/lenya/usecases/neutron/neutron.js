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

importClass(Packages.org.apache.lenya.cms.cocoon.flow.FlowHelper);
importClass(Packages.org.apache.excalibur.source.SourceResolver);
importClass(Packages.org.apache.lenya.cms.cocoon.source.SourceUtil);
importClass(Packages.org.apache.lenya.ac.Identity);
importClass(Packages.org.apache.lenya.cms.publication.DefaultDocumentIdToPathMapper);
importClass(Packages.org.apache.lenya.ac.AccreditableManager);
importClass(Packages.org.apache.lenya.cms.authoring.UploadHelper);
importClass(Packages.org.apache.lenya.cms.publication.ResourcesManager);
importClass(Packages.java.io.File);
importClass(Packages.java.io.FileInputStream);


/**
 * Provides flow functions for Neutron protocol support.
   @see http://www.wyona.org/osr-101/osr-101.xhtml
 * @version $Id$ tc
 */


/**
 * Returns a document from the current page envelope
 */
function open() {

   var flowHelper = new FlowHelper();
   var document = flowHelper.getPageEnvelope(cocoon).getDocument();

   var path = "/" + document.getId() + "/index_" + document.getLanguage() + ".xml";
   cocoon.sendPage("xmlsource/" + path);
  
}


/**
 * Saves a document from current page envelope
 */

function save() { 
   
   var wfFactory = Packages.org.apache.lenya.cms.workflow.WorkflowFactory.newInstance();
   var flowHelper = new FlowHelper();
   var document = flowHelper.getPageEnvelope(cocoon).getDocument();
   
   try {
     flowHelper.reservedCheckIn(cocoon, true);
     var resolver = cocoon.getComponent(SourceResolver.ROLE);
     var dstUri = flowHelper.getDocumentHelper(cocoon).getSourceUri(document);
     SourceUtil.copy(resolver, "cocoon:/request2document", dstUri, true);
     var hasWorkflow = wfFactory.hasWorkflow(document);
     if(hasWorkflow)
         flowHelper.triggerWorkflow(cocoon, _getParameter("workflowEvent", "edit"));

     cocoon.sendStatus(200);
     return;
     
   } catch (e) {
    cocoon.response.setStatus(500);
    cocoon.sendPage("exception-checkin.jx", {"message" : e});
    return;
  } 
 	
}


/**
 * Checks out and returns a document from the current page envelope 
 */

function checkout () {

  var flowHelper = new FlowHelper();
  var document = flowHelper.getPageEnvelope(cocoon).getDocument();
  var rc = flowHelper.getRevisionController(cocoon);
  
  var identity = new Identity();
  var user = identity.getIdentity(cocoon.session).getUser();
  
  var docToPathMapper = new DefaultDocumentIdToPathMapper();
  var path = docToPathMapper.getPath(document.getId(), document.getLanguage());
 
  try {
    rc.reservedCheckOut("/content/authoring/" + path, user.getId());
    
  } catch (e) {
    cocoon.response.setStatus(500);

    var user = e.getCheckOutUsername();
    var date = e.getCheckOutDate(); 
    cocoon.sendPage("exception-checkout.jx", {"user": user, "date": date });
    return;
  }
  cocoon.sendPage("xmlsource/" + path);
}


/**
 * Checks in and saves a document from the current page envelope  
 */

function checkin () {

   var flowHelper = new FlowHelper();
   var document = flowHelper.getPageEnvelope(cocoon).getDocument();
  
   try {
     flowHelper.reservedCheckIn(cocoon, true);
     var resolver = cocoon.getComponent(SourceResolver.ROLE);
     var dstUri = flowHelper.getDocumentHelper(cocoon).getSourceUri(document);
     SourceUtil.copy(resolver, "cocoon:/request2document", dstUri, true);
     cocoon.sendStatus(200);
     return;
     
   } catch (e) {
    cocoon.response.setStatus(500);
    cocoon.sendPage("exception-checkin.jx", {"message" : e});
    return;
  }
  
}


/**
 * Locks a document from the current page envelope  
 */

function lock() {
  
  var flowHelper = new FlowHelper();
  var document = flowHelper.getPageEnvelope(cocoon).getDocument();
  var rc = flowHelper.getRevisionController(cocoon);
  
  var identity = new Identity();
  var user = identity.getIdentity(cocoon.session).getUser();

  var docToPathMapper = new DefaultDocumentIdToPathMapper();
  var path = docToPathMapper.getPath(document.getId(), document.getLanguage());
 
  try {
    rc.reservedCheckOut("/content/authoring/" + path, user.getId());
    
  } catch (e) {
    cocoon.response.setStatus(500);
    cocoon.sendPage("exception-checkout.jx", {"message" : e});
    return;
  }
  cocoon.sendPage("xmlsource/" + path);
 
}


/**
 * Unlocks document from the current page envelope.     
 */

function unlock () {

  var flowHelper = new FlowHelper();
  var document = flowHelper.getPageEnvelope(cocoon).getDocument();
    
  try {
    flowHelper.reservedCheckIn(cocoon, true);
    cocoon.sendStatus(200);
    return;
   
  } catch (e) {
    cocoon.log.error(e);
    cocoon.response.setStatus(500);
    cocoon.sendPage("exception-checkin.jx", {"message" : e});
    return;
  }
}


/**
 * Upload
 */


function upload () {

  var flowHelper = new FlowHelper();
  var uploadHelper = new UploadHelper("/tmp");

  var uploadFile = cocoon.request.get("upload-file");
  var title = cocoon.request.get("title");
  var creator = cocoon.request.get("creator");
  var rights = cocoon.request.get("rights");
  var mimeType = cocoon.request.get("mimeType");

  var width = cocoon.request.get("width");
  var height = cocoon.request.get("height");  

  cocoon.log.error("Width, height: " + width + ", " + height);   

  if (!uploadFile) {
    cocoon.sendStatus(403);
    return;
  }


  cocoon.log.error("Upload File: " + uploadFile + "\n");

  var document = flowHelper.getPageEnvelope(cocoon).getDocument();
  var file = uploadHelper.save(cocoon.request, "upload-file"); 
  var fileName = file.getName();
  var extent = file.length(); 

  var resolver = cocoon.getComponent(Packages.org.apache.excalibur.source.SourceResolver.ROLE);
  var resourcesManager = new ResourcesManager(document);
    
  var assetDirectoryPath = resourcesManager.getPath();
  var assetPath = assetDirectoryPath +  File.separator + fileName;
  var targetSource = resolver.resolveURI(assetPath);

  var is = new FileInputStream(file);
  var os = targetSource.getOutputStream();

  var buffer = new java.lang.reflect.Array.newInstance(java.lang.Byte.TYPE, 1024);
  var len;

  try {
    while((len = is.read(buffer)) >= 0)
      os.write(buffer, 0, len);
  } catch (e) {

  } finally {
    is.close();
    os.close();
  }

  // remove tmp file 
  try {
    file["delete"](); 
  } catch(e) {}


  var metaFilePath = resolver.resolveURI(assetPath + ".meta");
  var metaOs = metaFilePath.getOutputStream();

  if (width && height) {
    var dimensions = new Object(); 
    dimensions.width = width; 
    dimensions.height = height; 
    cocoon.processPipelineTo("createmetadata", {"mimeType" : mimeType, "extent" : extent, "title" : title, "creator": creator, "dimensions": dimensions }, metaOs);
  } else {
    cocoon.processPipelineTo("createmetadata", {"mimeType" : mimeType, "extent" : extent, "title" : title, "creator": creator }, metaOs);
  }

  metaOs.close();

  var assetFile = new File(assetPath);

  if (assetFile.exists()) {
    cocoon.sendPage("upload-success.jx", {"filename" : fileName});  
  } else {
    cocoon.sendStatus(403);
  }  

}



/**
 * Authenticates a credential contained in the request body. Requests another 
 * authentication attempt if that fails.  
 */

function authenticate() {

   var pipelineUtil = cocoon.createObject(Packages.org.apache.cocoon.components.flow.util.PipelineUtil);
   var flowHelper = new FlowHelper();
   var accessControllerResolver = cocoon.getComponent("org.apache.lenya.ac.AccessControllerResolverSelector").select("publication");
   var identity = new Identity();
   
   var accessController = accessControllerResolver.resolveAccessController("/" + flowHelper.getPageEnvelope(cocoon).getPublication().getId() + "/authoring/index.html");
   var accreditableManager = accessController.getAccreditableManager();
   var ident = identity.getIdentity(cocoon.session);
   
   var dom = pipelineUtil.processToDOM("request2document", null);   
   var params = dom.getDocumentElement().getElementsByTagName("param");
   
   var username;
   var password;
   
   for (var i = 0; i < params.length; i++) {
     if (params.item(i).getAttribute("name") == "username") {
       if (params.item(i).getFirstChild() != null)
         username = params.item(i).getFirstChild().nodeValue;
     }
     if (params.item(i).getAttribute("name") == "password") { 
       if (params.item(i).getFirstChild() != null)
         password = params.item(i).getFirstChild().nodeValue;
     }
   }
   
   var user = accreditableManager.getUserManager().getUser(username);
   
   if (user != null && user.authenticate(password)) {
     ident.addIdentifiable(user);
  	 cocoon.sendStatus(200);
  	 return;
   } 
   
   cocoon.sendPage("exception-auth"); 

}

function _getParameter(name, defaultValue) {
    if(cocoon.parameters[name])
        return cocoon.parameters[name];
    else
        return defaultValue;
}
