/*
 * Created on 27.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.lenya.cms.workflow.ui;

import org.apache.lenya.cms.workflow.Situation;
import org.apache.lenya.cms.workflow.WorkflowInstance;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface CommandFilter {

    String[] getExecutableCommands(WorkflowInstance instance, Situation situation);

}
