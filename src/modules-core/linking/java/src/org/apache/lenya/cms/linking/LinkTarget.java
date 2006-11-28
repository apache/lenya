/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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
package org.apache.lenya.cms.linking;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.util.Assert;

/**
 * The target of a link.
 */
public class LinkTarget {

    private Document doc;
    private int revisionNumber = -1;

    /**
     * Ctor.
     * @param doc The document.
     * @param revisionNumber The revision number.
     */
    protected LinkTarget(Document doc, int revisionNumber) {
        this(doc);
        this.revisionNumber = revisionNumber;
    }

    /**
     * Ctor.
     * @param doc The document.
     */
    protected LinkTarget(Document doc) {
        Assert.notNull("document", doc);
        this.doc = doc;
    }

    /**
     * Ctor for non-existing targets.
     */
    protected LinkTarget() {
    }

    /**
     * @return The linked document.
     * @throws LinkException if the target doesn't exist.
     */
    public Document getDocument() throws LinkException {
        if (!exists()) {
            throw new LinkException("The target doesn't exist!");
        }
        return this.doc;
    }

    /**
     * @return The revision number.
     * @throws LinkException if the target doesn't exist or no revision number
     *         is specified in the link.
     */
    public int getRevisionNumber() throws LinkException {
        if (!exists()) {
            throw new LinkException("The target doesn't exist!");
        }
        if (this.revisionNumber == -1) {
            throw new LinkException("No revision specified!");
        }
        return this.revisionNumber;
    }

    /**
     * @return if the revision is specified in the link.
     */
    public boolean isRevisionSpecified() {
        return this.revisionNumber != -1;
    }

    /**
     * @return if the target exists.
     */
    public boolean exists() {
        return this.doc != null;
    }

}
