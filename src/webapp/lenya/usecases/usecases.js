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
/* $Id$ */
 
 cocoon.load("resource://org/apache/cocoon/forms/flow/javascript/Form.js");
 cocoon.load("usecases-util.js");

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


/*
 * Main function to execute a usecase.
 *
 * Uses request parameter "lenya.usecase" to determine what
 * usecase to execute.
 * 
 */
function executeUsecase() {
    var usecaseName = cocoon.request.getParameter("lenya.usecase");
    var view;
    var proxy;
    var menu = "nomenu";
    
    var usecaseResolver;
    var usecase;
    var sourceUrl;
    
    if (cocoon.log.isDebugEnabled())
       cocoon.log.debug("usecases.js::executeUsecase() called, parameter lenya.usecase = [" + usecaseName + "]");
    
    try {

        var flowHelper = cocoon.getComponent("org.apache.lenya.cms.cocoon.flow.FlowHelper");
        var request = flowHelper.getRequest(cocoon);
        sourceUrl = Packages.org.apache.lenya.util.ServletHelper.getWebappURI(request);
        
        usecaseResolver = cocoon.getComponent("org.apache.lenya.cms.usecase.UsecaseResolver");
        usecase = usecaseResolver.resolve(sourceUrl, usecaseName);
        usecase.setSourceURL(sourceUrl);
        usecase.setName(usecaseName);
        view = usecase.getView();
        if (view && view.showMenu()) {
            menu = "menu";
        }

        passRequestParameters(flowHelper, usecase);
        usecase.checkPreconditions();
        usecase.lockInvolvedObjects();
        proxy = new Packages.org.apache.lenya.cms.usecase.UsecaseProxy(usecase);
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
    var targetUrl;
    var form;
    var scriptString;
    var evalFunc;

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
                var templateUri = view.getTemplateURI();
                if (templateUri) {
                    /* Generic document variable that can be used in all funtions
                      FIXME: The rhino evalFunc() is broken that makes it impossible to
                      change this variable from a function. 
                      We need to use the usecase java class for it!
                    var genericDoc;*/
                    var viewUri = "view/" + menu + "/" + view.getTemplateURI();
                    if (cocoon.log.isDebugEnabled())
                        cocoon.log.debug("usecases.js::executeUsecase() in usecase " + usecaseName + ", creating view, calling Cocoon with viewUri = [" + viewUri + "]");
                    if (view.getViewType()=="cforms"){
                      var viewDef = "fallback://lenya/"+ view.getCformDefinition();
                      if (cocoon.log.isDebugEnabled())
                       cocoon.log.debug("usecases.js::executeUsecase()::cforms in usecase " + usecaseName + ", preparing formDefinition, calling Cocoon with viewUri = [" + viewDef + "]");
                       
                       // custom flowscript 
                       if (view.getCformDefinitionBefore()!=null){
                          scriptString= view.getCformDefinitionBefore();
                          evalFunc = new Function (scriptString);
                          evalFunc();
                       }

                       // form definition                       
                       form = new Form(viewDef);
                       
                       // custom flowscript 
                       if (view.getCformDefinitionAfter()!=null){
                          scriptString= view.getCformDefinitionAfter();
                          evalFunc = new Function ("form",scriptString);
                          evalFunc(form);
                       }

                       // form binding
                       if (view.getCformBinding() != null){
                          var viewBind = "fallback://lenya/"+ view.getCformBinding();
                          if (cocoon.log.isDebugEnabled())
                             cocoon.log.debug("usecases.js::executeUsecase()::cforms in usecase " + usecaseName + ", preparing formDefinition, calling Cocoon with viewUri = [" + viewBind + "]");
                             
                          // custom flowscript 
	                      if (view.getCformBindingBefore()!=null){
                              scriptString= view.getCformBindingBefore();
                              evalFunc = new Function ("form",scriptString);
                              evalFunc(form);
	                      }
	                      
	                      // form binding
	                      form.createBinding(viewBind);
	                      
	                      // custom flowscript 
	                      if (view.getCformBindingAfter()!=null){
                              scriptString= view.getCformBindingAfter();
                              evalFunc = new Function ("form",scriptString);
                              evalFunc(form);
	                      }
	                      
	                      // form template
                          form.showForm(viewUri, {"usecase" : proxy});
                          
                          // custom flowscript 
	                      if (view.getCformOutro()!=null){
                              scriptString= view.getCformOutro();
                              evalFunc = new Function ("form",scriptString);
                              evalFunc(form);
	                      }
                          
                       }
                    }
                    else{
                        cocoon.sendPageAndWait(viewUri, {
                            "usecase" : proxy
                        });
                    }
                }
                else {
                    var viewUri = view.getViewURI();
                    cocoon.sendPage(viewUri);
                    return;
                }
            }
            catch (exception) {
                /* if an exception was thrown by the view, allow the usecase to rollback the transition */
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
                cocoon.log.debug("usecases.js::executeUsecase() in usecase " + usecaseName + ", after view, now advancing in usecase");
        
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
                targetUrl = usecase.getTargetURL(success);
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
                
            usecase.execute();
            if (! usecase.hasErrors()) {
                usecase.checkPostconditions();
                if (! usecase.hasErrors()) {
                    success = true;
                }
            }
            targetUrl = usecase.getTargetURL(success);
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
    
    var url = request.getContextPath() + targetUrl;

    if (cocoon.log.isDebugEnabled())
       cocoon.log.debug("usecases.js::executeUsecase() in usecase " + usecaseName + ", completed, redirecting to url = [" + url + "]");
        
    cocoon.redirectTo(url);
    
}
