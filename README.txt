# What is the doco pub?
This publication is created with the aim to be a generic documentation publication for Apache wide usage.

The first projects to test it are the Apache Forrest and the Apache Lenya project.

The idea is to manage the content via the Apache Lenya CMS and export it via Apache Forrest.

# How to install?
## Download
You need to have the svn head of lenya and forrest:
svn co https://svn.apache.org/repos/asf/lenya/trunk lenya-trunk
svn co https://svn.apache.org/repos/asf/forrest/trunk forrest-trunk

Further you need to download the doco pub:
svn co https://svn.apache.org/repos/asf/lenya/sandbox/doco doco

We will refer to the checkouts {lenya-trunk} for lenya and {forrest-trunk} for forrest
and {doco} for the doco pub.

## Install forrest/lenya
Follow 
http://forrest.apache.org/docs_0_80/build.html
and
http://lenya.apache.org/1_4/installation/source_version.html

##Install lenya docu pub.
Since we are using a specific pub we need to tell lenya about it. 
In your {lenya-trunk}/local.build.properties 
*** NOTE: 
*** If you not yet have them do
*** cp {lenya-trunk}/build.properties {lenya-trunk}/local.build.properties) 
you need to 
1) add the pub:
pubs.root.dirs={doco}:{pubs.root.dirs.more.pubs}
2) add the modules:
modules.root.dirs={doco}/modules:{modules.root.dirs.more.modules}
3) change the default port
web.app.server.jetty.port=9999

Since we are using an external content dir we will need to override the publication.xconf
1) cp {doco}/config/publication.xconf {doco}/config/local.publication.xconf
2) change <content-dir src="/home/thorsten/src/apache/forrest-trunk/whiteboard/doco"/> to
<content-dir src="{forrest-trunk}/whiteboard/doco"/> for forrest (we will start with this)

# Start
## CMS mode
For authoring start lenya with
{lenya-trunk}/build.sh
{lenya-trunk}/lenya.sh
http://localhost:9999/doco/authoring/index.html
## Render mode
To render your lenya site with forrest first you will need to deploy 
locally some plugins (first time and after ./build.sh clean) and then start forrest.
1) deploy locally
cd {forrest-trunk}/whiteboard/plugins/org.apache.forrest.themes.core
$FORREST_HOME/tools/ant/bin/ant local-deploy
cd ../org.apache.forrest.plugin.internal.dispatcher
$FORREST_HOME/tools/ant/bin/ant local-deploy
cd ../org.apache.forrest.plugin.input.Lenya
$FORREST_HOME/tools/ant/bin/ant local-deploy
2) Run forrest, run ;)
cd $FORREST_HOME/whiteboard/doco
forrest run
http://localhost:8888/index.html

WARNING
This is a prototype ONLY for now, do not use it in production!!!

NOTE
For now we are rendering the authoring area only and do not support
the live are. This will change.

TODO
* lenya 
- due to the exit usecases set in some of the usecases it throws an error 
 -> remove all exit usecases in default conf or override it.
* forrest
- we need a xhtml to xdocs plugin
