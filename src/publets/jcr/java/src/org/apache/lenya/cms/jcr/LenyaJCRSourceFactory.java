/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.jcr;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.cocoon.jcr.source.JCRSourceFactory;
import org.apache.lenya.cms.metadata.LenyaMetaData;

public class LenyaJCRSourceFactory extends JCRSourceFactory {

    public Node createFileNode(Node folderNode, String name) throws RepositoryException {
        Node node = super.createFileNode(folderNode, name);
        String prefix = node.getSession().getNamespacePrefix(LenyaMetaData.NAMESPACE);
        if (!prefix.equals("")) {
            prefix = prefix + ":";
        }
        
        node.addMixin(prefix + LenyaRepository.CONTENT_NODE);
        return node;
    }
/*
    public Node createContentNode(Node fileNode) throws RepositoryException {
        Node node = super.createContentNode(fileNode);
        
        String prefix = node.getSession().getNamespacePrefix(LenyaMetaData.NAMESPACE);
        if (!prefix.equals("")) {
            prefix = prefix + ":";
        }
        
        node.addMixin(prefix + LenyaRepository.CONTENT_NODE);
        return node;
    }
*/
}
