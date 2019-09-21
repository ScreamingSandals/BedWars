package misat11.bw.special;

import misat11.bw.Main;
import misat11.bw.api.Game;
import misat11.bw.api.RunningTeam;
import misat11.bw.api.Team;
import misat11.bw.game.GamePlayer;
import misat11.bw.utils.MiscUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static misat11.lib.lang.I18n.i18n;
import static misat11.lib.lang.I18n.i18nonly;

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
					MiscUtils.sendActionBarMessage(player, i18nonly("specials_tracker_target_found").replace("%target%", target.getDisplayName()).replace("%distance%", String.valueOf(distance)));
				} else {
					MiscUtils.sendActionBarMessage(player, i18nonly("specials_tracker_no_target_found"));
					player.setCompassTarget(game.getTeamOfPlayer(player).getTeamSpawn());
				}
			}
		}.runTask(Main.getInstance());
	}

	@Override
	public Player findTarget(Player player) {
		return MiscUtils.findTarget(game, player, Double.MAX_VALUE);
	}
}
