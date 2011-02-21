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
/* $Id$ */
 
cocoon.load("resource://org/apache/cocoon/forms/flow/javascript/Form.js");
importClass(org.apache.lenya.cms.usecase.Usecase);

//placeholders for custom flow code:
var customLoopFlow = undefined;
var customSubmitFlow = undefined;

/**
 * Get usecase.
 *
 * @param usecaseName, a string
 * @return a new org.apache.lenya.cms.usecase.Usecase Avalon component
 */
function getUsecase(usecaseName) {
    var flowHelper;
    var request;
    //var urlInformation;
    var sourceUrl;
    var usecaseResolver;
    var usecase;
    	log("error", "==================================== test =============================================");
    	
    	log("error", "usecaseName = " + usecaseName);
    	
        flowHelper = cocoon.getComponent("org.apache.lenya.cms.cocoon.flow.FlowHelper");
        
        log("error", "flowHelper = " + flowHelper);
        
        request = flowHelper.getRequest(cocoon);
        
        log("error", "request = " + request);
        
        //sourceUrl = Packages.org.apache.lenya.util.ServletHelper.getWebappURI(request);
        sourceUrl = Packages.org.apache.lenya.utils.ServletHelper.getCurrentURI();
        
        //urlInformation = cocoon.getComponent("org.apache.lenya.utils.URLInformation");
        //sourceURL = urlInformation.getWebappUrl();
        
        log("error", "sourceUrl = " + sourceUrl);
        
        usecaseResolver = cocoon.getComponent("org.apache.lenya.cms.usecase.UsecaseResolver");
        
        log("error", "usecaseResolver = " + usecaseResolver);
        
        usecase = usecaseResolver.resolve(sourceUrl, usecaseName);
        
        /**
         * TODO : remove this code : duplicate from usecaseResolver.resolve
         */
        /*usecase.setSourceURL(sourceUrl);
        usecase.setName(usecaseName);*/
        
        log("error", "usecase = " + usecase);
        
        
        
        
        
        log("error", "==================================== FIN test =============================================");
    try {
    } catch (exception) {
        log("error", "Error in getUsecase(): " + exception);
        log("debug", "usecaseName = " + usecaseName);
        log("debug", "flowHelper = " + flowHelper);
        log("debug", "request = " + request);
        log("debug", "sourceUrl = " + sourceUrl);
        log("debug", "usecaseResolver = " + usecaseResolver);
        log("debug", "usecase = " + usecase);
        throw exception;
    } finally {
        cocoon.releaseComponent(flowHelper);
        cocoon.releaseComponent(usecaseResolver);
    }
    return usecase;
}

/**
 * Release usecase. Since usecases are Avalon Components, they must
 * be released before a continuation is created.
 *
 * @param usecase, a org.apache.lenya.cms.usecase.Usecase Avalon component
 */
function releaseUsecase(usecase) {
    var usecaseResolver = cocoon.getComponent("org.apache.lenya.cms.usecase.UsecaseResolver");
    try {
        if (usecase != null) {
            usecaseResolver.release(usecase);
        }
    } finally {
        cocoon.releaseComponent(usecaseResolver);
    }
}

/**
 * Pass all parameters from the current request to a usecase
 * (except lenya.usecase, lenya.continuation and submit).
 *
 * @param usecase, a org.apache.lenya.cms.usecase.Usecase Avalon component
 */
function passRequestParameters(usecase) {
    var flowHelper = cocoon.getComponent("org.apache.lenya.cms.cocoon.flow.FlowHelper");
    var request = flowHelper.getRequest(cocoon);
    var names = cocoon.request.getParameterNames();
    while (names.hasMoreElements()) {
        var name = names.nextElement();
        // some parameters are handled elsewhere:
        if (!name.equals("lenya.usecase") 
            && !name.equals("lenya.continuation") 
            && !name.equals("submit")) { 
            // pass the rest on:
            var value = flowHelper.getRequest(cocoon).get(name);
            var string = new Packages.java.lang.String();
            var vector = new Packages.java.util.Vector();
            if (string.getClass().isInstance(value) || vector.getClass().isInstance(value)) {
                // use getParameters() to avoid character encoding problems
                var values = request.getParameterValues(name);
                if (values.length < 2) {
                    usecase.setParameter(name, values[0]);
                } else {
                    usecase.setParameter(name, values);
                }
            } else if (value == null) {
                value = cocoon.request.getParameter(name);
                usecase.setParameter(name, value);
            } else {
                usecase.setPart(name, value);
            }
        }
    }
    cocoon.releaseComponent(flowHelper);
}

/**
 * Load the custom flow functions as provided in the view 
 * configuration, if any.
 *
 * @param view, a org.apache.lenya.cms.usecase.UsecaseView object
 */
function loadCustomFlow(view) {
    customLoopFlow = undefined;
    customSubmitFlow = undefined;
    
    var flowUri;
    if (view != null) {
        flowUri = view.getCustomFlow();
        if (flowUri != null && flowUri != "") { // for some reason, flowUri is not correctly cast into a Boolean, so "if (flowUri)" does not work
            log("debug", "customFlow uri: [" + flowUri + "]");
            cocoon.load(flowUri);
        }
    } else {
        log("debug", "Usecase does not define a view.");
    }
}


/**
 * Log messages via cocoon.log.
 *
 * @param level, one of ("debug"|"info"|"warn"|"error")
 * @param message, a string
 * @param usecaseName, a string.
 */
function log(level, message, usecaseName) {
    var msg = "usecases.js::executeUsecase() "
        + (usecaseName ? "with lenya.usecase=[" + usecaseName + "]" : "")
        + ": " 
        + message;
    switch (level) {
        case "debug":
            if (cocoon.log.isDebugEnabled())
                cocoon.log.debug(msg);
            break;
        case "info":
            cocoon.log.info(msg);
            break;
       case "warn":
            cocoon.log.warn(msg);
            break;
        case "error":
            cocoon.log.error(msg);
            break;
        default:
            cocoon.log.error(msg + "[Unknown log level " + level + "]"); 
            break;
    }
}

/**
 * The Loop stage of the flow, in which a view is displayed. 
 * 
 * @param view, a org.apache.lenya.cms.usecase.UsecaseView object
 * @param usecase, a org.apache.lenya.cms.usecase.Usecase object
 * @param generic, a generic Javascript object for custom flow code to preserve state information (currently not used by the default code)
 * @return A WebContinuation object or null if no continuation was created.
 *
 * This function invokes customLoopFlow if it exists.
 * Otherwise it falls back to defaultLoopFlow.
 */
function loopFlow(view, usecase, generic) {
    if (customLoopFlow != undefined) {
        log("info", "Using customLoopFlow function", usecase.getName());
        return customLoopFlow(view, usecase, generic);
    } else{
        return defaultLoopFlow(view, usecase);
    }
}


/**
 * The Submit stage of the flow, in which a user interaction is processed.
 * and the usecase is advanced. If the user has submitted, the usecase is executed.
 * 
 * @param usecase, a org.apache.lenya.cms.usecase.Usecase Avalon component
 * @param generic, a generic Javascript object for custom flow code to preserve state information (currently not used by the default code)
 * @return a string with the return state ("success"|"cancel"|"continue").
 *
 * This function invokes customSubmitFlow if it exists.
 * Otherwise it falls back to defaultSubmitFlow.
 */
function submitFlow(usecase, generic) {
    if (customSubmitFlow != null) {
        log("info", "Using customSubmitFlow function", usecase.getName());
        return customSubmitFlow(usecase, generic);
    } else{

        return defaultSubmitFlow(usecase);
    }
}

/**
 * @see loopFlow.
 */
function defaultLoopFlow(view, usecase) {
    var viewUri = view.getViewURI();
    // we used to allow a cocoon:/ and cocoon:// prefix (which sendPageXXX does not handle),
    // but it is now deprecated!
    if (viewUri.startsWith("cocoon:/")) {
        // leave leading / in case of cocoon:// for root sitemap
        viewUri = viewUri.substring(new Packages.java.lang.String("cocoon:/").length());
        log("warn", "The use of the cocoon:/ protocol prefix in the <view uri=\"...\"> attribute is deprecated!");
    }
    if (! viewUri.startsWith("/")) {
        // a local URI must be handled by usecase.xmap, which assumes a prefix "usecases-view/[menu|nomenu]/
        // that determines whether the menu is to be displayed. this mechanism is used by most lenya core usecases.
        //viewUri = "usecases-view/" + (view.showMenu() ? "menu" : "nomenu");
    	viewUri = "lenya/modules/usecase/usecases-view/" + (view.showMenu() ? "menu" : "nomenu");
    	
    }
    if (view.createContinuation()) {
        log("debug", "Creating view and continuation, calling Cocoon with viewUri = [" + viewUri + "]");
        return cocoon.sendPageAndWait(viewUri, { "usecase" : usecase });
    } else {
        log("debug", "Creating view without continuation (!), calling Cocoon with viewUri = [" + viewUri + "]");
        cocoon.sendPage(viewUri, { "usecase" : usecase});
        cocoon.exit(); // we're done.
        return null;
    }
}

/**
 * @see submitFlow
 */
function defaultSubmitFlow(usecase) {
    var preconditionsOk = true;
    if (cocoon.request.getParameter("submit")||cocoon.request.getParameter("lenya.submit")=="ok") {
        if (usecase.getTransactionPolicy().equals(Usecase.TRANSACTION_POLICY_OPTIMISTIC)) {
            usecase.checkPreconditions();
            preconditionsOk = !usecase.hasErrors();
        }
        if (preconditionsOk) {
            if (usecase.getTransactionPolicy().equals(Usecase.TRANSACTION_POLICY_OPTIMISTIC)) {
                usecase.lockInvolvedObjects();
            }
            usecase.checkExecutionConditions();
            if (! usecase.hasErrors()) {
                return executeFlow(usecase);
            }
        }
    } else if (cocoon.request.getParameter("cancel")) {
        usecase.cancel();
        return "cancel";
    }
    else {
        usecase.advance();
    }
    return "continue"
}

/**
 * The Execute stage of the flow, in which the usecase is finally executed.
 *
 * @param a org.apache.lenya.cms.usecase.Usecase object
 * @return a string with the return state ("success"|"continue").
 */
function executeFlow(usecase) {
    usecase.execute();
    if (! usecase.hasErrors()) {
        usecase.checkPostconditions();
        if (! usecase.hasErrors()) {
            return "success";
        }
    }
    return "continue";
}

/**
 * Redirect to target URL after finishing the usecase, 
 * taking proxy settings into account.
 *
 * @param the webapp-internal target URL
 */
function redirect(targetUrl) {
    var flowHelper;  // needed to obtain the current objectModel 
    var objectModel; // needed to provide context to the proxyModule 
    var inputModuleSelector; // needed to obtain a proxyModule
    var proxyModule; // used to rewrite the targetUrl according to the publication's proxy settings
    var proxyUrl;    // the rewritten target Url

    flowHelper = cocoon.getComponent("org.apache.lenya.cms.cocoon.flow.FlowHelper");
    try {
        objectModel = flowHelper.getObjectModel(cocoon);
    } finally {
        cocoon.releaseComponent(flowHelper);
    }

    inputModuleSelector = cocoon.getComponent(org.apache.cocoon.components.modules.input.InputModule.ROLE + "Selector");
    try {
        proxyModule = inputModuleSelector.select("proxy");
        try {
            proxyUrl = proxyModule.getAttribute(targetUrl, null, objectModel);
        } finally {
            cocoon.releaseComponent(proxyModule);
        }
    } finally {
        cocoon.releaseComponent(inputModuleSelector);
    }

    log("debug", "Redirecting to {proxy:" + targetUrl + "} = " + proxyUrl + ".");

    cocoon.redirectTo(proxyUrl, true);
}

/**
 * Main function to execute a usecase. This is called from <map:flow/>.
 *
 * Uses request parameter "lenya.usecase" to determine what
 * usecase to execute.
 * 
 */
function executeUsecase() {

    var usecaseName;
    var usecase; // the Usecase object
    var view; // the UsecaseView object that belongs to our usecase.
    var state; // the state of the usecase ("continue"|"success"|"cancel");
    var targetUrl; // URL to redirect to after completion.
    var generic = new Object; // a generic helper object for custom flow code to preserve state information.
    
    var preconditionsOK;

    log("error", "==================================== IN EXecute usecase =============================================");

    
        usecaseName = cocoon.parameters["usecaseName"];
        
        log("error", "usecasename = " + usecaseName);
        
        usecase = getUsecase(usecaseName);
        
        log("error", "get usecase ok");
        
        passRequestParameters(usecase);
        
        log("error", "requestParamaters ok");
        
        usecase.checkPreconditions();
        
        log("error", "checkPreconditions");
        
        preconditionsOK = !usecase.hasErrors();
        
        log("error", "No Errors ? " + preconditionsOK);
        
        if (preconditionsOK && !usecase.getTransactionPolicy().equals(Usecase.TRANSACTION_POLICY_OPTIMISTIC)) {
            usecase.lockInvolvedObjects();
        }
        
        log("error", "get the view");
        
        view = usecase.getView();
        if (view) {
            usecase.setupView();
        }
        
        log("error", "view is OK");
        
        log("debug", "Successfully prepared usecase.", usecaseName);
    try {
    } catch (exception) {
        log("error", "Could not prepare usecase: " + exception, usecaseName);
        throw exception;
    } finally {
        releaseUsecase(usecase);
    }
    loadCustomFlow(view);
    
    // If the usecase has a view uri, this means we want to display something 
    // to the user before proceeding. This also means the usecase can consist
    // of several steps; repeated until the user chooses to submit or cancel.
	log("error", "new test %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
	log("error", "valeur de view" + view.getViewURI());
	log("error", "new test %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
    if (view != null && view.getViewURI()) {
        var continuation = null;
        do {
            // show the view:
                continuation = loopFlow(view, usecase, generic); //usecase must be released here!
            try {
            } catch (exception) {
                // if something went wrong, try and rollback the usecase:
                log("error", "Exception during loopFlow(): " + exception, usecaseName);
                try {
                    usecase = getUsecase(usecaseName);
                    usecase.cancel();
                    throw exception;
                } finally {
                    releaseUsecase(usecase);
                }
            }
            if (preconditionsOK) {
                log("debug", "Advancing in usecase.", usecaseName);
                // restore the usecase state and handle the user input:
                usecase = getUsecase(usecaseName);
                passRequestParameters(usecase);
                state = submitFlow(usecase, generic);
            }
        } while (state == "continue");
        if (continuation != null) {
            continuation.invalidate();
        }
    // If the usecase does not have a view uri, we can directly jump to 
    // executeFlow().
    } else {
        usecase = getUsecase(usecaseName);
        passRequestParameters(usecase);
        
        if (usecase.getTransactionPolicy().equals(Usecase.TRANSACTION_POLICY_OPTIMISTIC)) {
            usecase.checkPreconditions();
            preconditionsOK = !usecase.hasErrors();
        }
        
        if (preconditionsOK) {
            if (usecase.getTransactionPolicy().equals(Usecase.TRANSACTION_POLICY_OPTIMISTIC)) {
                usecase.lockInvolvedObjects();
            }
            usecase.checkExecutionConditions();
            var hasErrors = usecase.hasErrors();
            if (!hasErrors) {
                state = executeFlow(usecase);
                hasErrors = usecase.hasErrors();
            }
        }
        releaseUsecase(usecase);
        if (hasErrors) {
            cocoon.sendPage("usecases-view/nomenu/modules/usecase/templates/error.jx", { "usecase" : usecase });
            return;
        }
    }
    //getTargetURL takes a boolean that is true on success:
    targetUrl = usecase.getTargetURL(state == "success");
    log("debug", "Completed, redirecting to url = [" + targetUrl + "]", usecaseName);
    
    log("error", "==================================== FIN execute usecase =============================================");
    
    // jump to the appropriate URL:
    redirect(targetUrl);
}
