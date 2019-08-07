package misat11.bw.game;

import misat11.bw.Main;
import misat11.bw.utils.MiscUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static misat11.lib.lang.I18n.i18n;

public class RespawnProtection extends BukkitRunnable {
    private Game game;
    private Player player;
    private int length;

    public RespawnProtection(Game game, Player player, int seconds) {
        this.game = game;
        this.player = player;
        this.length = seconds;
    }

    @Override
    public void run() {
        if (length > 0) {
            MiscUtils.sendActionBarMessage(player, i18n("respawn_protection_remaining").replace("%time%", String.valueOf(this.length)));

        }
        if (length <= 0) {
            MiscUtils.sendActionBarMessage(player, i18n("respawn_protection_end"));
            game.removeProtectedPlayer(player);
        }
        length--;
    }

    public void runProtection() {
        runTaskTimerAsynchronously(Main.getInstance(), 5L, 20L);
    }


}

