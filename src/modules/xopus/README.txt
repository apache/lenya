How to configure/install:
1. download your preferred xopus release (i.e. Xopus-2.1.79)

2. follow the installation instructions to install it on your server
   (in this case we are unzipping to the tomcat webapps/ROOT directory)
   
3. configure the xopus context and path in (local.)build.properties
   (i.e. xopus.context=Xopus-2.1.79, xopus.path=relative/path/to/tomcat/webapps/ROOT)
   
4. set these same two parameters in modules/xopus/config/cocoon-xconf/input-modules/xopus.xconf

5. [optional] if you wish to use a resource type other than xhtml, create a resource type-specific xsl file
   in <your-pub>/modules/xopus/xslt/{resource-type}.xsl
   
6. [optional] this applies only if you do step 5.  create a resource type-specific xsd schema file
   in <your-pub>/modules/xopus/resources/schemas/{resource-type}.xsd
   
7. TODO: add the xopus module call to menu.xsp
   (currently menu.xsp only recognizes usecases when creating the url, we need to modify the stylesheet to
   accept lenya.module in addition to lenya.usecase.  It should look similar to what is shown below)

    ex: menu.xsp
    
    [...]
      <menu i18n:attr="name" name="Edit">
          <xsp:logic>
            String doctype = <input:get-attribute module="page-envelope" as="string" name="document-type"/>;
            if ("xhtml".equals(doctype)) {
                <block info="false">
                  <item wf:event="edit" uc:usecase="bxe.edit" href="?"><i18n:text>With BXE</i18n:text></item>
                  <item wf:event="edit" uc:usecase="editors.oneform" href="?"><i18n:text>With one Form</i18n:text></item>
                  <item wf:event="edit" mod:module="xopus" href="?lenya.step=open"><i18n:text>With Xopus</i18n:text></item>
                </block>
            }
          </xsp:logic>
      </menu>
    [...]
    
8. build lenya

9. try to edit your document by adding "?lenya.module=xopus&lenya.step=open" to the url and hitting enter.
   this will eventually be replaced by the menu entry in step 7.
    
