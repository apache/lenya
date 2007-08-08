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

/* 
* Processes the event. Ordering is important for Opera 9 on Windows,
* as it must use the Mozilla style code, but will match IE style code.
*/
function LenyaDisableBackspace(e)
{
  // Mozilla style code for Opera and Safari as well
  // For Opera 9 under Windows to work properly, the Mozilla test must be first.
  if(typeof document.addEventListener != 'undefined') {
    var src = e.target.type;
    var key = e.which;
    if( key == 8 && (src != 'text' && src != 'textarea')) {
      e.preventDefault();
      e.stopPropagation();
      return false;
    }
  }
  // IE style code for IE 6 and IE 7. Should work on IE 5.5+
  else if(typeof document.attachEvent != 'undefined' && window.event) {
    var src = event.srcElement.type;
    var key = window.event.keyCode;
    if(key == 8 && (src != 'text' && src != 'textarea')) {
      window.event.cancelBubble = true;
      return false;
    }
  }
  return true;
}

/*
* Register event handler.
* This does not need to wait for onload to fire.
*/
// Mozilla style for Opera and Safari as well
if(typeof document.addEventListener != 'undefined') {
  document.addEventListener('keypress',LenyaDisableBackspace,false);
}
// IE style code for IE 6 and IE 7. Should work on IE 5.5+
else if(typeof document.attachEvent != 'undefined') {
  document.attachEvent('onkeydown',LenyaDisableBackspace);
}

