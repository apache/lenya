/**
 * 
 */
package org.apache.lenya.cms.observation;

public class TestListener implements RepositoryListener {
    
    private boolean notified = false;

    public void documentChanged(RepositoryEvent event) {
        this.notified = true;
    }
    
    public boolean wasNotified() {
        return this.notified;
    }
    
    public void reset() {
        this.notified = false;
    }
    
}