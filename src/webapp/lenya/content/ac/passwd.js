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


      var session = cocoon.request.getSession(false);
      var identityFromSession = session.getAttribute("org.lenya.cms.ac.Identity");
      var filename = "/home/michi/build/jakarta-tomcat-4.1.21-LE-jdk14/webapps/lenya/lenya/pubs/oscom/content/ac/passwd/" + identityFromSession.getUsername() + ".iml";
      var identity = new Packages.org.lenya.cms.ac.Identity(filename);
      if (identity.changePassword(oldpassword, newpassword, confirmednewpassword)) {
          break;
      } else {
          exceptionMessage = "Either authentication failed (" + identity.getUsername() + ") or new password and confirmed new password do not match (#" + newpassword + "##" + confirmednewpassword + "#)";
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
