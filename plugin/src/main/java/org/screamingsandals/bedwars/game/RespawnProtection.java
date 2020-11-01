package org.screamingsandals.bedwars.game;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18nonly;

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
            MiscUtils.sendActionBarMessage(player, i18nonly("respawn_protection_remaining").replace("%time%", String.valueOf(this.length)));

        }
        if (length <= 0) {
            MiscUtils.sendActionBarMessage(player, i18nonly("respawn_protection_end"));
            game.removeProtectedPlayer(player);
            running = false;
        }
        length--;
    }

    public void runProtection() {
        runTaskTimerAsynchronously(Main.getInstance(), 5L, 20L);
    }


}

