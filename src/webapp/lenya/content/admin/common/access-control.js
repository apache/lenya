
function getAccreditableManager() {
	return accreditableManager;
}

var accreditableManager;
var selector;
var resolver;
var requestUrl;
var contextPath;

function resolve() {
    selector = cocoon.getComponent("org.apache.lenya.ac.AccessControllerResolverSelector");
	resolver = selector.select(Packages.org.apache.lenya.ac.AccessControllerResolver.DEFAULT_RESOLVER);
	if (!requestUrl) {
		requestUrl = cocoon.parameters["requestUri"];
	}
	if (!contextPath) {
		contextPath = cocoon.parameters["contextPath"];
	}
	var webappUrl = Packages.org.apache.lenya.util.ServletHelper.getWebappURI(contextPath, requestUrl);
	var accessController = resolver.resolveAccessController(webappUrl);
	accreditableManager = accessController.getAccreditableManager();
}

function release() {
	cocoon.releaseComponent(resolver);
	cocoon.releaseComponent(selector);
}
