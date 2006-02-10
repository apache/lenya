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
package org.apache.lenya.cms.jcr.mapping;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Node;

import org.apache.lenya.cms.jcr.util.Assertion;
import org.apache.lenya.cms.repo.RepositoryException;

/**
 * Node proxy base class.
 */
public abstract class AbstractNodeProxy implements NodeProxy {

    public Node getNode() throws RepositoryException {
        return this.node;
    }
    
    private Node node;
    private RepositoryFacade repo;

    public void setup(RepositoryFacade repo, Node node) throws RepositoryException {
        this.node = node;
        this.repo = repo;
    }

    public RepositoryFacade getRepository() throws RepositoryException {
        return this.repo;
    }

    protected NodeProxy getParentProxy() throws RepositoryException {
        try {
            Node parentNode = getNode().getParent();
            return getRepository().getProxy(parentNode);
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }
    
    protected String getPropertyString(String propertyName) throws RepositoryException {
        try {
            return getNode().getProperty(propertyName).getString();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    protected long getPropertyLong(String propertyName) throws RepositoryException {
        try {
            return getNode().getProperty(propertyName).getLong();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    protected InputStream getPropertyStream(String propertyName) throws RepositoryException {
        try {
            return getNode().getProperty(propertyName).getStream();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    protected Node getPropertyNode(String propertyName) throws RepositoryException {
        try {
            return getNode().getProperty(propertyName).getNode();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    protected boolean getPropertyBoolean(String propertyName) throws RepositoryException {
        try {
            return getNode().getProperty(propertyName).getBoolean();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    protected void setProperty(String propertyName, String value) throws RepositoryException {
        Assertion.notNull(value, "property value");
        try {
            getNode().setProperty(propertyName, value);
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    protected void setProperty(String propertyName, InputStream stream) throws RepositoryException {
        Assertion.notNull(stream, "input stream");
        try {
            getNode().setProperty(propertyName, stream);
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    protected void setProperty(String propertyName, long value) throws RepositoryException {
        try {
            getNode().setProperty(propertyName, value);
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    protected void setProperty(String propertyName, Calendar calendar) throws RepositoryException {
        Assertion.notNull(calendar, "calendar");
        try {
            getNode().setProperty(propertyName, calendar);
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }
    
    protected void setProperty(String propertyName, Node node) throws RepositoryException {
        Assertion.notNull(node, "node");
        try {
            getNode().setProperty(propertyName, node);
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }
    
    protected void setProperty(String propertyName, boolean value) throws RepositoryException {
        try {
            getNode().setProperty(propertyName, value);
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }
    
    protected static String getXPathPredicate(String propertyName, String propertyValue) {
        return "[@" + propertyName + " = '" + propertyValue + "']";
    }
    
    protected static PathElement getPathElement(String nodeName, String propertyName, String propertyValue) {
        return new PropertyPathElement(nodeName, propertyName, propertyValue);
    }

    protected static PathElement getPathElement(String nodeName) {
        return new NamePathElement(nodeName);
    }

    /**
     * @return The node name.
     * @throws RepositoryException if an error occurs.
     */
    public String getName() throws RepositoryException {
        try {
            return getNode().getName();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }
    
    /**
     * @return The path element which identifies this node against its parent.
     * @throws RepositoryException if an error occurs.
     */
    public abstract PathElement getPathElement() throws RepositoryException;
    
    public Path getAbsolutePath() throws RepositoryException {
        NodeProxy parent = getParentProxy();
        if (parent == null) {
            return new Path(getPathElement());
        }
        else {
            return parent.getAbsolutePath().append(getPathElement());
        }
    }

    public String toString() {
        try {
            return getNode().getSession().getWorkspace().getName() + "::" + getAbsolutePath();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        } catch (javax.jcr.RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes the node.
     * @throws RepositoryException if an error occurs.
     */
    public void remove() throws RepositoryException {
        try {
            getNode().remove();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
        save();
    }
    
    protected void save() throws RepositoryException {
        try {
            getRepository().getSession().save();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }
}
