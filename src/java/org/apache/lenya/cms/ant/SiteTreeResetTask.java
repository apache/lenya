/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id: SiteTreeResetTask.java,v 1.2 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to reset the sitetree xml file
 */
public class SiteTreeResetTask extends PublicationTask {
	private String area;

	/**
	 * 
	 */
	public SiteTreeResetTask() {
		super();
	}

	/**
	 * @return string The area the sitetree belongs to. 
	 */
	public String getArea() {
		return area;
	}

	/**
	 * @param string The area the sitetree belongs to.
	 */
	public void setArea(String string) {
		area = string;
	}

    /** (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException {
		try {
			log("area : " + this.getArea());
			Publication publication= getPublication();
			SiteTree tree = publication.getSiteTree(getArea());
           
			SiteTreeNode node = tree.getNode("/");
            node.removeChildren(); 			
			tree.save();
			} catch (
				Exception e) {
			throw new BuildException(e);
		}
	}

}
