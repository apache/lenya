/*
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://cocoon.apache.org/lenya/)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact board@apache.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://cocoon.apache.org/lenya/)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

/**
 * ant task to set the properties of the project dependent of the area
 * @author edith
 */
public class SetAreaProperties extends Task {
	private String area;
	private String resourcedirpropertyname;
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
     * @return String The name of the property for the resource directory.
     */
    public String getResourcedirpropertyname() {
        return resourcedirpropertyname;
    }

    /**
     * @param resourcedirpropertyname The name of the property for the resource directory.
     */
    public void setResourcedirpropertyname(String resourcedirpropertyname) {
        this.resourcedirpropertyname = resourcedirpropertyname;
    }

	/**
	 * Sets the properties dependent of the area for the project
	 * @param area The area.
	 * @param dirpropertyname The name of the property for the content directory.
	 * @param resourcedirpropertyname The name of the property for the resource directory.
	 */
	protected void setNewProperties(String area, String dirpropertyname, String resourcedirpropertyname) {
		Target target = getOwningTarget();
		Project project = target.getProject();

		String dirproperty = project.getProperty(area+".dir");
		project.setProperty(dirpropertyname, dirproperty);

		String resourcedirproperty = project.getProperty(area+".resource.dir");
		project.setProperty(resourcedirpropertyname, resourcedirproperty);
	}

	/** 
	 * @see org.apache.tools.ant.Task#execute()
	 **/
	public void execute() throws BuildException {
		try {
			log("area " + this.getArea());
			log("dirpropertyname " + this.getDirpropertyname());
			log("resourcedirpropertyname " +this.getResourcedirpropertyname());
			setNewProperties(this.getArea(), this.getDirpropertyname(), this.getResourcedirpropertyname());
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}
}
