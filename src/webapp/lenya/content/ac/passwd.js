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

      if (oldpassword != 'vanya') {
          exceptionMessage = 'Authentication failed';
          continue;
      }
      if (newpassword != 'levi') {
      //if (newpassword != confirmednewpassword) {
          exceptionMessage = 'New password and confirmed new password do not match';
          continue;
      }
      if (oldpassword == 'vanya' && newpassword == 'levi') {
          break;
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
