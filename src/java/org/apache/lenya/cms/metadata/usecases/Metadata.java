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

    try {
      MetaData meta = getSourceDocument().getMetaDataManager()
          .getDublinCoreMetaData();

      String[] keys = meta.getPossibleKeys();
      for (int i = 0; i < keys.length; i++) {
        String value = meta.getFirstValue(keys[i]);
        if (value != null) {
          setParameter(keys[i], value);
        }
      }

    } catch (Exception e) {
      getLogger().error("Unable to load Dublin Core metadata.", e);
      addErrorMessage("Unable to load Dublin Core metadata.");
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

    MetaData meta = getSourceDocument().getMetaDataManager()
        .getDublinCoreMetaData();

    String[] keys = meta.getPossibleKeys();
    for (int i = 0; i < keys.length; i++) {
      String value = getParameterAsString(keys[i]);
      if (value != null) {
        meta.setValue(keys[i], value);
      }
    }
    meta.save();

    // TODO set workflow situation to edit here.
  }

}
