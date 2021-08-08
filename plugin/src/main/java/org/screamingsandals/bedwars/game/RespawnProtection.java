package org.screamingsandals.bedwars.game;

import lombok.Data;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.TaskerTask;

@Data
public class RespawnProtection implements Runnable {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private final int seconds;
    private int length;
    private boolean running;
    private TaskerTask task;

    @Override
    public void run() {
    	if (!running) return;
        if (length > 0) {
            MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.IN_GAME_RESPAWN_PROTECTION_REMAINING).placeholder("time", this.length));

        }
        if (length <= 0) {
            MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.IN_GAME_RESPAWN_PROTECTION_END));
            game.removeProtectedPlayer(player);
            running = false;
        }
        length--;
    }

    public void runProtection() {
        if (!running) {
            running = true;
            this.task = Tasker.build(this)
                    .async()
                    .delay(5, TaskerTime.TICKS)
                    .repeat(20, TaskerTime.TICKS)
                    .start();
        }
    }
}

