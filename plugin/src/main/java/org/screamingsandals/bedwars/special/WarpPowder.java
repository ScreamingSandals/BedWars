package org.screamingsandals.bedwars.special;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.bedwars.utils.SpawnEffects;
import org.screamingsandals.bedwars.lib.nms.entity.PlayerUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;

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
    public void cancelTeleport(boolean unregisterSpecial, boolean showMessage) {
        try {
            teleportingTask.cancel();
        } catch (Exception ignored) {
        }

        if (unregisterSpecial) {
            game.unregisterSpecialItem(this);
        } else {
            if (player.getInventory().firstEmpty() == -1 && !player.getInventory().contains(item)) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            } else {
                player.getInventory().addItem(item);
            }
            player.updateInventory();
        }

        if (showMessage) {
            Message.of(LangKeys.SPECIALS_WARP_POWDER_CANCELED)
                    .prefixOrDefault(((org.screamingsandals.bedwars.game.Game) game).getCustomPrefixComponent())
                    .send(PlayerMapper.wrapPlayer(player));
        }
    }

    @Override
    public void runTask() {
        game.registerSpecialItem(this);

        Message.of(LangKeys.SPECIALS_WARP_POWDER_STARTED)
                .prefixOrDefault(((org.screamingsandals.bedwars.game.Game) game).getCustomPrefixComponent())
                .placeholder("time", teleportingTime)
                .send(PlayerMapper.wrapPlayer(player));

        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            try {
                if (player.getInventory().getItemInOffHand().equals(item)) {
                    player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                } else {
                    player.getInventory().remove(item);
                }
            } catch (Throwable e) {
                player.getInventory().remove(item);
            }
        }
        player.updateInventory();

        teleportingTask = new BukkitRunnable() {

            @Override
            public void run() {
                if (teleportingTime == 0) {
                    cancelTeleport(true, false);
                    PlayerUtils.teleportPlayer(player, team.getTeamSpawn());
                } else {
                    SpawnEffects.spawnEffect((org.screamingsandals.bedwars.game.Game) game, PlayerManager.getInstance().getPlayer(player.getUniqueId()).orElseThrow(), "game-effects.warppowdertick");
                    teleportingTime--;
                }
            }
        }.runTaskTimer(Main.getInstance().getPluginDescription().as(JavaPlugin.class), 0L, 20L);
    }
}
