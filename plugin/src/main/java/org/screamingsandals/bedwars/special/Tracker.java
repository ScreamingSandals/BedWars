package org.screamingsandals.bedwars.special;

import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.utils.AdventureHelper;

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
                var wrapper = PlayerMapper.wrapPlayer(player);
                Player target = MiscUtils.findTarget(game, player, Double.MAX_VALUE);
                if (target != null) {
                    player.setCompassTarget(target.getLocation());

                    int distance = (int) player.getLocation().distance(target.getLocation());
                    MiscUtils.sendActionBarMessage(wrapper, Message.of(LangKeys.SPECIALS_TRACKER_TARGET_FOUND).placeholder("target", AdventureHelper.toComponent(target.getDisplayName())).placeholder("distance", distance));
                } else {
                    MiscUtils.sendActionBarMessage(wrapper, Message.of(LangKeys.SPECIALS_TRACKER_NO_TARGET_FOUND));
                    player.setCompassTarget(game.getTeamOfPlayer(player).getTeamSpawn());
                }
            }
        }.runTask(Main.getInstance().getPluginDescription().as(JavaPlugin.class));
    }
}
