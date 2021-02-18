package org.screamingsandals.bedwars.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DelayFactory implements org.screamingsandals.bedwars.api.utils.DelayFactory {
    private int delay;
    private SpecialItem specialItem;
    private Player player;
    private Game game;
    private boolean delayActive;

    public DelayFactory(int delay, SpecialItem specialItem, Player player, Game game) {
        this.delay = delay;
        this.specialItem = specialItem;
        this.player = player;
        this.game = game;

        runDelay();
    }

    @Override
    public boolean getDelayActive() {
        return delayActive;
    }

    @Override
    public SpecialItem getSpecialItem() {
        return specialItem;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public int getRemainDelay() {
        return delay;
    }

    private void runDelay() {
        new BukkitRunnable() {
            public void run() {
                if (delay > 0) {
                    delayActive = true;
                    delay--;
                    if (delay == 0) {
                        delayActive = false;

                        this.cancel();
                        game.unregisterDelay(DelayFactory.this);
                    }
                }
            }
        }.runTaskTimer(Main.getInstance().getPluginDescription().as(JavaPlugin.class), 0L, 20L);
    }
}
