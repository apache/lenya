
function getAccessController() {
    var publication = Packages.org.apache.lenya.cms.publication.PublicationFactory
                          .getPublication(cocoon.request, cocoon.context);
    var configDir = new java.io.File(publication.getDirectory(),
                       "config" + java.io.File.separator + "ac");
    var accessController = new Packages.org.apache.lenya.cms.ac2.file.FileAccessController(configDir);
    return accessController;
}

