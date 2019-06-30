package misat11.bw.legacy;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.confuser.barapi.BarAPI;

public class BossBar_1_8 {

	public static boolean isPluginForLegacyBossBarEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("BarAPI");
	}

	private boolean visible = false;
	private List<Player> players = new ArrayList<>();
	private String message = "";
	private double progress = 0;

	public BossBar_1_8() {

	}

	public void setVisible(boolean visible) {
		if (this.visible != visible) {
			if (visible) {
				for (Player player : players) {
					show(player);
				}
			} else {
				for (Player player : players) {
					hide(player);
				}
			}
			this.visible = visible;
		}
	}

	public void addPlayer(Player player) {
		if (!players.contains(player)) {
			players.add(player);

			if (visible) {
				show(player);
			}
		}
	}

	public void removePlayer(Player player) {
		if (players.contains(player)) {
			players.remove(player);

			if (BarAPI.hasBar(player)) {
				hide(player);
			}
		}
	}

	public void setProgress(double progress) {
		this.progress = progress;
		if (visible) {
			for (Player p : players) {
				show(p);
			}
		}
	}

	public void setMessage(String message) {
		this.message = message;
		if (visible) {
			for (Player p : players) {
				show(p);
			}
		}
	}

	private void show(Player player) {
		BarAPI.setMessage(player, message, (float) progress * 100);
	}

	private void hide(Player player) {
		BarAPI.removeBar(player);
	}
}
