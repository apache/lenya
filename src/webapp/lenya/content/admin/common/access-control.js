/*
* Copyright 1999-2004 The Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

function getRequestUri() {
	var flowHelper = new Packages.org.apache.lenya.cms.cocoon.flow.FlowHelper();
	var requestUri = flowHelper.getRequestURI(cocoon);
	return requestUri;
}

function getAccreditableId() {
	var flowHelper = new Packages.org.apache.lenya.cms.cocoon.flow.FlowHelper();
	var requestUri = flowHelper.getRequestURI(cocoon);
	requestUri = requestUri.substring(0, requestUri.length() - 5);
	var lastSlashIndex = requestUri.lastIndexOf("/");
	var id = requestUri.substring(lastSlashIndex + 1);
	return id;
}

function getAccreditableManager() {
	return accreditableManager;
}

var accreditableManager;
var selector;
var resolver;

function resolve() {

    selector = cocoon.getComponent(Packages.org.apache.lenya.ac.AccessControllerResolver.ROLE + "Selector");
	resolver = selector.select(Packages.org.apache.lenya.ac.AccessControllerResolver.DEFAULT_RESOLVER);
	var requestUrl = cocoon.parameters["requestUri"];
	var contextPath = cocoon.parameters["contextPath"];
	var webappUrl = Packages.org.apache.lenya.util.ServletHelper.getWebappURI(contextPath, requestUrl);
	var accessController = resolver.resolveAccessController(webappUrl);
	accreditableManager = accessController.getAccreditableManager();
}

function release() {
	cocoon.releaseComponent(resolver);
	cocoon.releaseComponent(selector);
}
