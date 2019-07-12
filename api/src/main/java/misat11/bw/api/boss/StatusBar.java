package misat11.bw.api.boss;

import java.util.List;

import org.bukkit.entity.Player;

public interface StatusBar {
	
	public void addPlayer(Player player);
	
	public void removePlayer(Player player);
	
	public void setProgress(double progress);
	
	public List<Player> getViewers();
	
	public double getProgress();
	
	public boolean isVisible();
	
	public void setVisible(boolean visible);
}
