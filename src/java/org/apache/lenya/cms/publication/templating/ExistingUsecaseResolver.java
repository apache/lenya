/*
 * Created on 12.08.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.publication.templating;

import java.io.File;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.lenya.cms.publication.Publication;

/**
 * Publication visitor which returns the first publication implementing a certain usecase.
 */
public class ExistingUsecaseResolver implements PublicationVisitor {

    private String usecase;
    private Publication publication;

    /**
     * Ctor.
     * @param usecase The name of the usecase to resolve.
     */
    public ExistingUsecaseResolver(String usecase) {
        this.usecase = usecase;
    }

    protected static final String ELEMENT_USECASES = "usecases";
    protected static final String ELEMENT_USECASE = "usecase";
    protected static final String ATTRIBUTE_NAME = "name";

    /**
     * @see org.apache.lenya.cms.publication.templating.PublicationVisitor#visit(org.apache.lenya.cms.publication.Publication)
     */
    public void visit(Publication publication) {

        if (this.publication == null) {
            File configFile = new File(publication.getDirectory(), Publication.CONFIGURATION_FILE);
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();

            try {
                Configuration config = builder.buildFromFile(configFile);
                Configuration usecasesConfig = config.getChild(ELEMENT_USECASES);
                if (usecasesConfig != null) {
                    Configuration[] usecaseConfigs = usecasesConfig.getChildren(ELEMENT_USECASE);
                    for (int i = 0; i < usecaseConfigs.length; i++) {
                        String usecaseName = usecaseConfigs[i].getAttribute(ATTRIBUTE_NAME);
                        if (usecaseName.equals(this.usecase)) {
                            this.publication = publication;
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Resolving usecases failed: ", e);
            }
        }
    }

    /**
     * Returns the resolved publication.
     * @return A publication or <code>null</code> if no publication contains the usecase.
     */
    public Publication getPublication() {
        return this.publication;
    }

}