package org.apache.lenya.cms.cocoon.components.modules.input;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.lenya.cms.cocoon.components.source.impl.VirtualSourceFactory;
/**
 * {virtual:new} Reserves and returns a new Key for a VirtualSource.
 * {virtual:Key} Releases Key for a VirtualSource.
 */
public class VirtualModule extends AbstractInputModule implements ThreadSafe {
   /**
    * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String,
    *      org.apache.avalon.framework.configuration.Configuration,
    *      java.util.Map)
    */
   public Object getAttribute(String name, Configuration modeConf, Map objectModel) throws ConfigurationException {
      if(name.equalsIgnoreCase("new"))
         return VirtualSourceFactory.reserve();
      VirtualSourceFactory.releaseSource(name);
      return name;
   }
   /**
    * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration,
    *      java.util.Map)
    */
   public Iterator getAttributeNames(Configuration modeConf, Map objectModel) throws ConfigurationException {
      return Collections.EMPTY_SET.iterator();
   }
   /**
    * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String,
    *      org.apache.avalon.framework.configuration.Configuration,
    *      java.util.Map)
    */
   public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel) throws ConfigurationException {
      Object[] objects = {getAttribute(name, modeConf, objectModel)};
      return objects;
   }
   /**
    * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
    */
   public void configure(Configuration conf) throws ConfigurationException {
      super.configure(conf);
   }
}