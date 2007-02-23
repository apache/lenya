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


Each module must include a modules.xml file and should adhere to the
directory layout described in
http://lenya.apache.org/docs/1_4/reference/modules/index.html

NOTE: be careful to specify the depencies correctly. The <depends/> element
is meant for java code dependencies only, i.e. issues that affect
compilation. If a module merely links to another module's usecases in its
views, that does not qualify as a dependency (although maybe it should at
some point in the future).
