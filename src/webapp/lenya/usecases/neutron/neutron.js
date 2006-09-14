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

importClass(Packages.org.apache.lenya.cms.cocoon.flow.FlowHelper);
importClass(Packages.org.apache.excalibur.source.SourceResolver);
importClass(Packages.org.apache.lenya.cms.cocoon.source.SourceUtil);
importClass(Packages.org.apache.lenya.ac.Identity);
importClass(Packages.org.apache.lenya.cms.publication.DefaultDocumentIdToPathMapper);
importClass(Packages.org.apache.lenya.ac.AccreditableManager);

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
   cocoon.sendPage("xml/" + path);
  
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
    cocoon.sendPage("exception-checkout.jx", {"message": e});
    return;
  }
  cocoon.sendPage("xml/" + path);
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
  cocoon.sendPage("xml/" + path);
 
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
