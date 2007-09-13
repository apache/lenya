## Script (Python) "browserSupportsKupu"
##title=Allow graceful degradation is browser isn't supported
##bind container=container
##bind context=context
##bind namespace=
##bind script=script
##bind subpath=traverse_subpath
##parameters=useragent=''
from Products.CMFCore.utils import getToolByName

def numerics(s):
    '''Convert a string into a tuple of all digit sequences
    Since we are in a Zope script we can't use regexes, so lets go back to first principles.
    '''
    seq = ['']
    for c in s:
        if c.isdigit():
            seq[-1] = seq[-1] + c
        elif seq[-1]:
            seq.append('')
    return tuple([ int(val) for val in seq if val])

pm = getToolByName(context, 'portal_membership')
user = pm.getAuthenticatedMember()
if user.getProperty('wysiwyg_editor').lower() != 'kupu':
    return False

if not useragent:
    useragent = context.REQUEST['HTTP_USER_AGENT']


if 'Opera' in useragent or 'BEOS' in useragent:
    return False

if not useragent.startswith('Mozilla/'):
    return False

try:
    mozillaver = numerics(useragent[len('Mozilla/'):].split(' ')[0])
    if mozillaver > (5,0):
        return True
    elif mozillaver == (5,0):
        rv = useragent.find(' rv:')
        if rv >= 0:
            verno = numerics(useragent[rv+4:].split(')')[0])
            return verno >= (1,3,1)

    MSIE = useragent.find('MSIE')
    if MSIE >= 0:
        verno = numerics(useragent[MSIE+4:].split(';')[0])
        return verno >= (5,5)

except:
    # In case some weird browser makes the test code blow up.
    pass
return False
