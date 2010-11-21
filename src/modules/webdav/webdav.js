/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

/* Helper method to add all request parameters to a usecase */
function passRequestParameters(flowHelper, usecase) {
    var names = cocoon.request.getParameterNames();
    while (names.hasMoreElements()) {
        var name = names.nextElement();
        if (!name.equals("lenya.usecase")
            && !name.equals("lenya.continuation")
            && !name.equals("submit")) {
            
            var value = flowHelper.getRequest(cocoon).get(name);
            
            var string = new Packages.java.lang.String();
            if (string.getClass().isInstance(value)) {
                usecase.setParameter(name, value);
            }
            else {
                usecase.setPart(name, value);
            }
            
        }
    }
}

/* Helper method to add all request headers to a usecase */
function passRequestHeaders(flowHelper, usecase) {
    var names = cocoon.request.getHeaderNames();
    while (names.hasMoreElements()) {
        var name = names.nextElement();
        var value = flowHelper.getRequest(cocoon).getHeader(name);
            
        var string = new Packages.java.lang.String();
        if (string.getClass().isInstance(value)) {
            usecase.setParameter("header-"+name, value);
        }
        else {
            usecase.setPart("header-"+name, value);
        }
    }
}

function selectMethod() {
  var page = cocoon.parameters["page"];
  var requestURI = cocoon.parameters["requestURI"];
  var method = cocoon.request.getMethod();
  cocoon.sendPage(method+"/"+page, {"requestURI":requestURI});
}

function sendStatus(sc) {
  cocoon.sendStatus(sc);
}
 
function mkcol() {
  var status = executeUsecase("webdav.mkcol");
  if(status)
    sendStatus(201);
  else
    sendStatus(403);
}
 
function put() {
  var status = executeUsecase("webdav.put");
  if(status)
    sendStatus(204);
  else {
    sendStatus(415);
  }
}

function remove() {
  var status = executeUsecase("webdav.delete");
  sendStatus(200);
}

function get() {
  var uri = new java.lang.String(cocoon.parameters["forward"]);
  var uri = uri.substring(8, uri.length());
  cocoon.sendPage(uri);
}

function filePropfind() {
  var status = executeUsecase("webdav.filePropfind");
  sendStatus(500);
}

function propfind() {
  var status = executeUsecase("webdav.propfind");
  sendStatus(500);
}

function options() {
  cocoon.response.setHeader("DAV","1");
  var type = "";
  var options = "";
  if (type == "file") {
    options = "OPTIONS,GET,HEAD,POST,DELETE,TRACE,PROPFIND,PROPPATCH,COPY,MOVE,PUT,LOCK,UNLOCK";
  } else {
    options = "OPTIONS,GET,HEAD,POST,DELETE,TRACE,PROPFIND,PROPPATCH,COPY,MOVE,LOCK,UNLOCK";
  }
  cocoon.response.setHeader("Allow",options);

  //interoperability with Windows 2000
  var w2kDAVDiscoverAgent = "Microsoft Data Access Internet"
                          + " Publishing Provider Protocol Discovery";
  if (cocoon.request.getHeader("User-Agent") == w2kDAVDiscoverAgent) {
      cocoon.response.setHeader("MS-Author-Via","DAV");
  }

  sendStatus(200);
}

/*
 * parse the depth header to find out if recursion
 * take place. (used by MOVE and COPY)
 */
function isRecurse(depth) {
  var recurse;
  if (depth == null || depth == '') {
    recurse = true;
  }
  else if (depth == 'Infinity') {
    recurse = true;
  }
  else {
    recurse = false;
  }
  return recurse;
}

/*
 * convert the overwrite header into a boolean type
 */
function isOverwrite(header) {
  var overwrite = true;
  if (header == 'F') {
    overwrite = false;
  }
  return overwrite;
}

function executeUsecase(usecaseName) {
    var view;
    var proxy;
    var menu = "nomenu";
    
    var usecaseResolver;
    var usecase;
    var sourceUrl;
    
    var preconditionsOk;
    
    if (cocoon.log.isDebugEnabled())
       cocoon.log.debug("webdav.js::executeUsecase() called, parameter lenya.usecase = [" + usecaseName + "]");
    
    try {

        var flowHelper = cocoon.getComponent("org.apache.lenya.cms.cocoon.flow.FlowHelper");
        var request = flowHelper.getRequest(cocoon);
        sourceUrl = Packages.org.apache.lenya.util.ServletHelper.getWebappURI(request);
        
        usecaseResolver = cocoon.getComponent("org.apache.lenya.cms.usecase.UsecaseResolver");
        usecase = usecaseResolver.resolve(sourceUrl, usecaseName);
        usecase.setSourceURL(sourceUrl);
        usecase.setName(usecaseName);
        view = usecase.getView();

        passRequestParameters(flowHelper, usecase);
        passRequestHeaders(flowHelper, usecase);
        usecase.checkPreconditions();
        preconditionsOk = !usecase.hasErrors();
        if (preconditionsOk) {
            usecase.lockInvolvedObjects();
        }
        proxy = new Packages.org.apache.lenya.cms.usecase.impl.UsecaseProxy(usecase);
    }
    finally {
        /* done with usecase component, tell usecaseResolver to release it */
        if (usecaseResolver != null) {
            if (usecase != null) {
                usecaseResolver.release(usecase);
                usecase = undefined;
            }
            cocoon.releaseComponent(usecaseResolver);
        }
    }
    
    var success = false;
    //var targetUrl;
    var form;
    var scriptString;
    var evalFunc;
    var generic;

    /*
     * If the usecase has a view, this means we want to display something 
     * to the user before proceeding. This also means the usecase consists
     * several steps; repeated until the user chooses to submit or cancel.
     *
     * If the usecase does not have a view, it is simply executed.
     */

    if (view) {
        var ready = false;
        while (!ready) {

            try {
                var templateUri = view.getViewURI();
                if (templateUri) {
                    var viewUri = "view/" + menu + "/" + view.getViewURI();
                    if (cocoon.log.isDebugEnabled())
                        cocoon.log.debug("webdav.js::executeUsecase() in usecase " + usecaseName + ", creating view, calling Cocoon with viewUri = [" + viewUri + "]");

                    cocoon.sendPageAndWait(viewUri, {"usecase" : proxy});
                    
                }
                else {
                    var viewUri = view.getViewURI();
                    cocoon.sendPage(viewUri);
                    return;
                }
            }
            catch (exception) {
                /* if an exception was thrown by the view, allow the usecase to rollback the transition */
		if (cocoon.log.isDebugEnabled())
		    cocoon.log.debug("webdav.js::executeUsecase() in usecase " + usecaseName + ": exception " + exception.name + " caught, message is:\n" + exception.message);

                try {
                    usecaseResolver = cocoon.getComponent("org.apache.lenya.cms.usecase.UsecaseResolver");
                    usecase = usecaseResolver.resolve(sourceUrl, usecaseName);
                    proxy.setup(usecase);
                    usecase.cancel();
                    throw exception;
                }
                finally {
                    if (usecaseResolver != null) {
                        if (usecase != null) {
                            usecaseResolver.release(usecase);
                            usecase = undefined;
                        }
                        cocoon.releaseComponent(usecaseResolver);
                    }
                }
            }
            
            if (cocoon.log.isDebugEnabled())
                cocoon.log.debug("webdav.js::executeUsecase() in usecase " + usecaseName + ", after view, now advancing in usecase");
        
            try {
                usecaseResolver = cocoon.getComponent("org.apache.lenya.cms.usecase.UsecaseResolver");
                usecase = usecaseResolver.resolve(sourceUrl, usecaseName);
                proxy.setup(usecase);
            
                passRequestParameters(flowHelper, usecase);
                usecase.advance();
            
                if (cocoon.request.getParameter("submit")) {
                    usecase.checkExecutionConditions();
                    if (! usecase.hasErrors()) {
                        usecase.execute();
                        if (! usecase.hasErrors()) {
                            usecase.checkPostconditions();
                            if (! usecase.hasErrors()) {
                                ready = true;
                                success = true;
                            }
                        }
                    }
                }
                else if (cocoon.request.getParameter("cancel")) {
                    usecase.cancel();
                    ready = true;
                }
                proxy = new Packages.org.apache.lenya.cms.usecase.UsecaseProxy(usecase);
            }
            catch (exception) {
                /* allow usecase to rollback the transition */
                usecase.cancel();
                throw exception;
            }
            finally {
                if (usecaseResolver != null) {
                    if (usecase != null) {
                        usecaseResolver.release(usecase);
                        usecase = undefined;
                    }
                    cocoon.releaseComponent(usecaseResolver);
                }
            }
        }
    }
    else {
        try {
            usecaseResolver = cocoon.getComponent("org.apache.lenya.cms.usecase.UsecaseResolver");
            usecase = usecaseResolver.resolve(sourceUrl, usecaseName);
            proxy.setup(usecase);
            
            if (preconditionsOk) {
                usecase.checkExecutionConditions();
                if (!usecase.hasErrors()) {
                    usecase.execute();
                    if (! usecase.hasErrors()) {
                        usecase.checkPostconditions();
                        if (! usecase.hasErrors()) {
                            success = true;
                        }
                    }
                }
            }
        }
        catch (exception) {
            /* allow usecase to rollback the transition */
            usecase.cancel();
            throw exception;
        }
        finally {
            usecaseResolver.release(usecase);
            usecase = undefined;
            cocoon.releaseComponent(usecaseResolver);
        }
    }
        
    return success;
    
}
