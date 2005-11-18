/*
* Copyright 1999-2004 The Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

function saveXML() {
  var resolver = null;
  var source = null;
  var outputStream = null;
  try {
    resolver = cocoon.getComponent(Packages.org.apache.excalibur.source.SourceResolver.ROLE);

    var uri = cocoon.parameters.source;
    source = resolver.resolveURI(uri);

    outputStream = source.getOutputStream();

    cocoon.processPipelineTo("saveXML", null, outputStream);

  } catch (error) {
    log.cocoon.error("Error saving document: " + error);
    cocoon.sendStatus(500);
  } finally {
    if (source != null)
      resolver.release(source);
    if (outputStream != null) {
      try {
        outputStream.flush();
        outputStream.close();
      } catch (error) {
        cocoon.log.error("Could not flush/close outputstream: " + error);
        cocoon.sendStatus(500);
      }
    }
    if (resolver != null)
      cocoon.releaseComponent(resolver);

    cocoon.sendStatus(204);
    cocoon.exit();
  }
}

