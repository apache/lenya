package org.apache.lenya.cms.observation;

//florent remove session import org.apache.lenya.cms.repository.Session;

public interface RepositoryEvent {

	/**
	 * @return The session.
	 */
	//public abstract Session getSession();

	/**
	 * @return The descriptor.
	 */
	public abstract Object getDescriptor();

	public abstract String toString();

	/**
	 * @param nodeUri The source URI of the affected node.
	 */
	public abstract void setNodeUri(String nodeUri);

	/**
	 * @return The source URI of the affected node.
	 */
	public abstract String getNodeUri();

	/**
	 * @param revision The latest revision of the node at the time the event was created.
	 */
	public abstract void setRevision(int revision);

	/**
	 * @return The latest revision of the node at the time the event was created.
	 */
	public abstract int getRevision();

	public abstract Object getSource();

}