/*
 * AbstractExporter.java
 *
 * Created on November 4, 2002, 6:14 PM
 */

package org.wyona.cms.publishing;

import java.net.URL;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.util.HashMap;
import org.apache.log4j.Category;
import org.wyona.cms.task.AbstractTask;
import org.wyona.cms.task.Task;

/**
 *
 * @author  ah
 */
public abstract class AbstractExporter
    extends AbstractTask
    implements Exporter {

    static Category log = Category.getInstance(AbstractExporter.class);

    protected AbstractExporter() {
    }
    
}
