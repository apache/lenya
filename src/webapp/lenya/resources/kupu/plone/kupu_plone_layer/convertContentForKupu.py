## Script (Python) "convertContentForKupu"
##title=Convert content to HTML for editing with Kupu
##bind container=container
##bind context=context
##bind namespace=
##bind script=script
##bind subpath=traverse_subpath
##parameters=fieldname, inputvalue
from Products.CMFCore.utils import getToolByName
from Products.PythonScripts.standard import structured_text, newline_to_br

field = context.getField(fieldname)
text_format = context.REQUEST.get('%s_text_format' % fieldname, context.getContentType(fieldname))
accessor = field.getEditAccessor(context)

content = inputvalue

if len(content)==0 or 'html' in text_format.lower():
    return content
 
transforms = getToolByName(context, 'portal_transforms')
return transforms.convertTo('text/html', content, mimetype=text_format)
