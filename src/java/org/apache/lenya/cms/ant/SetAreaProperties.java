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

/* $Id: SetAreaProperties.java,v 1.3 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

/**
 * ant task to set the properties of the project dependent of the area
 */
public class SetAreaProperties extends Task {
	private String area;
	private String dirpropertyname;
    /**
     * Creates a new instance of SetAreaProperties
     */
    public SetAreaProperties() {
        super();
    }
    /**
     * @return String The area.
     */
    public String getArea() {
        return area;
    }

    /**
     * @param area The area.
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * @return String The name of the property for the content directory.
     */
    public String getDirpropertyname() {
        return dirpropertyname;
    }

    /**
     * @param dirpropertyname The name of the property for the content directory.
     */
    public void setDirpropertyname(String dirpropertyname) {
        this.dirpropertyname = dirpropertyname;
    }

	/**
	 * Sets the properties dependent of the area for the project
	 * @param area The area.
	 * @param dirpropertyname The name of the property for the content directory.
	 */
	protected void setNewProperties(String area, String dirpropertyname) {
		Target target = getOwningTarget();
		Project project = target.getProject();

		String dirproperty = project.getProperty(area+".dir");
		project.setProperty(dirpropertyname, dirproperty);
	}

	/** 
	 * @see org.apache.tools.ant.Task#execute()
	 **/
	public void execute() throws BuildException {
		try {
			log("area " + this.getArea());
			log("name of the property for the directory " + this.getDirpropertyname());
			setNewProperties(this.getArea(), this.getDirpropertyname());
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}
}
