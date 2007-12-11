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

function lenyaGetTitle(num) {
  return document.getElementById('lenyaTabTitle' + num);
}

function lenyaGetBody(num) {
  return document.getElementById('lenyaTabBody' + num);
}

function lenyaInitTabs(count) {
  lenyaToggleTab(count, 0);
}

function lenyaToggleTab(count, num) {
  for (var i = 0; i < count; i++) {
    var title = document.getElementById('lenyaTabTitle' + i);
    var body = document.getElementById('lenyaTabBody' + i);
    if (i == num) {
      title.className = 'lenyaTabTitleActive';
      body.className = 'lenyaTabBodyActive';
    }
    else {
      title.className = 'lenyaTabTitle';
      body.className = 'lenyaTabBody';
    }
  }
}