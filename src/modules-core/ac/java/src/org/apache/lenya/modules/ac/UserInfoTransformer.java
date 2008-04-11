/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lenya.modules.ac;

import java.io.IOException;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.ac.PolicyUtil;
import org.apache.lenya.util.ServletHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * <p>
 * Transformer to retrieve user information based on the user ID. The user ID can
 * either be provided using the <em>userId</em> attribute or as text inside the element.
 * </p>
 * <p>Elements:</p>
 * <ul>
 * <li><code>&lt;user:fullname&gt;</code> - The full name. If the user doesn't exist, the user ID is inserted.</li>
 * </ul>
 * <p>
 * Usage examples:
 * </p>
 * <pre><code>
 * &lt;user:fullname&gt;john&lt;/user:fullname&gt;
 * 
 * &lt;user:fullname userId="john"/&gt;
 * </code></pre>
 */
public class UserInfoTransformer extends AbstractSAXTransformer {

    protected static final String ATTR_USER_ID = "userId";
    protected static final String ELEM_FULLNAME = "fullname";
    protected static final String NAMESPACE = "http://apache.org/lenya/userinfo/1.0";
    private String userId;
    private String url;

    public UserInfoTransformer() {
        this.defaultNamespaceURI = NAMESPACE;
    }

    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters params)
            throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, src, params);
        Request request = ObjectModelHelper.getRequest(objectModel);
        this.url = ServletHelper.getWebappURI(request);
        
    }

    public void startTransformingElement(String uri, String name, String raw, Attributes attr)
            throws ProcessingException, IOException, SAXException {
        if (name.equals(ELEM_FULLNAME)) {
            String userId = attr.getValue(ATTR_USER_ID);
            if (userId != null) {
                this.userId = userId;
            } else {
                startTextRecording();
            }
        }
    }

    public void endTransformingElement(String uri, String name, String raw)
            throws ProcessingException, IOException, SAXException {
        if (name.equals(ELEM_FULLNAME)) {
            String userId = this.userId != null ? this.userId : endTextRecording();
            try {
                User user = PolicyUtil.getUser(this.manager, this.url, userId, getLogger());
                String output = user != null ? user.getName() : userId;
                char[] chars = output.toCharArray();
                characters(chars, 0, chars.length);
            } catch (AccessControlException e) {
                throw new ProcessingException(e);
            }
            this.userId = null;
        }
    }

}
