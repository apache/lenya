
function getAccreditableManager() {
	return accreditableManager;
}

var accreditableManager;
var selector;
var resolver;

function resolve() {
    selector = cocoon.getComponent("org.apache.lenya.cms.ac2.AccessControllerResolverSelector");
	resolver = selector.select(Packages.org.apache.lenya.cms.ac2.AccessControllerResolver.DEFAULT_RESOLVER);
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
