package org.apache.lenya.cms.usecase;

import java.util.Properties;

import org.apache.lenya.cms.usecase.gui.GUIManager;
import org.apache.lenya.cms.usecase.gui.Tab;

public interface UsecaseView {

	/**
	 * @return The URI of the JX template;
	 */
	public abstract String getViewURI();

	/**
	 * @return whether the menubar should be visible on usecase screens.
	 */
	public abstract boolean showMenu();

	/**
	 * @return whether a continuation should be created.
	 */
	public abstract boolean createContinuation();

	/**
	 * @return the Flowscript snippet to be executed during the usecase view loop.
	 */
	public abstract String getCustomFlow();

	/**
	 * @param name The parameter name.
	 * @return The parameter value.
	 */
	public abstract String getParameter(String name);

	/**
	 * @return The tab the usecase belongs to or <code>null</code>.
	 */
	public abstract Tab getTab();

	/**
	 * @return All tabs in the same group.
	 */
	public abstract Tab[] getTabsInGroup();

	/**
	 * init method of bean configuration.
	 */
	public abstract void initialize();

	public abstract GUIManager getGuiManager();

	public abstract void setGuiManager(GUIManager guiManager);

	/**
	 * Bean setter.
	 * @param uri The view URI.
	 */
	public abstract void setViewUri(String uri);

	public abstract void setShowMenu(boolean showMenu);

	public abstract void setCreateContinuation(boolean createContinuation);

	public abstract void setTabName(String tabName);

	public abstract void setTabGroup(String tabGroup);

	public abstract void setCustomFlow(String customFlow);

	public abstract void setParameters(Properties params);

}