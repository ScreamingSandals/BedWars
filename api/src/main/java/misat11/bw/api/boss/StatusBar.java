package misat11.bw.api.boss;

import java.util.List;

import org.bukkit.entity.Player;

/**
 * @author Bedwars Team
 *
 */
public interface StatusBar {
	
	/**
	 * @param player
	 */
	public void addPlayer(Player player);
	
	/**
	 * @param player
	 */
	public void removePlayer(Player player);
	
	/**
	 * @param progress
	 */
	public void setProgress(double progress);
	
	/**
	 * @return list of all viewers
	 */
	public List<Player> getViewers();
	
	/**
	 * @return progress of status bar
	 */
	public double getProgress();
	
	/**
	 * @return visibility of status bar
	 */
	public boolean isVisible();
	
	/**
	 * @param visible
	 */
	public void setVisible(boolean visible);
}
