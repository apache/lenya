function uploadDocument() {

    var documentRoot = cocoon.parameters["documentRoot"];

    var cocoonFlowHelper = new Packages.org.apache.lenya.cms.cocoon.flow.FlowHelper();
    var redirectUri = cocoonFlowHelper.getRequestURI(cocoon);
    var servletContextPath = cocoonFlowHelper.getPageEnvelope(cocoon).getPublication().getServletContext();
    //var uploadHelper = new Packages.org.apache.lenya.cms.authoring.UploadHelper(documentRoot + "/authoring/al/140");
    var uploadHelper = new Packages.org.apache.lenya.cms.authoring.UploadHelper(servletContextPath + "/lenya/pubs/default/content");
    
    var uploaded = false;
    while (!uploaded) {
        
        cocoon.sendPageAndWait("form.xml", {
            "error-messages" : null,
            //"error-messages" : uploadHelper.getErrorMessages(),
            "form-title" : "Upload Document"
        });
        
        if (cocoon.request.getParameter("upload-regulation")) {
            var part = cocoon.request.get("upload-regulation");
            uploaded = uploadHelper.save(part);
        }
    }
    
    cocoon.redirectTo(redirectUri);
}
