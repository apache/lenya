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
package org.apache.shibboleth.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.shibboleth.AttributeRequestService;
import org.apache.shibboleth.ShibbolethModule;
import org.apache.shibboleth.saml.ShibbolethBinding;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLAttribute;
import org.opensaml.SAMLAttributeQuery;
import org.opensaml.SAMLAttributeStatement;
import org.opensaml.SAMLAuthenticationStatement;
import org.opensaml.SAMLException;
import org.opensaml.SAMLRequest;
import org.opensaml.SAMLResponse;
import org.opensaml.SAMLStatement;
import org.opensaml.SAMLSubject;
import org.opensaml.XML;
import org.opensaml.SAMLBrowserProfile.BrowserProfileResponse;

import edu.internet2.middleware.shibboleth.aap.AAP;
import edu.internet2.middleware.shibboleth.aap.AttributeRule;
import edu.internet2.middleware.shibboleth.metadata.AttributeAuthorityDescriptor;
import edu.internet2.middleware.shibboleth.metadata.EntityDescriptor;
import edu.internet2.middleware.shibboleth.metadata.RoleDescriptor;

/**
 * Initial Date: 17.07.2004
 * 
 * @author Mike Stock Comment:
 */

public class AttributeRequestServiceImpl extends AbstractLogEnabled implements
        AttributeRequestService, ThreadSafe, Serviceable {

    private ServiceManager manager;

    /**
     * @param bpResponse
     * @return A map.
     * @throws Exception
     */
    public Map requestAttributes(BrowserProfileResponse bpResponse) throws Exception {
        // The Entity name was fed by by ShibPOSTProfile.accept(). Look it up in
        // the
        // Metadata now and return the Entity object.
        EntityDescriptor entity = getShibbolethModule().getMetadata().lookup(
                bpResponse.assertion.getIssuer());
        if (entity == null)
            throw new SAMLException(
                    "Entity(Site) deleted from Metadata since authentication POST received.");

        // Find the Shibboleth protocol AA Role configured in the Metadata for
        // this Entity. (throws MetadataException)
        AttributeAuthorityDescriptor aa = entity
                .getAttributeAuthorityDescriptor(XML.SAML11_PROTOCOL_ENUM);
        if (aa == null) {
            throw new SAMLException("No Attribute Authority in Metadata for ID=" + entity.getId());
        }

        SAMLResponse response = checkForAttributePush(bpResponse);
        if (response != null) {
            // Attributes were already pushed (by POST or Artifact)
            getLogger().info("Bypassing Attribute Query because Attributes already Pushed.");
        } else {
            response = executeAttributePull(bpResponse, aa);
            if (response == null)
                throw new SAMLException(
                        "AttributeRequestor Query to remote AA returned no response.");
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Received SAMLResponse: " + response.toString());
        }
        // At this point we either have Attribute Assertions because
        // they were already there or because we fetched them from the AA

        // Check each assertion in the response.
        applyAAP(response, aa);

        // A response may end up with no attributes, but that is not an error.
        // Maybe there is just nothing important to say about this user.
        return extractAttributes(response);
    }

    /**
     * Scan the POST data for Attribute Assertions. If any are found, then
     * attributes have been pushed and we don't need to go to the AA to get
     * them.
     * 
     * @param samldata The BrowserProfileResponse containing the SAMLResponse
     * @return A SAML response.
     */
    protected SAMLResponse checkForAttributePush(BrowserProfileResponse samldata) {
        SAMLResponse samlresponse = samldata.response;
        Iterator assertions = samlresponse.getAssertions();
        while (assertions.hasNext()) {
            SAMLAssertion assertion = (SAMLAssertion) assertions.next();
            Iterator statements = assertion.getStatements();
            while (statements.hasNext()) {
                SAMLStatement statement = (SAMLStatement) statements.next();
                if (statement instanceof SAMLAttributeStatement) {
                    getLogger().info("Found Attributes with Authenticaiton data (Attribute Push).");
                    return samlresponse;
                }
            }
        }
        return null;
    }

    protected SAMLResponse executeAttributePull(BrowserProfileResponse bpResponse,
            AttributeAuthorityDescriptor aa) throws Exception {
        // Get the POST data from the Session. It has the Subject and its
        // source.
        SAMLAuthenticationStatement authenticationStatement = bpResponse.authnStatement;
        if (authenticationStatement == null)
            throw new SAMLException("Session contains no Authentication Statement.");

        SAMLSubject subject2 = authenticationStatement.getSubject();
        if (subject2 == null)
            throw new SAMLException("Session Authentication Statement contains no Subject.");

        SAMLSubject subject = (SAMLSubject) subject2.clone();
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Subject (Handle) is " + subject.getNameIdentifier());
        }

        SAMLAttributeQuery query = new SAMLAttributeQuery(subject, getShibbolethModule()
                .getProviderId(), null);
        SAMLRequest request = new SAMLRequest(query);

        // TODO: Signing
        // String credentialId = appinfo.getCredentialIdForEntity(entity);
        // if (credentialId != null)
        // possiblySignRequest(config.getCredentials(),
        // request, credentialId);

        ShibbolethBinding binding = new ShibbolethBinding(this.manager, getLogger());
        SAMLResponse response = binding.send(request, aa, null, null);
        return response;
    }

    /**
     * Check each assertion in the response.
     * @param response
     * @param aa
     */
    protected void applyAAP(SAMLResponse response, AttributeAuthorityDescriptor aa) {
        int acount = 0;
        Iterator assertions = response.getAssertions();
        ArrayList assertionList = new ArrayList();
        while (assertions.hasNext()) {
            assertionList.add(assertions.next());
        }
        assertions = assertionList.iterator();
        while (assertions.hasNext()) {
            SAMLAssertion assertion = (SAMLAssertion) assertions.next();
            try {
                applyAAP(assertion, aa); // apply each AAP to this assertion
                acount++;
            } catch (SAMLException ex) {
                response.removeAssertion(acount); // AAP rejected all
                // statements for
                // this assertion
            }
        }
    }

    /**
     * Returns the UniqueIdentifier from a given attributeMap derived from the
     * given authStatement.
     * 
     * @param attributeMap
     * @param bpResponse
     * @return Unique identifier as defined in the config.
     */
    public String getUniqueID(Map attributeMap, BrowserProfileResponse bpResponse) {
        String originSiteName = getOriginSiteName(bpResponse.authnStatement);
        ShibbolethModule module = null;
        try {
            module = (ShibbolethModule) this.manager.lookup(ShibbolethModule.ROLE);
            String uniqueIdentifierAttribute = module.getUidMapper().resolveUIDAttribute(
                    originSiteName);
            SAMLAttribute attribute = (SAMLAttribute) attributeMap.get(uniqueIdentifierAttribute);
            Iterator it = attribute.getValues();
            if (getLogger().isDebugEnabled()) {
                StringBuffer sb = new StringBuffer();
                while (it.hasNext()) {
                    Object element = it.next();
                    sb.append("" + element + " --- ");
                }
                getLogger().debug("uniqueIdentifierAttribute: " + sb.toString());
            }

            boolean uidProvided = attribute != null && attribute.getValues().hasNext();
            return uidProvided ? (String) attribute.getValues().next() : null;
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        } finally {
            if (module != null) {
                this.manager.release(module);
            }
        }
    }

    private static String getOriginSiteName(SAMLAuthenticationStatement authStatement) {
        SAMLSubject subj = authStatement.getSubject();
        if (subj == null)
            return null;
        return subj.getNameIdentifier().getNameQualifier();
    }

    protected Map extractAttributes(SAMLResponse samlResponse) throws SAMLException {
        HashMap attributeHashMap = new HashMap();
        if (samlResponse.isSigned())
            samlResponse.checkValidity();

        Iterator assertionIterator = samlResponse.getAssertions();
        while (assertionIterator.hasNext()) {
            SAMLAssertion assertion = (SAMLAssertion) assertionIterator.next();
            Iterator statementsIterator = assertion.getStatements();
            while (statementsIterator.hasNext()) {
                SAMLStatement statement = (SAMLStatement) statementsIterator.next();
                if (statement instanceof SAMLAttributeStatement) {
                    SAMLAttributeStatement attributeStatement = (SAMLAttributeStatement) statement;
                    Iterator attributesIterator = attributeStatement.getAttributes();
                    while (attributesIterator.hasNext()) {
                        SAMLAttribute attribute = (SAMLAttribute) attributesIterator.next();
                        attributeHashMap.put(attribute.getName(), attribute);
                    }
                }
            }
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Extracted Shibboleth attributes from response: " + attributeHashMap);
        }
        return attributeHashMap;
    }

    private ShibbolethModule shibbolethModule;

    protected ShibbolethModule getShibbolethModule() {
        if (this.shibbolethModule == null) {
            try {
                this.shibbolethModule = (ShibbolethModule) this.manager
                        .lookup(ShibbolethModule.ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
        return this.shibbolethModule;
    }

    /**
     * Convenience function to apply AAP by calling the apply() method of each
     * AAP implementor.
     * <p>
     * Any AAP implementor can delete an assertion or value. Empty SAML elements
     * get removed from the assertion. This can yield an AttributeAssertion with
     * no attributes.
     * 
     * @param assertion SAML Attribute Assertion
     * @param role Role that issued the assertion
     * @throws SAMLException Raised if assertion is mangled beyond repair
     */
    protected void applyAAP(SAMLAssertion assertion, RoleDescriptor role) throws SAMLException {

        AAP aap = getShibbolethModule().getAttributeAcceptancePolicy();
        if (aap == null) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("no filters specified, accepting entire assertion");
            }
            return;
        }

        if (aap.anyAttribute()) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("AAP accepts any attribute, accepting entire assertion");
            }
            return;
        }

        // Foreach Statement in the Assertion
        Iterator statements = assertion.getStatements();

        // Statements can be deleted out of the assertion
        // which renders the interator invalid unless it is
        // based on a derived collection.
        ArrayList statementList = new ArrayList();
        while (statements.hasNext()) {
            statementList.add(statements.next());
        }
        statements = statementList.iterator();
        int istatement = 0;
        while (statements.hasNext()) {
            Object statement = statements.next();
            if (statement instanceof SAMLAttributeStatement) {
                SAMLAttributeStatement attributeStatement = (SAMLAttributeStatement) statement;

                // Check each attribute, applying any matching rules.
                Iterator attributes = attributeStatement.getAttributes();

                // Same trick for attributes
                ArrayList attributeList = new ArrayList();
                while (attributes.hasNext()) {
                    attributeList.add(attributes.next());
                }
                attributes = attributeList.iterator();

                int iattribute = 0;
                while (attributes.hasNext()) {
                    SAMLAttribute attribute = (SAMLAttribute) attributes.next();
                    boolean ruleFound = false;
                    AttributeRule rule = aap.lookup(attribute.getName(), attribute.getNamespace());
                    if (rule != null) {
                        ruleFound = true;
                        try {
                            rule.apply(attribute, role);
                        } catch (SAMLException ex) {
                            if (getLogger().isDebugEnabled()) {
                                getLogger().debug("no values remain, removing attribute");
                            }
                            attributeStatement.removeAttribute(iattribute--);
                            break;
                        }
                    }
                    if (!ruleFound) {
                        getLogger().warn(
                                "no rule found for attribute (" + attribute.getName()
                                        + "), filtering it out");
                        attributeStatement.removeAttribute(iattribute--);
                    }
                    iattribute++;
                }

                try {
                    attributeStatement.checkValidity();
                    istatement++;
                } catch (SAMLException ex) {
                    // The statement is now defunct
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("no attributes remain, removing statement");
                    }
                    assertion.removeStatement(istatement);
                }
            }
        }

        // Now see if we trashed it irrevocably.
        assertion.checkValidity();
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
