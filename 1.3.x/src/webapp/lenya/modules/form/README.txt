<module id="form" name="Form">
   <version>
      <minimum>1.2</minimum>
      <created>1.3</created>
   </version>
<description>Handles forms.</description>
<usage>
=== post.xsp
Creates XML from POST data.
<map:generate type="serverpages" src="module://form/post.xsp"/>

=== formfixer.xsl
Adds the language to the ACTION for i18n-compatible URLs with CForms.

=== forms-samples-styling.xsl
From the Cocoon "form" block.
Adds formatting to a form

=== XMAP Usage of formfixer.xsl and forms-sampling.xsl

To Generate the Form:
<map:generate src="CForms-Template.xml"/>
<map:transform type="forms"/>
<map:transform src="module://form/formfixer.xsl">
   <map:parameter name="language" value="{page-envelope:document-language}"/>
</map:transform>
<map:transform src="module://form/forms-samples-styling.xsl"/>

For Continuation:
<map:match pattern="**/*_*.more">
   <map:call continuation="{2}"/>
</map:match>
</usage>
</module>