function customLoopFlow(view,proxy,generic) {
   // load some helper functions
   // cocoon.load("fallback://lenya/modules/cforms/flow/lenyadoc-utils.js");
    try {
        var formDef = "fallback://lenya/modules/cforms/usecases/dynamicrepeater.xml";
        var formBind = "fallback://lenya/modules/cforms/usecases/dynamicrepeater_binding.xml";
        var formView = "usecases-view/menu/modules/cforms/usecases/dynamicrepeater_template.xml";
        generic.form = new Form(formDef);
        generic.form.setAttribute("counter", new java.lang.Integer(0));
        generic.form.createBinding(formBind);
        
        try {
                var parser = cocoon.getComponent(Packages.org.apache.excalibur.xml.dom.DOMParser.ROLE);
                var resolver = cocoon.getComponent(Packages.org.apache.cocoon.environment.SourceResolver.ROLE);
                var source = resolver.resolveURI(proxy.getParameter('sourceUri'));
                var is = new Packages.org.xml.sax.InputSource(source.getInputStream());
                is.setSystemId(source.getURI());
                generic.doc = parser.parseDocument(is);
        } finally {
                if (source != null)
                resolver.release(source);
                cocoon.releaseComponent(parser);
                cocoon.releaseComponent(resolver);
        }
        
        generic.form.load(generic.doc);
        generic.form.showForm(formView, {"usecase" : proxy});
    } catch (exception) {
        // if an exception was thrown by the view, allow the usecase to rollback the transition
        log("error", "Exception during customLoopFlow: " + exception);
        throw exception;
    }
}

function customSubmitFlow(usecase, generic) {
    generic.form.save(generic.doc);
    usecase.setParameter("xml", generic.doc);
    defaultSubmitFlow(usecase);
}
