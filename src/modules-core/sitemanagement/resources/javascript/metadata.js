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

  dojo.require("dojo.io.*");
  dojo.require("dojo.event.*");

  function saveMetaData(namespace, element) {
    var id = element + "@" + namespace;
    var bindArgs = {
      url: URL,
      content: {
        "lenya.usecase": 'metadata.change',
        namespace: namespace,
        element: element,
        oldValue: dojo.byId("oldValue_" + id).value,
        value: dojo.byId("input_" + id).value
      },
      method: "post",
      encoding: "utf-8",
      error: function(response) {
        alert(response);
      },
      headers: { 
        "Content-Type" : "application/x-www-form-urlencoded; charset=utf-8"
      },
      load: function(type, data, evt) {
        var result = data.replace(/\s/g, "");
        if (result == "concurrent-change") {
          alert("Value can't be saved, concurrent change detected.");
        }
        else {
          var successIndicator = dojo.byId("saved_" + result);
          successIndicator.style.visibility = "visible";
          
          var inputField = dojo.byId("input_" + result);
          var oldValueField = dojo.byId("oldValue_" + result);
          oldValueField.value = inputField.value;
        }
      },
      mimetype: "text/plain"
    };
    dojo.io.bind(bindArgs);
  }

