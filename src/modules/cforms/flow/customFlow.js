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
    var flowHelper = cocoon.getComponent("org.apache.lenya.cms.cocoon.flow.FlowHelper");
//    try {
        flowHelper.triggerWorkflow(cocoon, 'edit');
        //saveDocument(generic.doc, usecase.getSourceURL());
//        try {
            var resolver = cocoon.getComponent(Packages.org.apache.cocoon.environment.SourceResolver.ROLE);
            var source = resolver.resolveURI(usecase.getSourceURL());
            var tf = Packages.javax.xml.transform.TransformerFactory.newInstance();
            log("debug", "source instanceof ModifiableSource ? " + (source instanceof Packages.org.apache.excalibur.source.ModifiableSource));
            log("debug", "tf.getFeature(SAXTransformerFactory.FEATURE): " + tf.getFeature(Packages.javax.xml.transform.sax.SAXTransformerFactory.FEATURE));
            if (source instanceof Packages.org.apache.excalibur.source.ModifiableSource
                    && tf.getFeature(Packages.javax.xml.transform.sax.SAXTransformerFactory.FEATURE)) {
                var outputStream = source.getOutputStream();
                var transformerHandler = tf.newTransformerHandler();
                var transformer = transformerHandler.getTransformer();
                transformer.setOutputProperty(Packages.javax.xml.transform.OutputKeys.INDENT, "true");
                transformer.setOutputProperty(Packages.javax.xml.transform.OutputKeys.METHOD, "xml");
                transformerHandler.setResult(new Packages.javax.xml.transform.stream.StreamResult(outputStream));

                var streamer = new Packages.org.apache.cocoon.xml.dom.DOMStreamer(transformerHandler);
                streamer.stream(generic.doc);
            } else {
                throw new Packages.org.apache.cocoon.ProcessingException("Cannot write to source " + usecase.getSourceURL());
            }
/*        } catch (exception) {
            log("error", "Something went wrong during saving: " + exception, usecase.getName());
            log("debug", "usecase.getSourceURL(): " + usecase.getSourceURL(), usecase.getName());
            log("debug", "source: " + source, usecase.getName());
            log("debug", "tf: " + tf, usecase.getName());
            log("debug", "generic.doc: " + generic.doc, usecase.getName());
            log("debug", "outputStream: " + outputStream, usecase.getName());
            //log("debug", "streamer.stream(generic.doc): " + streamer.stream(generic.doc), usecase.getName());
            throw exception;
        } finally {
            if (source != null)
            resolver.release(source);
            cocoon.releaseComponent(resolver);
            if (outputStream != null) {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (exception) {
                log("error", "Could not flush/close outputstream: " + exception, usecase.getName());
                throw exception;
            }
            }
        }

    } catch (exception) {
        log("error", "Exception during customSubmitFlow: " + exception, usecase.getName());
        throw exception;
    } finally {
        cocoon.releaseComponent(flowHelper);
    }
*/
    return "success";
}
