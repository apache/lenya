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
package org.apache.lenya.cms.metadata.usecases;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.usecases.SiteUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Usecase to edit metadata for a resource.
 * 
 * @version $Id$
 */
public class Metadata extends SiteUsecase {

  /**
   * Ctor.
   */
  public Metadata() {
    super();
  }

  public static final String DC_FORM_PREFIX = "meta.dc.";

  public static final String CUSTOM_FORM_PREFIX = "meta.custom.";

  public static final String SHOW_CUSTOM_PARAMETER = "showCustom";

  private boolean showCustom;

  /**
   * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
   */
  protected Node[] getNodesToLock() throws UsecaseException {
    Node[] objects = { getSourceDocument().getRepositoryNode() };
    return objects;
  }

  /**
   * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
   */
  protected void initParameters() {
    super.initParameters();
    showCustom = getParameterAsBoolean(SHOW_CUSTOM_PARAMETER, false);
    setParameter(SHOW_CUSTOM_PARAMETER, String.valueOf(showCustom));

    // dc metadata
    try {
      MetaData meta = getSourceDocument().getMetaDataManager()
          .getDublinCoreMetaData();

      String[] keys = meta.getPossibleKeys();
      for (int i = 0; i < keys.length; i++) {
        String value = meta.getFirstValue(keys[i]);
        if (value != null) {
          setParameter(DC_FORM_PREFIX + keys[i], value);
        }
      }

    } catch (Exception e) {
      getLogger().error("Unable to load Dublin Core metadata.", e);
      addErrorMessage("Unable to load Dublin Core metadata.");
    }

    // custom metadata
    try {
      MetaData customMeta = getSourceDocument().getMetaDataManager()
          .getCustomMetaData();

      HashMap customMetaHash = customMeta.getAvailableKey2Value();
      Iterator customKeys = customMetaHash.keySet().iterator();
      while (customKeys.hasNext()) {
        String key = (String) customKeys.next();
        String value = (String) customMetaHash.get(key);
        if (value != null) {
          setParameter(CUSTOM_FORM_PREFIX + key, value);
        }
      }

    } catch (Exception e) {
      getLogger().error("Unable to load custom metadata.", e);
      addErrorMessage("Unable to load custom metadata.");
    }

  }

  /**
   * Validates the request parameters.
   * 
   * @throws UsecaseException
   *           if an error occurs.
   */
  void validate() throws UsecaseException {
    // do nothing
  }

  /**
   * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
   */
  protected void doCheckExecutionConditions() throws Exception {
    validate();
  }

  /**
   * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
   */
  protected void doExecute() throws Exception {
    super.doExecute();

    // dc metadata
    MetaData meta = getSourceDocument().getMetaDataManager()
        .getDublinCoreMetaData();

    String[] keys = meta.getPossibleKeys();
    for (int i = 0; i < keys.length; i++) {
      String value = getParameterAsString(DC_FORM_PREFIX + keys[i]);
      if (value != null) {
        meta.setValue(keys[i], value);
      }
    }

    // custom metadata
    MetaData customMeta = getSourceDocument().getMetaDataManager()
        .getCustomMetaData();
    String[] parameterNames = getParameterNames();
    for (int i = 0; i < parameterNames.length; i++) {
      String id = parameterNames[i];
      if (id.startsWith(CUSTOM_FORM_PREFIX)) {
        String key = id.substring(CUSTOM_FORM_PREFIX.length());
        String value = getParameterAsString(id);
        if (value != null) {
          customMeta.setValue(key, value);
        }
      }
    }

    // TODO set workflow situation to edit here.
  }

  public String getTargetURL(boolean success) {
    showCustom = getParameterAsBoolean(SHOW_CUSTOM_PARAMETER, false);
    if (showCustom) {
      String transfer = getSourceDocument().getCanonicalWebappURL()
          + "?lenya.usecase=tab.meta&showCustom=true";
      return transfer;
    } else
      return super.getTargetURL(success);
  }
}
