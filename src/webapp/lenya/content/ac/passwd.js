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
      var identityFromSession = session.getAttribute("org.apache.lenya.cms.ac.Identity");
      var filename = sitemapPath + "pubs/" + authenticatorId + "/content/ac/passwd/" + identityFromSession.getUsername() + ".iml";
      var identity = new Packages.org.apache.lenya.cms.ac.Identity(filename);
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
