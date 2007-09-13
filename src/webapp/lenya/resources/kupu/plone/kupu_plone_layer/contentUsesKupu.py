## Script (Python) "contentUsesKupu"
##title=Allow graceful degradation if content is not text/html
##bind container=container
##bind context=context
##bind namespace=
##bind script=script
##bind subpath=traverse_subpath
##parameters=fieldname
from Products.CMFCore.utils import getToolByName

# If the user doesn't have kupu configured then we can't use it.
if not context.browserSupportsKupu():
    return False

if not fieldname:
    return True # Non AT content always tries to use kupu

REQUEST = context.REQUEST

if fieldname == REQUEST.form.get('kupu.convert', ''):
    return True
if fieldname == REQUEST.form.get('kupu.suppress', ''):
    return False

if not hasattr(context, 'getField'):
    return True
    
field = context.getField(fieldname)
if not field:
  return True
text_format = REQUEST.get('%s_text_format' % fieldname, context.getContentType(fieldname))
content = field.getEditAccessor(context)()

return len(content)==0 or 'html' in text_format.lower()

