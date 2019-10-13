package org.screamingsandals.bedwars.special;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static misat11.lib.lang.I18n.i18nonly;

public class Tracker extends SpecialItem implements org.screamingsandals.bedwars.api.special.Tracker {
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
                Player target = MiscUtils.findTarget(game, player, Double.MAX_VALUE);
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
}
