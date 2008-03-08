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
/* $Id: PublicationException.java 617035 2008-01-31 07:44:03Z solprovider $  */
package org.apache.lenya.cms.content;
/**
 * 
 * @author solprovider
 * @since 1.3
 */
public class ResourceException extends Exception {
   private static final long serialVersionUID = 1L;
   /**
    * Creates a new ResourceException.
    * 
    */
   public ResourceException() {
      super();
   }
   /**
    * Creates a new ResourceException.
    * 
    * @param message
    *           the exception message
    */
   public ResourceException(String message) {
      super(message);
   }
   /**
    * Creates a new ResourceException.
    * 
    * @param message
    *           the exception message
    * @param cause
    *           the cause of the exception
    */
   public ResourceException(String message, Throwable cause) {
      super(message, cause);
   }
   /**
    * Creates a new ResourceException.
    * 
    * @param cause
    *           the cause of the exception
    */
   public ResourceException(Throwable cause) {
      super(cause);
   }
}
