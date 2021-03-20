package org.screamingsandals.bedwars.game;

import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;

public class RespawnProtection extends BukkitRunnable {
    private Game game;
    private Player player;
    private int length;
    private boolean running = true;

    public RespawnProtection(Game game, Player player, int seconds) {
        this.game = game;
        this.player = player;
        this.length = seconds;
    }

    @Override
    public void run() {
    	if (!running) return;
        if (length > 0) {
            MiscUtils.sendActionBarMessage(PlayerMapper.wrapPlayer(player), Message.of(LangKeys.IN_GAME_RESPAWN_PROTECTION_REMAINING).placeholder("time", this.length));

        }
        if (length <= 0) {
            MiscUtils.sendActionBarMessage(PlayerMapper.wrapPlayer(player), Message.of(LangKeys.IN_GAME_RESPAWN_PROTECTION_END));
            game.removeProtectedPlayer(player);
            running = false;
        }
        length--;
    }

    public void runProtection() {
        runTaskTimerAsynchronously(Main.getInstance().getPluginDescription().as(JavaPlugin.class), 5L, 20L);
    }


}

