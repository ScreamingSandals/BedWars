package misat11.bw.boss;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import misat11.lib.nms.NMSUtils;

public class XPBar implements misat11.bw.api.boss.XPBar {

	private boolean visible = false;
	private float progress = 0F;
	private int seconds = 0;
	private List<Player> players = new ArrayList<Player>();

	@Override
	public void addPlayer(Player player) {
		if (!players.contains(player)) {
			players.add(player);
			if (visible) {
				NMSUtils.fakeExp(player, progress, seconds);
			}
		}

	}

	@Override
	public void removePlayer(Player player) {
		if (!players.contains(player)) {
			players.remove(player);
			NMSUtils.fakeExp(player, player.getExp(), player.getLevel());
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
				NMSUtils.fakeExp(player, this.progress, seconds);
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
					NMSUtils.fakeExp(player, progress, seconds);
				}
			} else {
				for (Player player : players) {
					NMSUtils.fakeExp(player, player.getExp(), player.getLevel());
				}
			}
		}
		this.visible = visible;
	}

	@Override
	public void setSeconds(int seconds) {
		this.seconds = seconds;
		if (visible) {
			for (Player player : players) {
				NMSUtils.fakeExp(player, this.progress, seconds);
			}
		}
	}

	@Override
	public int getSeconds() {
		return this.seconds;
	}

}
