/*
 * Copyright 1999-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.reading;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.xml.sax.SAXException;


/**
 * The <code>RequestReader</code> component is used to serve binary data
 * from the Http request
 */ 
public class RequestReader extends AbstractReader {

  protected int bufferSize = 512;

  protected Response response;

  protected HttpServletRequest httpRequest;

  /**
   * Setup the reader The resource is opened to get an
   * <code>HttpServletRequest</code>
   */
  public void setup(SourceResolver resolver, Map objectModel, String src,
      Parameters par) throws ProcessingException, SAXException, IOException {
    super.setup(resolver, objectModel, src, par);

    this.response = ObjectModelHelper.getResponse(objectModel);

    this.httpRequest = (HttpServletRequest) objectModel
        .get(HttpEnvironment.HTTP_REQUEST_OBJECT);

    if (httpRequest == null) {
      throw new ProcessingException(
          "This feature is only available in an http environment.");
    }
  }

  /**
   * Recyclable
   */
  public void recycle() {
    this.httpRequest = null;
    super.recycle();
  }

  protected void processStream(InputStream inputStream) throws IOException,
      ProcessingException {

    byte[] buffer = new byte[bufferSize];
    int length = -1;

    long contentLength = httpRequest.getContentLength();

    if (contentLength != -1) {
      response.setHeader("Content-Length", Long.toString(contentLength));
    }

    while ((length = inputStream.read(buffer)) > -1) {
      out.write(buffer, 0, length);
    }
    out.flush();
  }

  /**
   * Generates the requested resource.
   */
  public void generate() throws IOException, ProcessingException {
    try {
      InputStream inputStream = httpRequest.getInputStream();

      try {
        processStream(inputStream);
      } finally {
        if (inputStream != null) {
          inputStream.close();
        }
      }

    } catch (IOException e) {
      getLogger()
          .debug(
              "Received an IOException, assuming client severed connection on purpose");
    }
  }

  /**
   * Returns the mime-type of the resource in process.
   */
  public String getMimeType() {
    Context ctx = ObjectModelHelper.getContext(objectModel);
    if (ctx != null) {
      final String mimeType = ctx.getMimeType(source);
      if (mimeType != null) {
        return mimeType;
      }
    }
    return null;
  }
}
