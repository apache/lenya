#!/bin/sh

BASE_URL=http://127.0.0.1:48080/lenya/docs

#WEBPAGES='index.html /lenya/lenya.org/images/lenya_org.gif /lenya/lenya.org/css/pc-mac-lunix.css'
#WEBPAGES='index.html js-index.html inhalt.txt style.js displayToc.js /lenya/lenya.org/images/lenya_org.gif /lenya/lenya.org/css/pc-mac-lunix.css'
WEBPAGES='index.html js-index.html inhalt.txt style.js displayToc.js /lenya/lenya.org/images/lenya_org.gif /lenya/lenya.org/css/pc-mac-lunix.css  xdocs/user-guide.html xdocs/usr-glitches.html xdocs/usr-glitches.html xdocs/usr-authorization.html xdocs/usr-authorization.html xdocs/usr-authorization.html xdocs/usr-authorization.html xdocs/usr-authorization.html xdocs/authoring xdocs/usr-create.html xdocs/usr-create.html xdocs/usr-create.html xdocs/usr-import.html xdocs/ xdocs/ xdocs/ xdocs/edit xdocs/usr-edit-html.html xdocs/usr-edit-xopus.html xdocs/bitflux.html xdocs/bitflux-howto.html xdocs/usr-edit-eonpro.html xdocs/usr-publish.html xdocs/administrator-guide.html xdocs/adm-authorization.html xdocs/ xdocs/adm-authorization.html xdocs/ xdocs/adm-authorization.html xdocs/ xdocs/integrator-guide.html xdocs/int-create-pub.html xdocs/document xdocs/ xdocs/ xdocs/ xdocs/parent-child-creator.html xdocs/int-filesystem-publisher.html xdocs/edit xdocs/int-html-form-editor.html xdocs/xopus.html xdocs/ xdocs/ xdocs/ xdocs/ xdocs/ xdocs/ xdocs/ xdocs/ xdocs/ xdocs/ xdocs/ xdocs/ xdocs/int-virtual-server.html xdocs/int-proxy-apache.html xdocs/ xdocs/int-performance.html xdocs/ manuals/browser/sidebar/ xdocs/int-hsqldb.html xdocs/ xdocs/int-tomcat.html xdocs/ xdocs/encoding.html xdocs/developer-guide.html xdocs/ xdocs/ xdocs/architecture.html  xdocs/access-controller_deprecated.html xdocs/access-controller.html xdocs/ xdocs/ xdocs/ xdocs/ xdocs/ xdocs/ xdocs/authoring xdocs/create-resource.html xdocs/parent-child-creator.html xdocs/document-collection.html xdocs/ xdocs/http-upload-image.html xdocs/file-upload.html xdocs/edit xdocs/html-form-editor.html xdocs/xopus2.html xdocs/xopus.html xdocs/xopus.html xdocs/xopus.html xdocs/xopus.html xdocs/xopus.html xdocs/xopus.html xdocs/xopus.html xdocs/xopus.html  xdocs/bitflux.html xdocs/bitflux-howto.html xdocs/bitflux-howto.html xdocs/bitflux-howto.html xdocs/bitflux-howto.html xdocs/bitflux-howto.html xdocs/bitflux-howto.html xdocs/editonpro.html     xdocs/ xdocs/cm-markup-language.html xdocs/revision-controller.html xdocs/slide.html xdocs/wf-markup-language.html xdocs/workflow.html xdocs/xml.html xdocs/task-development.html xdocs/tasks-publisher.html xdocs/tasks-exporter.html xdocs/tasks-mailtask.html xdocs/publishing.html xdocs/filesystem-publisher.html xdocs/command-line-interface.html xdocs/replication.html xdocs/    xdocs/  xdocs/ xdocs/ xdocs/ xdocs/ xdocs/ xdocs/int-syndication.html xdocs/ xdocs/scheduler.html xdocs/ xdocs/lucene.html xdocs/ xdocs/       xdocs/proxy-generator.html xdocs/release-manager.html  xdocs/javadoc.html xdocs/index.html'

PWD=`pwd`
TMP_NAME=htdocs_lenya.org_docs
TMP_DIR="$PWD/$TMP_NAME"
echo "TEMPORARY DIRECTORY: $TMP_DIR"
mkdir -p $TMP_DIR

rm $TMP_NAME.tar.gz

echo "Export Static HTML"
echo "BEGIN"
for WEBPAGE in $WEBPAGES
do
  echo "$WEBPAGE"
  PARENT=`dirname $TMP_DIR/$WEBPAGE`
  mkdir -p $PARENT
  wget --quiet $BASE_URL/$WEBPAGE -O$TMP_DIR/$WEBPAGE
  #grep -l "/lenya/docs" $TMP_DIR/$WEBPAGE
  sed -e 's/\/lenya\/docs//g' -e 's/\lenya\/lenya.org\///g' $TMP_DIR/$WEBPAGE > tmp.html
  mv tmp.html $TMP_DIR/$WEBPAGE
done
echo "END"

tar -czf $TMP_NAME.tar.gz $TMP_NAME
rm -r $TMP_DIR
