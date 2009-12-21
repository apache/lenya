/**
 * 
 */
package org.apache.lenya.cms.observation;

public class TestListener implements RepositoryListener {
    
    private boolean changed = false;
    private boolean removed = false;

    public void eventFired(RepositoryEvent repoEvent) {
        if (!(repoEvent instanceof DocumentEvent)) {
            return;
        }
        DocumentEvent event = (DocumentEvent) repoEvent;
        if (event.getDescriptor().equals(DocumentEvent.CHANGED)) {
            this.changed = true;
        }
        else if (event.getDescriptor().equals(DocumentEvent.REMOVED)) {
            this.removed = true;
        }
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

}