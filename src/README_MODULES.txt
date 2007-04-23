Module categories:
==================

The modules in Lenya are currently split up into the following categories:

* modules-core
  This set of modules implements the core functionality of Lenya.
  Modules in the core will not have any dependencies outside of the core.


* modules
  This is where most of the add-on functionality goes: support for specific 
  editors, document types, the nifty new feature of the day, etc.


* modules-optional
  The scratchpad. Modules in here do not necessarily have to work or even
  compile, nor have a stable API. (FIXME: should modules that require 
  external code to be useful also be moved here, such as tinymce or
  fckeditor?


* modules-legacy
  This is the place for modules that do not adhere to the programming 
  conventions of the trunk, but are still needed as no updated version is
  available. For instance, modules that use the 1.2 way of handling usecases
  without using the usecase framework should go here. (FIXME: does that make
  the kupu module a candidate?)

Directory Layout:
=================

Each module should adhere to the following directory layout (subdirectories
if appropriate, take a look at the core modules if in doubt):

mymodule/
  config/                    configuration files
    module.xml               module descriptor
    cocoon-xconf/            patches for cocoon.xconf:
      component-mycomponent.xconf
      usecase-myusecase.xconf     
  usecases/
    myusecase.jx             usecase view
  java/
    src/                     Java source files
    lib/                     Java libraries
  xslt/                      XSLT stylesheets
  sitemap.xmap               main module sitemap


module.xml
==========

Each module must contain a description file config/module.xml:

<module xmlns="http://apache.org/lenya/module/1.0">
  <!-- the ID *must* start with the package name of 
       your custom Java code -->
  <id>org.myproject.lenya.modules.myeditor</id>
  <!-- build dependencies; see note below -->
  <depends module="org.apache.lenya.modules.usecase"/>
  <depends module="org.apache.lenya.modules.webdav"/>
  <!-- the Java package your module belongs to -->
FIXME: why is this needed? afaiu it could be gleaned from the id...
  <package>org.myproject.lenya.modules</package>
  <!-- a version string -->
FIXME: what does this do? just for information, or is there version
management?
  <version>0.1-dev</version>
  <!-- a short name for your module -->
  <name>My Own Editor</name>
  <!-- the required lenya version -->
FIXME: is this actually used?
  <lenya-version>1.4-dev</lenya-version>
  <!-- a detailed description of your module -->
  <description>
    This is my own editor.
    For more information, visit http://myproject.org/editor.
  </description>
</module>

NOTE: be careful to specify the depencies correctly. The <depends/> element
is meant for java code dependencies only, i.e. issues that affect
compilation. If a module merely links to another module's usecases in its
views, that does not qualify as a dependency (although maybe it should at
some point in the future).


see also:
=========

Please refer to the online docs at
http://lenya.apache.org/docs/1_4/reference/modules/index.html.
