/*
$Id: MoveSiteTreeNodeTask.java,v 1.2 2003/07/23 13:21:45 gregor Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.task;

import java.io.File;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publishing.PublishingEnvironment;
import org.apache.log4j.Category;

/**
 * Task to move a node amongst the siblings
 * @author edith
 */
public class MoveSiteTreeNodeTask extends AbstractTask {
	private static Category log = Category.getInstance(MoveSiteTreeNodeTask.class);

	public static final String PUBLICATION_DIRECTORY = "pub.dir";
	public static final String PUBLICATION_ID = "pub.id";
	public static final String SERVLET_CONTEXT_PATH = "servlet.context";
	public static final String TREE_PATH= "treePath";
	public static final String DOCUMENT_ID= "org.apache.lenya.cms.info.documentid";
	public static final String DIRECTION = "org.apache.lenya.cms.info.direction";

	/** (non-Javadoc)
	 * @see org.apache.lenya.cms.task.Task#execute(java.lang.String)
	 **/
	public void execute(String servletContextPath) throws ExecutionException {
	File publicationDirectory;
	String publicationId;
	String documentid = null;
	String direction = null;
    String treePath;
    String absolutetreepath = null;
	String parentid = null;
	String id = null;

	try {
		documentid=getParameters().getParameter(DOCUMENT_ID, null);
		direction=getParameters().getParameter(DIRECTION, null);
		publicationId = getParameters().getParameter(PARAMETER_PUBLICATION_ID);
		treePath = getParameters().getParameter(TREE_PATH);
		log.debug("documentid: " + documentid);
		log.debug("treePath : " + treePath);
		log.debug("move : " + direction);
		log.debug("publicationId: " + publicationId);
	} catch (ParameterException e) {
		throw new ExecutionException(e);
	}

	PublishingEnvironment environment = new PublishingEnvironment(servletContextPath,
				publicationId);
	publicationDirectory = environment.getPublicationDirectory();
	File absolutetree= new File(publicationDirectory,treePath);
       absolutetreepath = absolutetree.getAbsolutePath();
	log.debug("absolutetreepath: " + absolutetreepath);

	DefaultSiteTree tree = null;
	try {
		tree = new DefaultSiteTree(absolutetreepath);
        if (direction.equals("up")) {
			tree.moveUp(documentid);
        } else if (direction.equals("down")) {
			tree.moveDown(documentid);
        } else {
			throw new ExecutionException("The direction in which the node should" +				"be moved isn¨t specified");
        }
		tree.save();
	} catch (Exception e) {
 	    throw new ExecutionException(e);
	}
}

}