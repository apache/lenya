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

/* $Id$  */

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
        return this.area;
    }

    /**
     * @param _area The area.
     */
    public void setArea(String _area) {
        this.area = _area;
    }

    /**
     * @return String The name of the property for the content directory.
     */
    public String getDirpropertyname() {
        return this.dirpropertyname;
    }

    /**
     * @param _dirpropertyname The name of the property for the content directory.
     */
    public void setDirpropertyname(String _dirpropertyname) {
        this.dirpropertyname = _dirpropertyname;
    }

	/**
	 * Sets the properties dependent of the area for the project
	 * @param _area The area.
	 * @param _dirpropertyname The name of the property for the content directory.
	 */
	protected void setNewProperties(String _area, String _dirpropertyname) {
		Target _target = getOwningTarget();
		Project _project = _target.getProject();

		String dirproperty = _project.getProperty(_area+".dir");
		_project.setProperty(_dirpropertyname, dirproperty);
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
