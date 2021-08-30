package org.screamingsandals.bedwars.special;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.screamingsandals.bedwars.api.special.WarpPowder;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.SpawnEffects;
import org.screamingsandals.lib.entity.EntityItem;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.material.Item;
import org.screamingsandals.lib.material.builder.ItemFactory;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.TaskerTask;

@Getter
@EqualsAndHashCode(callSuper = true)
public class WarpPowderImpl extends SpecialItem implements WarpPowder<GameImpl, BedWarsPlayer, CurrentTeam, Item> {
    private final Item item;
    private TaskerTask teleportingTask;
    private int teleportingTime;

    public WarpPowderImpl(GameImpl game, BedWarsPlayer player, CurrentTeam team, Item item, int teleportingTime) {
        super(game, player, team);
        this.item = item;
        this.teleportingTime = teleportingTime;
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
            if (player.getPlayerInventory().as(Inventory.class).firstEmpty() == -1 && !player.getPlayerInventory().contains(item)) {
                EntityItem.dropItem(item, player.getLocation());
            } else {
                player.getPlayerInventory().addItem(item);
            }
            player.as(Player.class).updateInventory();
        }

        if (showMessage) {
            Message.of(LangKeys.SPECIALS_WARP_POWDER_CANCELED)
                    .prefixOrDefault(game.getCustomPrefixComponent())
                    .send(player);
        }
    }

    @Override
    public void runTask() {
        game.registerSpecialItem(this);

        Message.of(LangKeys.SPECIALS_WARP_POWDER_STARTED)
                .prefixOrDefault(game.getCustomPrefixComponent())
                .placeholder("time", teleportingTime)
                .send(player);

        item.setAmount(1);
        try {
            if (player.getPlayerInventory().getItemInOffHand().equals(item)) {
                player.getPlayerInventory().setItemInOffHand(ItemFactory.getAir());
            } else {
                player.getPlayerInventory().removeItem(item);
            }
        } catch (Throwable e) {
            player.getPlayerInventory().removeItem(item);
        }
        player.as(Player.class).updateInventory();

        teleportingTask = Tasker.build(() -> {
                    if (teleportingTime == 0) {
                        cancelTeleport(true, false);
                        player.teleport(team.getTeamSpawn());
                    } else {
                        SpawnEffects.spawnEffect(game, player, "game-effects.warppowdertick");
                        teleportingTime--;
                    }
                })
                .repeat(20, TaskerTime.TICKS)
                .start();
    }
}
