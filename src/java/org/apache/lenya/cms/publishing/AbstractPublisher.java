/*
 * AbstractPublisher.java
 *
 * Created on November 1, 2002, 3:43 PM
 */

package org.wyona.cms.publishing;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.log4j.Category;
import org.wyona.cms.task.AbstractTask;
import org.wyona.cms.task.Task;

/**
 *
 * @author <a href="mailto:andreas.hartmann@wyona.com">Andreas Hartmann</a>
 */
public abstract class AbstractPublisher
    extends AbstractTask
    implements Publisher {

    static Category log = Category.getInstance(AbstractPublisher.class);

    protected AbstractPublisher() {
    }
    
}
