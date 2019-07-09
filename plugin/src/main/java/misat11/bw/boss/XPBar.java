package misat11.bw.boss;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import misat11.lib.nms.NMSUtils;

public class XPBar implements misat11.bw.api.boss.XPBar {

	private boolean visible = false;
	private float progress = 0F;
	private List<Player> players = new ArrayList<Player>();

	@Override
	public void addPlayer(Player player) {
		if (!players.contains(player)) {
			players.add(player);
			if (visible) {
				NMSUtils.fakeExp(player, progress);
			}
		}

	}

	@Override
	public void removePlayer(Player player) {
		if (!players.contains(player)) {
			players.remove(player);
			NMSUtils.fakeExp(player, player.getExp());
		}

	}

	@Override
	public void setProgress(double progress) {
		if (progress < 0) {
			progress = 0;
		} else if (progress > 1) {
			progress = 1;
		}
		this.progress = (float) progress;
		if (visible) {
			for (Player player : players) {
				NMSUtils.fakeExp(player, this.progress);
			}
		}
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
		if (this.visible != visible) {
			if (visible) {
				for (Player player : players) {
					NMSUtils.fakeExp(player, progress);
				}
			} else {
				for (Player player : players) {
					NMSUtils.fakeExp(player, player.getExp());
				}
			}
		}
		this.visible = visible;
	}

}
