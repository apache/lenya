/*
 * Created on 22.07.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.usecase;

import java.util.List;

import org.apache.cocoon.servlet.multipart.Part;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.workflow.Situation;

/**
 * @author nobby
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface Usecase {

    /**
     * The <code>Usecase</code> role.
     */
    String ROLE = Usecase.class.getName();
    
    /**
     * Sets a parameter from the form.
     * @param name The parameter name.
     * @param value The parameter value.
     */
    void setParameter(String name, String value);
    
    /**
     * Returns the current value of a parameter.
     * @param name The parameter name.
     * @return A string.
     */
    String getParameter(String name);
    
    /**
     * Sets a parameter from the form. This method is called for parts in multipart requests.
     * @param name The parameter name.
     * @param value The parameter value.
     */
    void setPart(String name, Part value);
    
    /**
     * Passes the source document and the workflow situation to the usecase.
     * @param sourceDocument The document the workflow was invoked on.
     * @param situation The workflow situation.
     * 
     */
    void setup(Document sourceDocument, Situation situation);
    
    /**
     * Checks the conditions before a form is displayed.
     * @throws UsecaseException if an error occurs that causes an unstable system.
     */
    void checkPreconditions() throws UsecaseException;
    
    /**
     * Checks the conditions after the usecase was executed.
     * @throws UsecaseException if an error occurs that causes an unstable system.
     */
    void checkPostconditions() throws UsecaseException;
    
    /**
     * Checks the conditions right before the operation is executed.
     * @throws UsecaseException if an error occurs that causes an unstable system.
     */
    void checkExecutionConditions() throws UsecaseException;
    
    /**
     * Returns the error messages from the previous operation. Error messages
     * prevent the operation from being executed.
     * @return A list of strings.
     */
    List getErrorMessages();
    
    /**
     * Returns the info messages from the previous operation. Info messages
     * do not prevent the operation from being executed.
     * @return A list of strings.
     */
    List getInfoMessages();

    /**
     * Executes the usecase. During this method error and info messages are
     * filled in. If getErrorMessages() returns an empty array, the operation
     * succeeded. Otherwise, the operation failed.
     * @throws UsecaseException if an error occured that causes an unstable system.
     */
    void execute() throws UsecaseException;
    
    /**
     * Returns the document which should be shown after the usecase is completed.
     * @param success If the usecase was completed successfully.
     * @return A document.
     */
    Document getTargetDocument(boolean success);
    
}
