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

/*
      if (oldpassword != 'vanya') {
          exceptionMessage = 'Authentication failed';
          continue;
      }
      //if (newpassword != 'levi') {
      if (newpassword.trim() != newpassword.trim()) {
      //if (newpassword.trim() != confirmednewpassword.trim()) {
          exceptionMessage = "New password and confirmed new password do not match (#" + newpassword + "##" + confirmednewpassword + "#)";
          continue;
      }
      //if (oldpassword == 'vanya' && newpassword == confirmednewpassword) {
      if (oldpassword == 'vanya' && newpassword == 'levi') {
          break;
      }
*/
      if (Packages.org.lenya.cms.ac.Identity.changePassword(oldpassword, newpassword, confirmednewpassword)) {
          break;
      } else {
          exceptionMessage = "Either authentication failed or new password and confirmed new password do not match (#" + newpassword + "##" + confirmednewpassword + "#)";
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
