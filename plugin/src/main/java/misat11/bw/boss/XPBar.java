package misat11.bw.boss;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class XPBar implements misat11.bw.api.boss.XPBar {
	
	private boolean visible = false;
	private double progress = 0;
	private List<Player> players = new ArrayList<Player>();

	@Override
	public void addPlayer(Player player) {
		if (!players.contains(player)) {
			players.add(player);
			// todo update xp bar for player
		}
		
	}

	@Override
	public void removePlayer(Player player) {
		if (!players.contains(player)) {
			players.remove(player);
			// todo update xp bar for player
		}
		
	}

	@Override
	public void setProgress(double progress) {
		if (progress < 0) {
			progress = 0;
		} else if (progress > 1) {
			progress = 1;
		}
		this.progress = progress;
		// todo update xp bar
	}

	@Override
	public List<Player> getViewers() {
		return players;
	}

	@Override
	public double getProgress() {
		return progress;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
		// todo update xp bar
	}

}
