package org.screamingsandals.bedwars.api.boss;

import java.util.List;

import org.screamingsandals.lib.utils.Wrapper;

/**
 * @author Bedwars Team
 *
 */
public interface StatusBar {
	
	/**
	 * @param player
	 */
	void addPlayer(Wrapper player);
	
	/**
	 * @param player
	 */
	void removePlayer(Wrapper player);
	
	/**
	 * @param progress
	 */
	void setProgress(float progress);
	
	/**
	 * @return list of all viewers
	 */
	List<Wrapper> getViewers();
	
	/**
	 * @return progress of status bar
	 */
	float getProgress();
	
	/**
	 * @return visibility of status bar
	 */
	boolean isVisible();
	
	/**
	 * @param visible
	 */
	void setVisible(boolean visible);
}
