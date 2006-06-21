/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.lenya.cms.ant;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Module descriptor list.
 */
public class ModuleDescriptorList {

    private Map id2descriptor = new HashMap();

    public void add(File srcDir) {
        ModuleDescriptor descriptor = new ModuleDescriptor(srcDir);
        if (!this.id2descriptor.containsKey(descriptor.getId())) {
            this.id2descriptor.put(descriptor.getId(), descriptor);
        }
    }

    public ModuleDescriptor get(String id) {
        if (!this.id2descriptor.containsKey(id)) {
            throw new IllegalArgumentException("Module [" + id + "] not found!");
        }
        return (ModuleDescriptor) this.id2descriptor.get(id);
    }

    public ModuleDescriptor[] getSortedDescriptors() {
        Collection values = this.id2descriptor.values();
        ModuleDescriptor[] descriptors = (ModuleDescriptor[]) values.toArray(new ModuleDescriptor[values.size()]);
        sort(descriptors);
        return descriptors;
    }

    public boolean dependsOn(ModuleDescriptor d1, ModuleDescriptor d2) {
        boolean depends = false;
        if (Arrays.asList(d1.getDependencies()).contains(d2.getId())) {
            depends = true;
        } else {
            String[] dependencies = d1.getDependencies();
            for (int i = 0; i < dependencies.length; i++) {
                ModuleDescriptor dependency = ModuleDescriptorList.this.get(dependencies[i]);
                depends = depends || dependsOn(dependency, d2);
            }
        }
        return depends;
    }

    public void sort(ModuleDescriptor[] descriptors) {
        for (int i = 0; i < descriptors.length; i++) {
            for (int j = i + 1; j < descriptors.length; j++) {
                if (dependsOn(descriptors[i], descriptors[j])) {
                    ModuleDescriptor temp = descriptors[i];
                    descriptors[i] = descriptors[j];
                    descriptors[j] = temp;
                }
            }
        }
    }
}
