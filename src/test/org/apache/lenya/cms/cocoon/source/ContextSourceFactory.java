package org.apache.lenya.cms.cocoon.source;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.cocoon.environment.mock.MockContext;

public class ContextSourceFactory extends org.apache.cocoon.components.source.impl.ContextSourceFactory {

    public void contextualize(Context context) throws ContextException {
        this.envContext = new MockContext();
    }

}
