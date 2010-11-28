/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

/*
   this has been put in a separate file so that users can override it
   depending on the current document type. this does not yet work, since
   this file is referenced statically in xslt/page2edit.xsl.
   a solution might be to put it in some place that is not caught by
   the global module resources pipeline and handle the overriding
   in the tinymce module sitemap.
*/

lenya_valid_elements = ""
+"a[accesskey|charset|class|href|hreflang|id|rel|rev|name"
  +"|tabindex|title|target|type],"
+"abbr[class|id|title],"
+"acronym[class|id|title],"
+"address[class|id|title],"
+"base[href|target],"
+"blockquote[cite|class|id|title],"
+"body[class|id|title],"
+"br[class|id|title],"
+"caption[class|id|title],"
+"cite[class|id|title],"
+"code[class|id|title],"
+"dd[class|id|title],"
+"dfn[class|id|title],"
+"div[class|id|title],"
+"dl[class|id|title],"
+"dt[class|id|title],"
+"em[class|id|title],"
+"form[action|class|enctype|id|method<get?post|title|target],"
+"h1[class|id|title],"
+"h2[class|id|title],"
+"h3[class|id|title],"
+"h4[class|id|title],"
+"h5[class|id|title],"
+"h6[class|id|title],"
+"head[profile],"
+"html[version],"
+"img[!alt=Image|class|height|id|longdesc|!src|title|width],"
+"input[accesskey|checked<checked|class|id|maxlength|name"
  +"|size|src|title"
  +"|type<checkbox?hidden?password?radio?reset?submit?text"
  +"|value],"
+"kbd[class|id|title],"
+"label[accesskey|class|for|id|title],"
+"li[class|id|title],"
+"link[charset|class|href|hreflang|id|media|rel|rev|title|target|type],"
+"meta[content|http-equiv|name|scheme],"
+"object[archive|class|classid|codebase|codetype|data|declare|height|href|id|name"
  +"|standby|tabindex|title|type|width],"
+"ol[class|id|title],"
+"option[class|id|selected<selected|title|value],"
+"p[class|id|title],"
+"param[id|name|type|value|valuetype<DATA?OBJECT?REF],"
+"pre[class|id|title],"
+"q[cite|class|id|title],"
+"samp[class|id|title],"
+"select[class|id|multiple<multiple|name|size|title],"
+"span[class|id|title],"
+"strong[class|id|title],"
+"table[class|id|summary|title|width],"
+"td[abbr|align<center?char?justify?left?right|axis|class"
  +"|colspan|headers|id|rowspan|scope<col?colgroup?row?rowgroup"
  +"|title|valign<baseline?bottom?middle?top],"
+"textarea[accesskey|class|cols|id|name|rows|tabindex|title],"
+"th[abbr|align<center?char?justify?left?right|axis|class"
  +"|colspan|headers|id|rowspan|scope<col?colgroup?row?rowgroup"
  +"|title|valign<baseline?bottom?middle?top],"
+"title[],"
+"tr[align<center?char?justify?left?right|class"
  +"|id|title|valign<baseline?bottom?middle?top],"
+"ul[class|id|title],"
+"var[class|id|title]"
