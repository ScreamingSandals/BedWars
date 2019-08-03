package misat11.bw.special;

import misat11.bw.Main;
import misat11.bw.api.Game;
import misat11.bw.api.RunningTeam;
import misat11.bw.api.Team;
import misat11.bw.game.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static misat11.lib.lang.I18n.i18n;

public class Tracker extends SpecialItem implements misat11.bw.api.special.Tracker {
	private Game game;
	private Player player;

	public Tracker(Game game, Player player, Team team) {
		super(game, player, team);
		this.game = game;
		this.player = player;
	}

	@Override
	public void runTask() {
		game.registerSpecialItem(this);
		new BukkitRunnable() {

			@Override
			public void run() {
				Player target = findTarget(player);
				if (target != null) {
					player.setCompassTarget(target.getLocation());

					int distance = (int) player.getLocation().distance(target.getLocation());
					player.sendMessage(i18n("specials_tracker_target_found").replace("%target%", target.getDisplayName()).replace("%distance%", String.valueOf(distance)));
				} else {
					player.sendMessage(i18n("specials_tracker_no_target_found"));
					player.setCompassTarget(game.getTeamOfPlayer(player).getTeamSpawn());
				}
			}
		}.runTask(Main.getInstance());
	}

	@Override
	public Player findTarget(Player player) {
		return find(game, player);
	}
	
	public static Player find(Game game, Player player) {
		Player playerTarget = null;
		double maxDistance = Double.MAX_VALUE;

		RunningTeam team = game.getTeamOfPlayer(player);

		ArrayList<Player> foundTargets = new ArrayList<>(game.getConnectedPlayers());
		foundTargets.removeAll(team.getConnectedPlayers());


		for (Player p : foundTargets) {
			GamePlayer gamePlayer = Main.getPlayerGameProfile(p);
			if (player.getWorld() != p.getWorld()) {
				continue;
			}

			if (gamePlayer.isSpectator) {
				continue;
			}

			double realDistance = player.getLocation().distance(p.getLocation());
			if (realDistance < maxDistance) {
				playerTarget = p;
				maxDistance = realDistance;
			}
		}
		return playerTarget;
	}
}
