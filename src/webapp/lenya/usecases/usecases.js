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

//placeholders for custom flow code:
var customLoopFlow = undefined;  
var customSubmitFlow = undefined;

/**
 * Get the current usecase.
 *
 * @param usecaseName, a string
 * @return a new org.apache.lenya.cms.usecase.Usecase Avalon component
 */
function getUsecase(usecaseName) {
    var flowHelper;
    var request;
    var sourceUrl;
    var usecaseResolver;
    var usecase;
    try {
        flowHelper = cocoon.getComponent("org.apache.lenya.cms.cocoon.flow.FlowHelper");
        request = flowHelper.getRequest(cocoon);
        sourceUrl = Packages.org.apache.lenya.util.ServletHelper.getWebappURI(request);
        usecaseResolver = cocoon.getComponent("org.apache.lenya.cms.usecase.UsecaseResolver");
        usecase = usecaseResolver.resolve(sourceUrl, usecaseName);
        usecase.setSourceURL(sourceUrl);
        usecase.setName(usecaseName);
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
 * Release a usecase. Since usecases are Avalon Components, they must
 * be released before a continuation is created.
 *
 * @param usecase, a org.apache.lenya.cms.usecase.Usecase Avalon component
 */
function releaseUsecase(usecase) {
    var usecaseResolver = cocoon.getComponent("org.apache.lenya.cms.usecase.UsecaseResolver");
    try {
        usecaseResolver.release(usecase);
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
                var values = flowHelper.getRequest(cocoon).getParameterValues(name);
                if (values.length < 2) {
                    usecase.setParameter(name, values[0]);
                } else {
                    usecase.setParameter(name, values);
                }
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
 * <em>Note:</em> All Avalon components should be released before calling
 * this function! This means that you cannot hold a usecase object,
 * hence the proxy.
 * 
 * @param view, a org.apache.lenya.cms.usecase.UsecaseView object
 * @param proxy, a org.apache.lenya.cms.usecase.UsecaseProxy object
 * @param generic, a generic Javascript object for custom flow code to preserve state information (currently not used by the default code)
 *
 * This function invokes customLoopFlow if it exists.
 * Otherwise it falls back to defaultLoopFlow.
 */
function loopFlow(view, proxy, generic) {
    if (customLoopFlow != undefined) {
        log("info", "Using customLoopFlow function", proxy.getName());
        return customLoopFlow(view, proxy, generic);
    } else{
        return defaultLoopFlow(view, proxy);
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
function defaultLoopFlow(view, proxy) {
    var viewUri = view.getViewURI();
    // we used to allow a cocoon:/ prefix (which sendPageXXX does not handle),
    // but it is now deprecated!
    if (viewUri.startsWith("cocoon:/")) {
        viewUri = viewUri.substring(new Packages.java.lang.String("cocoon:/").length());
        log("warn", "The use of the cocoon:/ protocol prefix in the <view uri=\"...\"> attribute is deprecated!");
    }
    if (! viewUri.startsWith("/")) {
        // a local URI must be handled by usecase.xmap, which assumes a prefix "usecases-view/[menu|nomenu]/
        // that determines whether the menu is to be displayed. this mechanism is used by most lenya core usecases.
        viewUri = "usecases-view/" 
            + (view.showMenu() ? "menu" : "nomenu")
            + "/" + viewUri;
    }
    if (view.createContinuation()) {
        log("debug", "Creating view and continuation, calling Cocoon with viewUri = [" + viewUri + "]");
        cocoon.sendPageAndWait(viewUri, { "usecase" : proxy });
    } else {
        log("debug", "Creating view without continuation (!), calling Cocoon with viewUri = [" + viewUri + "]");
        cocoon.sendPage(viewUri, { "usecase" : proxy});
        cocoon.exit(); // we're done.
    }
}

/**
 * @see submitFlow
 */
function defaultSubmitFlow(usecase) {
    usecase.advance();
    if (cocoon.request.getParameter("submit")||cocoon.request.getParameter("lenya.submit")=="ok") {
        usecase.checkExecutionConditions();
        if (! usecase.hasErrors()) {
            return executeFlow(usecase);
        }
    } else if (cocoon.request.getParameter("cancel")) {
        usecase.cancel();
        return "cancel";
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
 * Redirect to target URL after finishing the usecase.
 *
 * @param the target URL
 */
function redirect(targetUrl) {
    var flowHelper = cocoon.getComponent("org.apache.lenya.cms.cocoon.flow.FlowHelper");
    var contextPath = flowHelper.getRequest(cocoon).getContextPath();
    cocoon.releaseComponent(flowHelper);
    cocoon.redirectTo(contextPath + targetUrl, true);
}



/**
 * Main function to execute a usecase. This is called from <map:flow/>.
 *
 * Uses request parameter "lenya.usecase" to determine what
 * usecase to execute.
 * 
 * Since "usecase" and "flowHelper" are avalon components, 
 * they must be released before a continuation is created.
 * In order to preserve state information, a "proxy" object
 * is used.
 */
function executeUsecase() {

    var usecaseName;
    var usecase; // the Usecase object
    var proxy; // a UsecaseProxy to make the usecase state persistent across continuations
    var view; // the UsecaseView object that belongs to our usecase.
    var state; // the state of the usecase ("continue"|"success"|"cancel");
    var targetUrl; // URL to redirect to after completion.
    var generic = new Object; // a generic helper object for custom flow code to preserve state information.

    try {
        usecaseName = cocoon.parameters["usecaseName"];
        usecase = getUsecase(usecaseName);
        passRequestParameters(usecase);
        usecase.checkPreconditions();
        usecase.lockInvolvedObjects();
        // create proxy object to save usecase state
        proxy = new Packages.org.apache.lenya.cms.usecase.UsecaseProxy(usecase);
        view = usecase.getView();
        log("debug", "Successfully prepared usecase.", usecaseName);
    } catch (exception) {
        log("error", "Could not prepare usecase: " + exception, usecaseName);
        throw exception;
    } finally {
        releaseUsecase(usecase);
    }
    loadCustomFlow(view);
    if (view != null && view.getViewURI()) {
        // If the usecase has a view uri, this means we want to display something 
        // to the user before proceeding. This also means the usecase can consist
        // of several steps; repeated until the user chooses to submit or cancel.
        do {
            // show the view:
            try {
                loopFlow(view, proxy, generic); //usecase must be released here!
            } catch (exception) {
                // if something went wrong, try and rollback the usecase:
                log("error", "Exception during loopFlow(): " + exception, usecaseName);
                try {
                    usecase = getUsecase(usecaseName);
                    proxy.setup(usecase);
                    usecase.cancel();
                    throw exception;
                } finally {
                    releaseUsecase(usecase);
                }
            }
            log("debug", "Advancing in usecase.", usecaseName);
            // restore the usecase state and handle the user input:
            usecase = getUsecase(usecaseName);
            proxy.setup(usecase);
            passRequestParameters(usecase);
            state = submitFlow(usecase, generic);
            // create a new proxy with the updated usecase state
            proxy = new Packages.org.apache.lenya.cms.usecase.UsecaseProxy(usecase);
            releaseUsecase(usecase);
        } while (state == "continue");
        // If the usecase does not have a view uri, we can directly jump to 
        // executeFlow().
    } else {
        usecase = getUsecase(usecaseName);
        proxy.setup(usecase);
        passRequestParameters(usecase);
        state = executeFlow(usecase);
        releaseUsecase(usecase);
    }
    //getTargetURL takes a boolean that is true on success:
    targetUrl = usecase.getTargetURL(state == "success");
    log("debug", "Completed, redirecting to url = [context:/" + targetUrl + "]", usecaseName);
    // jump to the appropriate URL:
    redirect(targetUrl);
}
