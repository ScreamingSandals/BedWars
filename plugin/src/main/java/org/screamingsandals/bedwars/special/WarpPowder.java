package org.screamingsandals.bedwars.special;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.utils.PaperUtils;
import org.screamingsandals.bedwars.utils.SpawnEffects;

import static misat11.lib.lang.I18n.i18n;

public class WarpPowder extends SpecialItem implements org.screamingsandals.bedwars.api.special.WarpPowder {
    private BukkitTask teleportingTask = null;
    private int teleportingTime;

    private ItemStack item;

    public WarpPowder(Game game, Player player, Team team, ItemStack item, int teleportingTime) {
        super(game, player, team);
        this.item = item;
        this.teleportingTime = teleportingTime;
    }

    @Override
    public ItemStack getStack() {
        return item;
    }

    @Override
    public void cancelTeleport(boolean removeSpecial, boolean showMessage, boolean decrementStack) {
        try {
            teleportingTask.cancel();
        } catch (Exception ignored) {

        }

        if (removeSpecial) {
            game.unregisterSpecialItem(this);
        }

        if (showMessage) {
            player.sendMessage(i18n("specials_warp_powder_canceled"));
        }

        if (decrementStack) {
            item.setAmount(item.getAmount() - 1);

            player.updateInventory();
        }
    }

    @Override
    public void runTask() {
        game.registerSpecialItem(this);

        player.sendMessage(i18n("specials_warp_powder_started").replace("%time%", Double.toString(teleportingTime)));

        teleportingTask = new BukkitRunnable() {

            @Override
            public void run() {
                if (teleportingTime == 0) {
                    cancelTeleport(true, false, true);
                    PaperUtils.teleport(player, team.getTeamSpawn());
                } else {
                    SpawnEffects.spawnEffect(game, player, "game-effects.warppowdertick");
                    teleportingTime--;
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }
}
