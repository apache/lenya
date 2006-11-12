/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
//var oldpassword, newpassword, confirmednewpassword;

function passwd() {
  var exceptionMessage = null;

  var oldpassword = 'not specified yet';
  var newpassword = 'not specified yet';
  var confirmednewpassword = 'not specified yet';

  while (true) {
      sendPageAndWait("passwd-input-screen", { "exceptionMessage" : exceptionMessage, "oldpassword" : oldpassword, "newpassword" : newpassword, "confirmednewpassword" : confirmednewpassword });
      oldpassword = cocoon.request.getParameter("oldpassword");
      newpassword = cocoon.request.getParameter("newpassword");
      confirmednewpassword = cocoon.request.getParameter("confirmednewpassword");


      var resolver = cocoon.environment.getObjectModel().get("source-resolver");
      var sitemapPath = resolver.resolve("").getSystemId().substring(5);
      var session = cocoon.request.getSession(false);
      var authenticatorId = session.getAttribute("org.apache.lenya.cms.cocoon.acting.Authenticator.id");
      var identityFromSession = session.getAttribute("org.apache.lenya.ac.Identity");
      var filename = sitemapPath + "pubs/" + authenticatorId + "/config/ac/passwd/" + identityFromSession.getUsername() + ".iml";
      var identity = new Packages.org.apache.lenya.ac.Identity(filename);
      if (identity.changePassword(oldpassword, newpassword, confirmednewpassword)) {
          identity.writeDocument(filename);
          break;
      } else {
          exceptionMessage = "Either authentication failed (username = " + identity.getUsername() + ") or new password and confirmed new password do not match or new password length is not between 5 and 8 characters";
          continue;
      }
  }

  exceptionMessage = 'New password has been set';
  sendPageAndWait("passwd-modified-screen", { "exceptionMessage" : exceptionMessage, "oldpassword" : oldpassword, "newpassword" : newpassword, "confirmednewpassword" : confirmednewpassword });

  //newpassword = getPasswords();
}

function getPasswords()
{
  var uri = "password-screen";
  sendPageAndWait(uri, { "a" : 3, "b" : 4 });
  return cocoon.request.getParameter("newpassword");
}
