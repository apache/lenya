/**
 * 
 */
package org.apache.lenya.cms.observation;

public class TestListener implements RepositoryListener {
    
    private boolean changed = false;
    private boolean removed = false;

    public void documentChanged(DocumentEvent event) {
        this.changed = true;
    }
    
    public boolean wasChanged() {
        return this.changed;
    }
    
    public boolean wasRemoved() {
        return this.removed;
    }
    
    public void reset() {
        this.changed = false;
        this.removed = false;
    }

    public void documentRemoved(DocumentEvent event) {
        this.removed = true;
    }
    
}