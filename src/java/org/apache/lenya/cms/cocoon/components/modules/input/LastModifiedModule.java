package org.apache.lenya.cms.cocoon.components.modules.input;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;

public class LastModifiedModule extends AbstractInputModule implements Serviceable {

    private ServiceManager manager;
    protected static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    @Override
    public Object getAttribute(String name, Configuration modeConf,
            @SuppressWarnings("rawtypes") Map objectModel) throws ConfigurationException {

        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            Source source = null;
            try {
                source = resolver.resolveURI(name);
                return format.format(new Date(source.getLastModified()));
            } finally {
                if (source != null) {
                    resolver.release(source);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
    }

    @Override
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
